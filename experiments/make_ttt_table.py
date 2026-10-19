#!/usr/bin/env python3
"""Creates a LaTeX table with the time each algorithm needs to reach a target.

usage: make_ttt_table.py OUT.tex LABEL CAPTION REF_RESULT REF_NAME TRACE_DIR:Name ... [--results=DIR]

The target of a benchmark is the median final dead space of the reference runs
(results CSV). For every traced algorithm, the table shows the median over the
runs of the time at which the best floorplan first reached the target (a run
that never reaches it counts as infinite).
"""
import csv
import glob
import os
import statistics as st
import sys
from collections import defaultdict

USED = {'MCNCapte': 46561628, 'MCNCxerox': 19350296, 'MCNChp': 8830584, 'MCNCami33': 1156449,
        'MCNCami49': 35445424, 'n100': 179501, 'n200': 175696, 'n300': 273170, 'n30b': 197781,
        'n50b': 203053, 'n100b': 160126, 'n100c': 171966, 'n200b': 174593, 'n200c': 170129}
ORDER = ['MCNCapte', 'MCNCxerox', 'MCNChp', 'MCNCami33', 'MCNCami49', 'n100', 'n200', 'n300',
         'n30b', 'n50b', 'n100b', 'n100c', 'n200b', 'n200c']

args = [a for a in sys.argv[1:] if not a.startswith('--')]
opts = dict(a[2:].split('=', 1) for a in sys.argv[1:] if a.startswith('--'))
directory = opts.get('results', os.path.join(os.path.dirname(os.path.abspath(__file__)), 'results'))
out, label, caption, ref, ref_name = args[:5]
traces = [a.split(':', 1) for a in args[5:]]
final = defaultdict(list)
for row in csv.reader(open(os.path.join(directory, ref + '.csv'))):
    final[row[0]].append(float(row[2]))
rows = []
for b in ORDER:
    if b not in final:
        continue
    target = st.median(final[b])
    cells = []
    for d, _ in traces:
        times = []
        for f in sorted(glob.glob(os.path.join(d, b + '_*.csv'))):
            hit = float('inf')
            for r in csv.reader(open(f)):
                # the results files round the dead space to four decimals
                if r and round(100.0 * int(r[1]) / USED[b], 4) <= target + 1e-9:
                    hit = int(r[0]) / 1000.0
                    break
            times.append(hit)
        m = st.median(times) if times else float('inf')
        cells.append('%.1f' % m if m != float('inf') else '--')
    rows.append('%s & %.2f & %s \\\\' % (b.replace('MCNC', ''), target, ' & '.join(cells)))
table = '\\begin{table}\n\\centering\n\\small\n\\begin{tabular}{@{}lr%s@{}}\n\\toprule\n' % ('r' * len(traces))
table += 'Benchmark & target [\\%%] & %s \\\\\n\\midrule\n' % ' & '.join(n for _, n in traces)
table += '\n'.join(rows) + '\n\\bottomrule\n\\end{tabular}\n\\caption{%s}\n\\label{%s}\n\\end{table}\n' % (caption, label)
open(out, 'w').write(table)
print(table)
