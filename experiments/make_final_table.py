#!/usr/bin/env python3
"""Creates the LaTeX table of the final comparison.

usage: make_final_table.py OUT.tex LABEL CAPTION RESULT:Name RESULT:Name ... [--results=DIR] [--bench=a,b]

For every benchmark (row) and algorithm (column), the table shows the mean
and the population standard deviation of the dead space [%] with the best run
in parentheses. Every column except the first is compared with the column to
its left (--compare=first: with the first column): bold = significantly better,
triangle = significantly worse (two-sided Mann-Whitney U test, p < 0.05). The
last columns show the Vargha-Delaney effect size A12 of the last algorithm
against each of the other ones (--compare=first: of each other algorithm
against the first one): the probability that a run of the one algorithm ends
with less dead space than a run of the other one (ties count one half).
"""
import csv
import os
import statistics as st
import sys
from collections import defaultdict

from scipy.stats import mannwhitneyu

ORDER = ['MCNCapte', 'MCNCxerox', 'MCNChp', 'MCNCami33', 'MCNCami49', 'n100', 'n200', 'n300',
         'n30b', 'n50b', 'n100b', 'n100c', 'n200b', 'n200c']


def load(directory, label):
    data = defaultdict(list)
    for row in csv.reader(open(os.path.join(directory, label + '.csv'))):
        if len(row) < 12:
            continue
        if row[11] != 'ok':
            raise SystemExit('invalid floorplan in %s: %s' % (label, row))
        data[row[0]].append(float(row[2]))
    return data


def a12(first, second):
    """Probability that a value of 'first' is smaller than a value of 'second'."""
    wins = sum(1.0 if x < y else 0.5 if x == y else 0.0 for x in first for y in second)
    return wins / (len(first) * len(second))


def main():
    args = [a for a in sys.argv[1:] if not a.startswith('--')]
    opts = dict(a[2:].split('=', 1) for a in sys.argv[1:] if a.startswith('--'))
    out, label, caption = args[0], args[1], args[2]
    directory = opts.get('results', os.path.join(os.path.dirname(os.path.abspath(__file__)), 'results'))
    columns = [(n, load(directory, f)) for f, n in (c.split(':', 1) for c in args[3:])]
    benches = opts['bench'].split(',') if 'bench' in opts else [b for b in ORDER if all(b in d for _, d in columns)]
    first = opts.get('compare') == 'first'
    # pairs (algorithm, opponent) of the effect sizes
    pairs = [(i, 0) for i in range(1, len(columns))] if first else \
        [(len(columns) - 1, i) for i in range(len(columns) - 1)]

    rows = []
    for b in benches:
        cells = []
        for i, (_, d) in enumerate(columns):
            v = d[b]
            cell = '%.2f_{\\pm %.2f}\\ (%.2f)' % (st.mean(v), st.pstdev(v), min(v))
            marker = ''
            if i > 0:
                ref = columns[0 if first else i - 1][1][b]
                if v != ref and mannwhitneyu(v, ref).pvalue < 0.05:
                    if st.mean(v) < st.mean(ref):
                        cell = '\\mathbf{' + cell + '}'
                    else:
                        marker = '^{\\blacktriangle}'
            cells.append('$' + cell + marker + '$')
        for i, j in pairs:
            cells.append('%.2f' % a12(columns[i][1][b], columns[j][1][b]))
        rows.append('%s & %s \\\\' % (b.replace('MCNC', ''), ' & '.join(cells)))

    spec = 'l' + 'c' * len(columns) + 'r' * len(pairs)
    if first:
        title = '$\\hat{A}_{12}$ vs. %s' % columns[0][0]
        names = [columns[i][0] for i, _ in pairs]
    else:
        title = '$\\hat{A}_{12}$ of %s vs.' % columns[-1][0]
        names = [columns[j][0] for _, j in pairs]
    header1 = ' & ' * len(columns) + ' & \\multicolumn{%d}{c}{%s} \\\\\n' % (len(pairs), title)
    header2 = 'Benchmark & ' + ' & '.join(n for n, _ in columns) + ' & ' + ' & '.join(names) + ' \\\\\n'
    table = '\\begin{table}\n\\centering\n\\%s\n' % opts.get('size', 'small')
    if 'tabcolsep' in opts:
        table += '\\setlength{\\tabcolsep}{%s}\n' % opts['tabcolsep']
    table += '\\begin{tabular}{@{}%s@{}}\n\\toprule\n' % spec
    table += header1 + header2 + '\\midrule\n'
    table += '\n'.join(rows) + '\n\\bottomrule\n\\end{tabular}\n'
    table += '\\caption{%s}\n\\label{%s}\n\\end{table}\n' % (caption, label)
    open(out, 'w').write(table)
    print(table)


if __name__ == '__main__':
    main()
