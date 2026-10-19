#!/usr/bin/env python3
"""Creates a LaTeX table comparing experiment configurations.

usage: make_table.py OUT.tex LABEL CAPTION REF_RESULT:Name RESULT:Name ... [options]

Each RESULT is a CSV file name (without .csv) in the results directory, as written
by program.Benchmark (one line per run). Cells show the mean dead space [%] with
the population standard deviation as a subscript; bold = significantly better and
a triangle = significantly worse than the first (reference) column (two-sided
Mann-Whitney U test, p < 0.05, on the runs with seeds present in both).

options:
  --results=DIR     directory with the CSV files (default: experiments/results)
  --bench=a,b,...   benchmarks (rows) to include, in this order
  --seeds=1,2,...   only use runs with these seeds
  --score           add a row with the score (geometric mean of mean / best mean)
  --best            add the best run in parentheses
  --size=footnotesize  font size of the table (default: small)
  --groups=2:A,3:B  grouped header above the column names (counts of columns)
  --tabcolsep=4pt   space between the columns (default: LaTeX default)
"""
import csv
import math
import os
import statistics as st
import sys
from collections import defaultdict

from scipy.stats import mannwhitneyu

ORDER = ['MCNCapte', 'MCNCxerox', 'MCNChp', 'MCNCami33', 'MCNCami49', 'n100', 'n200', 'n300',
         'n30b', 'n50b', 'n100b', 'n100c', 'n200b', 'n200c']


def load(directory, label, seeds):
    data = defaultdict(dict)
    for row in csv.reader(open(os.path.join(directory, label + '.csv'))):
        if len(row) < 12:
            continue
        if row[11] != 'ok':
            raise SystemExit('invalid floorplan in %s: %s' % (label, row))
        seed = int(row[1])
        if seeds and seed not in seeds:
            continue
        data[row[0]][seed] = float(row[2])
    return data


def name(benchmark):
    return benchmark.replace('MCNC', '')


def main():
    args = [a for a in sys.argv[1:] if not a.startswith('--')]
    opts = dict(a[2:].split('=', 1) if '=' in a else (a[2:], '1') for a in sys.argv[1:] if a.startswith('--'))
    out, label, caption = args[0], args[1], args[2]
    columns = [c.split(':', 1) for c in args[3:]]
    directory = opts.get('results', os.path.join(os.path.dirname(os.path.abspath(__file__)), 'results'))
    seeds = set(int(s) for s in opts['seeds'].split(',')) if 'seeds' in opts else None
    data = [(n, load(directory, f, seeds)) for f, n in columns]
    benches = opts['bench'].split(',') if 'bench' in opts else [b for b in ORDER if all(b in d for _, d in data)]
    best_mean = {b: min(st.mean(d[b].values()) for _, d in data) for b in benches}

    lines = []
    for b in benches:
        ref = data[0][1][b]
        cells = []
        for i, (_, d) in enumerate(data):
            v = d[b]
            values = list(v.values())
            cell = '%.2f_{\\pm %.2f}' % (st.mean(values), st.pstdev(values))
            if 'best' in opts:
                cell += '\\ (%.2f)' % min(values)
            marker = ''
            if i > 0:
                common = sorted(set(ref) & set(v))
                x = [ref[s] for s in common]
                y = [v[s] for s in common]
                if len(common) >= 5 and x != y and mannwhitneyu(x, y).pvalue < 0.05:
                    if st.mean(y) < st.mean(x):
                        cell = '\\mathbf{' + cell + '}'
                    else:
                        marker = '^{\\blacktriangle}'
            cells.append('$' + cell + marker + '$')
        lines.append('%s & %s \\\\' % (name(b), ' & '.join(cells)))
    if 'score' in opts:
        scores = []
        for _, d in data:
            g = math.exp(st.mean([math.log(st.mean(d[b].values()) / best_mean[b]) for b in benches]))
            scores.append('%.3f' % g)
        lines.append('\\midrule')
        lines.append('score & %s \\\\' % ' & '.join(scores))

    header = ' & '.join(n for n, _ in data)
    table = '\\begin{table}\n\\centering\n\\%s\n' % opts.get('size', 'small')
    if 'tabcolsep' in opts:
        table += '\\setlength{\\tabcolsep}{%s}\n' % opts['tabcolsep']
    table += '\\begin{tabular}{@{}l%s@{}}\n\\toprule\n' % ('c' * len(data))
    if 'groups' in opts:
        groups = [g.split(':', 1) for g in opts['groups'].split(',')]
        table += ' & ' + ' & '.join('\\multicolumn{%s}{c}{%s}' % (n, t) for n, t in groups) + ' \\\\\n'
    table += 'Benchmark & %s \\\\\n\\midrule\n' % header
    table += '\n'.join(lines)
    table += '\n\\bottomrule\n\\end{tabular}\n\\caption{%s}\n\\label{%s}\n\\end{table}\n' % (caption, label)
    open(out, 'w').write(table)
    print(table)


if __name__ == '__main__':
    main()
