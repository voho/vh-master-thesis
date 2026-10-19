#!/usr/bin/env python3
"""Creates the figures of Chapter 5 of the thesis.

usage: make_all_figures.py [EXPERIMENTS_DIR]   (default: this directory)
Reads results/, traces/ and placements/ of the experiments directory and writes
the figures into thesis/figures/new/. Figures whose data are missing are skipped.
"""
import csv
import glob
import os
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
BASE = sys.argv[1] if len(sys.argv) > 1 else HERE
RESULTS = os.path.join(BASE, 'results')
TRACES = os.path.join(BASE, 'traces')
PLACEMENTS = os.path.join(BASE, 'placements')
FIGURES = os.path.join(HERE, '..', 'thesis', 'figures', 'new')

# colours follow the algorithm in all figures: original = orange, POEMS = blue, LA-POEMS = aqua
ORIGINAL, POEMS, LAPOEMS = '#eb6834', '#2a78d6', '#1baf7a'


def run(script, *args):
    subprocess.run([sys.executable, os.path.join(HERE, script)] + list(args), check=True, stdout=subprocess.DEVNULL)
    print('created', args[0])


def best_placement(benchmark, results):
    """Placement file of the best run of a benchmark among the given (result, placement directory) pairs."""
    best = None
    for label, directory in results:
        path = os.path.join(RESULTS, label + '.csv')
        if not os.path.exists(path):
            continue
        for row in csv.reader(open(path)):
            if len(row) >= 12 and row[0] == benchmark:
                placement = os.path.join(PLACEMENTS, directory, '%s_%s.csv' % (row[0], row[1]))
                if os.path.exists(placement) and (best is None or float(row[2]) < best[0]):
                    best = (float(row[2]), placement)
    return best[1] if best else None


def main():
    os.makedirs(FIGURES, exist_ok=True)

    placer = [os.path.join(RESULTS, 'placer_%s.csv' % v) for v in ('orig', 'tree', 'array')]
    if all(os.path.exists(p) for p in placer):
        run('plot_placer.py', os.path.join(FIGURES, 'placer.pdf'), *placer)

    if all(glob.glob(os.path.join(TRACES, d, '*.csv')) for d in ('F0', 'F1', 'F2')):
        run('plot_convergence.py', os.path.join(FIGURES, 'convergence.pdf'), 'MCNCami49,n100,n300', '60',
            '%s:original POEMS:%s' % (os.path.join(TRACES, 'F0'), ORIGINAL),
            '%s:improved POEMS:%s' % (os.path.join(TRACES, 'F1'), POEMS),
            '%s:LA-POEMS:%s' % (os.path.join(TRACES, 'F2'), LAPOEMS))

    sources = [('F2_lapoems_main', 'F2'), ('LONG_lapoems', 'LONG')]
    items = []
    for b in ['MCNCapte', 'MCNCxerox', 'MCNChp', 'MCNCami33', 'MCNCami49', 'n100', 'n200', 'n300']:
        path = best_placement(b, sources)
        if path:
            items.append('%s:%s' % (path, b.replace('MCNC', '')))
    if items:
        run('plot_floorplans.py', os.path.join(FIGURES, 'floorplans.pdf'), '4', *items)


if __name__ == '__main__':
    main()
