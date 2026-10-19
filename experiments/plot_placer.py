#!/usr/bin/env python3
"""Plots the placement time against the number of modules (log-log).

usage: plot_placer.py OUT.pdf original.csv tree.csv array.csv
Each CSV has lines "modules,microseconds" as printed by PlacerBench.
"""
import csv
import sys

import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt

INK, INK2, GRID, SURF = '#0b0b0b', '#52514e', '#e4e3df', '#ffffff'
COLORS = ['#eb6834', '#2a78d6', '#1baf7a']
LABELS = ['original (sorted set of all modules)', 'array contour, immutable tree', 'array B*-Tree']


def main():
    out = sys.argv[1]
    plt.rcParams.update({'font.family': 'DejaVu Sans', 'font.size': 9, 'axes.edgecolor': GRID,
                         'axes.labelcolor': INK2, 'xtick.color': INK2, 'ytick.color': INK2})
    fig, ax = plt.subplots(figsize=(5.6, 3.6), facecolor=SURF)
    ax.set_facecolor(SURF)
    for path, color, label in zip(sys.argv[2:5], COLORS, LABELS):
        rows = [(int(r[0]), float(r[1])) for r in csv.reader(open(path)) if r]
        n = [r[0] for r in rows]
        t = [r[1] for r in rows]
        ax.plot(n, t, color=color, linewidth=2, marker='o', markersize=5, markeredgecolor=SURF,
                markeredgewidth=1.5, label=label)
        ax.annotate('%.0f µs' % t[-1] if t[-1] >= 10 else '%.1f µs' % t[-1], (n[-1], t[-1]), xytext=(6, 0),
                    textcoords='offset points', va='center', color=INK2, fontsize=8)
    ax.set_xscale('log')
    ax.set_yscale('log')
    ax.set_xlabel('modules')
    ax.set_ylabel('time of one placement [µs]')
    ax.grid(True, which='major', color=GRID, linewidth=0.8)
    ax.set_axisbelow(True)
    for side in ['top', 'right']:
        ax.spines[side].set_visible(False)
    ax.legend(frameon=False, loc='upper left', labelcolor=INK, fontsize=8)
    fig.tight_layout()
    fig.savefig(out, facecolor=SURF, bbox_inches='tight')


if __name__ == '__main__':
    main()
