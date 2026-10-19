#!/usr/bin/env python3
"""Creates thesis/tables/gaanalysis.tex from the GA instrumentation output (STAT lines)."""
import sys
NAMES = {'MCNCapte': 'apte', 'MCNCxerox': 'xerox', 'MCNChp': 'hp', 'MCNCami33': 'ami33', 'MCNCami49': 'ami49'}
rows = []
for line in open(sys.argv[1]):
    if not line.startswith('STAT,'):
        continue
    f = line.strip().split(',')
    b, iters, children, dup, ident, same, imp, eq, worse, gmean, gmed, epi = f[1:13]
    epi = float(epi)
    epi_s = '%.0f\\,k' % (epi / 1000) if epi < 1e6 else '%.1f\\,M' % (epi / 1e6)
    if float(imp) == 0:
        epi_s = '--'
    rows.append('%s & %s & %s & %s & %s & %s / %s / %s & %s / %s & %s \\\\' % (
        NAMES.get(b, b), iters, dup, ident, same, imp, eq, worse, gmean, gmed, epi_s))
out = r'''\begin{table}
\centering
\footnotesize
\setlength{\tabcolsep}{2.5pt}
\begin{tabular}{@{}lrrrrcrr@{}}
\toprule
 & & \multicolumn{3}{c}{children [\%]} & iterations [\%] & best found & evaluations \\
Benchmark & iterations & duplicate & unchanged & as parent & impr.\ / equal / worse & in generation & per impr. \\
\midrule
''' + '\n'.join(rows) + r'''
\bottomrule
\end{tabular}
\caption{Behaviour of the genetic algorithm of POEMS ($G = 1000$, $N = 3$, $S = 50$, 20\,s runs, seed 1). Children: duplicates of an individual of the population (same enabled actions), children that do not change the prototype, and children with the same fitness as a parent. Iterations: the best sequence of the iteration improves the prototype, has the same fitness, or is worse. Best found in generation: mean / median generation of the last improvement of the best individual of an iteration.}
\label{tab:gaanalysis}
\end{table}
'''
open(sys.argv[2], 'w').write(out)
print(out)
