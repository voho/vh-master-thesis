#!/usr/bin/env python3
"""Creates a LaTeX table of evaluation speeds (thousands of evaluations per second).

usage: make_speed_table.py OUT.tex LABEL CAPTION RESULT:Name ... [--results=DIR]
The speed of a run is its number of evaluations divided by its time; the table
shows the mean over the runs of each benchmark and the speed-up of each column
relative to the first one.
"""
import csv
import os
import statistics as st
import sys
from collections import defaultdict

ORDER = ['MCNCapte', 'MCNCxerox', 'MCNChp', 'MCNCami33', 'MCNCami49', 'n100', 'n200', 'n300']


def speeds(directory, label):
    result = defaultdict(list)
    for row in csv.reader(open(os.path.join(directory, label + '.csv'))):
        if len(row) >= 12:
            result[row[0]].append(int(row[8]) / max(1, int(row[10])))
    return {b: st.mean(v) for b, v in result.items()}


args = [a for a in sys.argv[1:] if not a.startswith('--')]
opts = dict(a[2:].split('=', 1) for a in sys.argv[1:] if a.startswith('--'))
directory = opts.get('results', os.path.join(os.path.dirname(os.path.abspath(__file__)), 'results'))
out, label, caption = args[0], args[1], args[2]
cols = [(n, speeds(directory, f)) for f, n in (c.split(':', 1) for c in args[3:])]
rows = []
for b in ORDER:
    if not all(b in s for _, s in cols):
        continue
    base = cols[0][1][b]
    cells = ['%.0f' % base]
    for _, s in cols[1:]:
        cells.append('%.0f \\ (%.1f$\\times$)' % (s[b], s[b] / base))
    rows.append('%s & %s \\\\' % (b.replace('MCNC', ''), ' & '.join(cells)))
table = '\\begin{table}\n\\centering\n\\small\n\\begin{tabular}{@{}l%s@{}}\n\\toprule\n' % ('r' * len(cols))
table += 'Benchmark & %s \\\\\n\\midrule\n' % ' & '.join(n for n, _ in cols)
table += '\n'.join(rows) + '\n\\bottomrule\n\\end{tabular}\n\\caption{%s}\n\\label{%s}\n\\end{table}\n' % (caption, label)
open(out, 'w').write(table)
print(table)
