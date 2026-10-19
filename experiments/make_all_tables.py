#!/usr/bin/env python3
"""Creates all tables of Chapter 5 of the thesis from the results.

usage: make_all_tables.py [RESULTS_DIR]   (default: experiments/results)
Tables whose results are missing are skipped with a message.
"""
import os
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
RESULTS = sys.argv[1] if len(sys.argv) > 1 else os.path.join(HERE, 'results')
TRACES = os.path.join(RESULTS, '..', 'traces')
TABLES = os.path.join(HERE, '..', 'thesis', 'tables')
SCREEN = 'SEEDS'  # marker only

MAIN = 'MCNCapte,MCNCxerox,MCNChp,MCNCami33,MCNCami49,n100,n200,n300'
# the screening run of the final configuration of LA-POEMS
LAPOEMS_SCREEN = 'L21_sa_1e-2_k2_local50'


def exists(*labels):
    missing = [l for l in labels if not os.path.exists(os.path.join(RESULTS, l + '.csv'))]
    if missing:
        print('skipping, missing results:', ', '.join(missing))
    return not missing


def table(out, label, caption, columns, *options):
    """columns: list of (result label, column name)"""
    if not exists(*[c for c, _ in columns]):
        return
    args = [sys.executable, os.path.join(HERE, 'make_table.py'), os.path.join(TABLES, out), label, caption]
    args += ['%s:%s' % c for c in columns]
    args += ['--results=' + RESULTS] + list(options)
    subprocess.run(args, check=True, stdout=subprocess.DEVNULL)
    print('created', out)


def final_table(out, label, caption, columns, *options):
    if not exists(*[c for c, _ in columns]):
        return
    args = [sys.executable, os.path.join(HERE, 'make_final_table.py'), os.path.join(TABLES, out), label, caption]
    args += ['%s:%s' % c for c in columns]
    args += ['--results=' + RESULTS] + list(options)
    subprocess.run(args, check=True, stdout=subprocess.DEVNULL)
    print('created', out)


def speed_table(out, label, caption, columns):
    if not exists(*[c for c, _ in columns]):
        return
    args = [sys.executable, os.path.join(HERE, 'make_speed_table.py'), os.path.join(TABLES, out), label, caption]
    args += ['%s:%s' % c for c in columns] + ['--results=' + RESULTS]
    subprocess.run(args, check=True, stdout=subprocess.DEVNULL)
    print('created', out)


def ttt_table(out, label, caption, reference, reference_name, traces):
    """traces: list of (trace directory name, column name)"""
    if not exists(reference):
        return
    dirs = [os.path.join(TRACES, d) for d, _ in traces]
    if not all(os.path.isdir(d) and os.listdir(d) for d in dirs):
        print('skipping, missing traces:', ', '.join(d for d, _ in traces))
        return
    args = [sys.executable, os.path.join(HERE, 'make_ttt_table.py'), os.path.join(TABLES, out), label, caption,
            reference, reference_name] + ['%s:%s' % (d, n) for d, (_, n) in zip(dirs, traces)]
    subprocess.run(args + ['--results=' + RESULTS], check=True, stdout=subprocess.DEVNULL)
    print('created', out)


def literature_table(out, label, caption, short, long_):
    if not exists(short):
        return
    args = [sys.executable, os.path.join(HERE, 'make_literature_table.py'), os.path.join(TABLES, out), label,
            caption, short, long_, '--results=' + RESULTS]
    subprocess.run(args, check=True, stdout=subprocess.DEVNULL)
    print('created', out)


def rotation_table():
    path = os.path.join(RESULTS, 'rotation_effect.txt')
    if not os.path.exists(path):
        print('skipping, missing rotation_effect.txt')
        return
    rows = {}
    for line in open(path):
        f = line.strip().split(',')
        rows[(f[0], f[1])] = f
    names = {'MCNCxerox': 'xerox', 'MCNCami33': 'ami33', 'n100': 'n100', 'n300': 'n300'}
    out = ('\\begin{table}\n\\centering\n\\small\n\\begin{tabular}{@{}lcccc@{}}\n\\toprule\n'
           ' & \\multicolumn{2}{c}{iterations improving / equal / worse [\\%]} & '
           '\\multicolumn{2}{c}{generation of the best (mean)} \\\\\n'
           'Benchmark & rotation ignored & corrected & rotation ignored & corrected \\\\\n\\midrule\n')
    for b in ['MCNCxerox', 'MCNCami33', 'n100', 'n300']:
        a, c = rows[('NOROT', b)], rows[('FIXED', b)]
        out += '%s & %s / %s / %s & %s / %s / %s & %s & %s \\\\\n' % (
            names[b], a[7], a[8], a[9], c[7], c[8], c[9], a[10], c[10])
    out += ('\\bottomrule\n\\end{tabular}\n\\caption{Outcome of the iterations of POEMS with the original identity '
            'check, which ignores the orientation of the modules, and with the corrected one (20\\,s, seed 1)}\n'
            '\\label{tab:rotation}\n\\end{table}\n')
    open(os.path.join(TABLES, 'rq2_rotation.tex'), 'w').write(out)
    print('created rq2_rotation.tex')


def main():
    os.makedirs(TABLES, exist_ok=True)

    # RQ1
    table('rq1_quality.tex', 'tab:rq1quality',
          'Dead space [\\%] of the original implementation and of the implementation optimised without '
          'changing the results (same algorithm and seeds, 30\\,s, 8 seeds)',
          [('E00_base_30s', 'Original'), ('E01_perf_30s', 'Optimised')], '--best', '--bench=' + MAIN)

    speed_table('speed.tex', 'tab:speed',
                'Evaluations per second (thousands) of the original implementation of POEMS, the optimised '
                'implementation, the corrected one, the corrected one with the array evaluation, and LA-POEMS, '
                'whose evaluations stop early when the candidate is rejected (mean of the 30\\,s runs; speed-up '
                'relative to the original implementation in parentheses)',
                [('E00_base_30s', 'Original'), ('E01_perf_30s', 'Optimised'), ('E02_fix_30s', 'Corrected'),
                 ('E08_fast_G1000', 'Array'), (LAPOEMS_SCREEN, 'LA-POEMS')])

    # RQ2
    table('rq2_fixes.tex', 'tab:rq2fixes',
          'Dead space [\\%] before and after the corrections of Section~\\ref{sec:defects} (30\\,s, 8 seeds)',
          [('E01_perf_30s', 'Before corrections'), ('E02_fix_30s', 'After corrections')], '--best', '--bench=' + MAIN)
    rotation_table()
    table('rq2_ga.tex', 'tab:rq2ga',
          'Modifications of the genetic algorithm of POEMS, each applied alone to the reference configuration '
          '(array evaluation, $G = 1000$, $N = 3$, $S = 50$): fewer generations per iteration, discarding duplicate '
          'children, the move node action, ending an iteration after 100 generations without improvement or at the '
          'first improvement of the prototype, and late acceptance of prototypes with a history of 20 iterations '
          '(dead space [\\%], 30\\,s, seeds 1--6)',
          [('E08_fast_G1000', 'reference'), ('E09_fast_G300', '$G = 300$'), ('E10_fast_G100', '$G = 100$'),
           ('E11_fast_dedupe', 'no dupl.'), ('E12_fast_move', 'move'), ('E13_fast_pat100', 'patience'),
           ('E15_fast_first', 'first'), ('E14_fast_lahc20', 'LA 20')],
          '--score', '--size=footnotesize', '--tabcolsep=4pt')

    # RQ3
    table('rq3_acceptance.tex', 'tab:rq3acceptance',
          'Acceptance criteria of LA-POEMS: late acceptance with a fixed history length $L$ or with the automatic '
          'length (ratio $C$), and simulated annealing with the initial temperature $T_0$ relative to the area of '
          'the modules (final temperature $10^{-6}$, or $10^{-5}$ for $T_0 = 10^{-2}$); steps of at most three '
          'actions (dead space [\\%], 30\\,s, seeds 1--6)',
          [('L01_h1000_k3', '1000'), ('L04_h200_k3', '200'), ('L02_h5000_k3', '5000'),
           ('L13_auto20k', '$2{\\cdot}10^4$'), ('L14_auto40k', '$4{\\cdot}10^4$'),
           ('L05_sa_1e-3', '$10^{-3}$'), ('L15_sa_3e-3', '$3{\\cdot}10^{-3}$'), ('L06_sa_1e-2', '$10^{-2}$')],
          '--score', '--size=footnotesize', '--tabcolsep=4pt',
          '--groups=3:late acceptance $L$,2:automatic $C$,3:annealing $T_0$')
    table('rq3_lahc_options.tex', 'tab:rq3lahc',
          'Options of LA-POEMS with late acceptance ($L = 1000$): steps of exactly one action and of at most two '
          'actions instead of three, weights of the action types favouring flip, rotate, exchange value and move '
          'node (3:2:1:3:1:1:2), restarts after $5 \\cdot 10^4 n$ steps without improvement, and local partners '
          'with the probability 0.5 and 0.25 (dead space [\\%], 30\\,s, seeds 1--6)',
          [('L01_h1000_k3', 'base'), ('L08_k1', '$k = 1$'), ('L09_k2', '$k \\leq 2$'), ('L07_weights', 'weights'),
           ('L10_restart50k', 'restarts'), ('L11_local50', 'local 0.5'), ('L12_local25', 'local 0.25')],
          '--score', '--size=footnotesize')
    table('rq3_sa_options.tex', 'tab:rq3sa',
          'Options of LA-POEMS with simulated annealing ($T_0 = 10^{-2}$, $T_1 = 10^{-5}$, steps of at most three '
          'actions): steps of exactly one and of at most two actions, local partners with the probability 0.5 '
          '(alone and with steps of at most two actions), adaptive selection of the action types, and, for '
          'comparison, late acceptance with the automatic history ($C = 4 \\cdot 10^4$) and restarts '
          '(dead space [\\%], 30\\,s, seeds 1--6)',
          [('L06_sa_1e-2', 'base'), ('L18_sa_1e-2_k1', '$k = 1$'), ('L19_sa_1e-2_k2', '$k \\leq 2$'),
           ('L17_sa_1e-2_local50', 'local'), ('L21_sa_1e-2_k2_local50', '$k \\leq 2$, local'),
           ('L16_sa_1e-2_adaptive', 'adaptive'), ('L20_auto40k_restart', 'LA, restarts')],
          '--score', '--size=footnotesize', '--tabcolsep=4pt')

    final_table('rq3_equal.tex', 'tab:rq3equal',
                'POEMS and LA-POEMS on the same array implementation: POEMS with the parameters of the original '
                'edition, the first design of LA-POEMS (late acceptance, $L = 1000$, steps of at most three actions) '
                'and the final configuration of LA-POEMS; both LA-POEMS columns are compared with POEMS (dead space '
                '[\\%], 30\\,s, seeds 1--6)',
                [('E08_fast_G1000', 'POEMS'), ('L01_h1000_k3', 'first design'),
                 (LAPOEMS_SCREEN, 'final')], '--compare=first', '--size=footnotesize', '--tabcolsep=4pt')

    # RQ4
    caption = ('Final comparison of the original POEMS, the improved POEMS and LA-POEMS on the %s benchmarks '
               '(dead space [\\%%], 60\\,s, seeds %s; mean, standard deviation and best run). Each algorithm '
               'is tested against the column to its left; the last columns give the probability that a run of '
               'LA-POEMS ends with less dead space than a run of the other algorithm')
    final_table('rq4_final.tex', 'tab:final', caption % ('tuning', '101--108'),
                [('F0_orig_main', 'original'), ('F1_improved_main', 'improved'), ('F2_lapoems_main', 'LA-POEMS')],
                '--size=footnotesize', '--tabcolsep=4pt')
    final_table('rq4_heldout.tex', 'tab:heldout', caption % ('held-out', '101--105'),
                [('F0_orig_heldout', 'original'), ('F1_improved_heldout', 'improved'),
                 ('F2_lapoems_heldout', 'LA-POEMS')],
                '--size=footnotesize', '--tabcolsep=4pt')
    ttt_table('rq4_ttt.tex', 'tab:ttt',
              'Median time [s] in which the improved POEMS and LA-POEMS first reach the median final dead space '
              'of the original POEMS (the target, 60\\,s runs, seeds 101--108; -- = not reached by half of the '
              'runs)',
              'F0_orig_main', 'original', [('F1', 'improved POEMS'), ('F2', 'LA-POEMS')])
    literature_table('rq4_literature.tex', 'tab:literature',
                     'LA-POEMS compared with the original edition and with published results: dead space [\\%] of '
                     'LA-POEMS (mean and best of 8 runs of 60\\,s, seeds 101--108, and of 5 runs of 300\\,s, seeds '
                     '101--105), the mean of 10 runs of the longest configuration of the original edition '
                     '($I = 20\\,000$, on a 2\\,GHz computer), CompaSS \\cite{benchmcnc,benchgsrc} (2.8\\,GHz '
                     'Pentium~4) and the B*-Tree with simulated annealing \\cite{btree} (computed from the areas '
                     'reported with two decimals, 200\\,MHz Sun Ultra-I); times as reported',
                     'F2_lapoems_main', 'LONG_lapoems')


if __name__ == '__main__':
    main()
