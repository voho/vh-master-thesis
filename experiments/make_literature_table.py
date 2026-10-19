#!/usr/bin/env python3
"""Creates the LaTeX table comparing LA-POEMS with the original edition and the literature.

usage: make_literature_table.py OUT.tex LABEL CAPTION SHORT_RESULT LONG_RESULT [--results=DIR]

SHORT_RESULT and LONG_RESULT are results files (without .csv) of LA-POEMS with
a short and a long time limit; the table shows their mean dead space [%] with
the best run in parentheses. The other columns are published results:

- the original edition of this thesis (2011), the longest configuration
  (I = 20000, G = 1000, N = 3, S = 50): mean unused area of 10 runs, from its
  Tables 5.3 and 5.4, converted to dead space; time on a 2 GHz computer;
- CompaSS [Chan, Markov 2004], results for the hard-block benchmarks published
  at vlsicad.eecs.umich.edu/BK/CompaSS/results/{mcnc,gsrc}_txt.html; time on a
  2.8 GHz Pentium 4;
- B*-Tree with simulated annealing [Chang et al., DAC 2000, Table 2]: area in
  mm^2 rounded to two decimals, converted to dead space with the module areas
  of the benchmark files; time on a 200 MHz Sun Ultra-I.
"""
import csv
import os
import statistics as st
import sys
from collections import defaultdict

USED = {'MCNCapte': 46561628, 'MCNCxerox': 19350296, 'MCNChp': 8830584, 'MCNCami33': 1156449,
        'MCNCami49': 35445424, 'n100': 179501, 'n200': 175696, 'n300': 273170}
ORDER = ['MCNCapte', 'MCNCxerox', 'MCNChp', 'MCNCami33', 'MCNCami49', 'n100', 'n200', 'n300']

# original edition, I = 20000: (mean unused area, time [ms])
ORIGINAL = {'MCNCapte': (363220, 771397), 'MCNCxerox': (536545, 723015), 'MCNChp': (276830, 800229),
            'MCNCami33': (26445, 1276712), 'MCNCami49': (906892, 1698927), 'n100': (5254, 3145128),
            'n200': (7590, 6067438), 'n300': (12929, 9199609)}
# CompaSS: (dead space [%], time [s])
COMPASS = {'MCNCapte': (0.78, 0.00), 'MCNCxerox': (3.91, 0.26), 'MCNChp': (2.28, 0.81),
           'MCNCami33': (4.94, 6.19), 'MCNCami49': (4.87, 6.34), 'n100': (7.32, 4.17),
           'n200': (6.48, 10.43), 'n300': (6.80, 6.87)}
# B*-Tree with simulated annealing: (area [mm^2], time [s])
BTREE = {'MCNCapte': (46.92, 7), 'MCNCxerox': (19.83, 25), 'MCNChp': (8.95, 55),
         'MCNCami33': (1.27, 3417), 'MCNCami49': (36.80, 4752)}


def load(directory, label):
    data = defaultdict(list)
    path = os.path.join(directory, label + '.csv')
    if not os.path.exists(path):
        return data
    for row in csv.reader(open(path)):
        if len(row) < 12:
            continue
        if row[11] != 'ok':
            raise SystemExit('invalid floorplan in %s: %s' % (label, row))
        data[row[0]].append(float(row[2]))
    return data


def cell(values):
    if not values:
        return '--'
    return '%.2f (%.2f)' % (st.mean(values), min(values))




def main():
    args = [a for a in sys.argv[1:] if not a.startswith('--')]
    opts = dict(a[2:].split('=', 1) for a in sys.argv[1:] if a.startswith('--'))
    out, label, caption, short, long_ = args[:5]
    directory = opts.get('results', os.path.join(os.path.dirname(os.path.abspath(__file__)), 'results'))
    short_data, long_data = load(directory, short), load(directory, long_)

    rows = []
    for b in ORDER:
        unused, ms = ORIGINAL[b]
        original = '%.2f & %.0f' % (100.0 * unused / USED[b], ms / 1000.0)
        compass = '%.2f & %.2f' % COMPASS[b]
        if b in BTREE:
            area, t = BTREE[b]
            btree = '%.2f & %d' % (100.0 * (area * 1e6 / USED[b] - 1.0), t)
        else:
            btree = '-- & --'
        rows.append('%s & %s & %s & %s & %s & %s \\\\' % (
            b.replace('MCNC', ''), cell(short_data[b]), cell(long_data[b]), original, compass, btree))

    table = ('\\begin{table}\n\\centering\n\\small\n\\begin{tabular}{@{}lcccrcrcr@{}}\n\\toprule\n'
             ' & \\multicolumn{2}{c}{LA-POEMS} & \\multicolumn{2}{c}{original edition} & '
             '\\multicolumn{2}{c}{CompaSS \\cite{compass}} & \\multicolumn{2}{c}{B*-Tree SA \\cite{btree}} \\\\\n'
             'Benchmark & 60\\,s & 300\\,s & [\\%] & time [s] & [\\%] & time [s] & [\\%] & time [s] \\\\\n\\midrule\n')
    table += '\n'.join(rows)
    table += '\n\\bottomrule\n\\end{tabular}\n\\caption{%s}\n\\label{%s}\n\\end{table}\n' % (caption, label)
    open(out, 'w').write(table)
    print(table)


if __name__ == '__main__':
    main()
