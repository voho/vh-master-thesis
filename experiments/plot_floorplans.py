#!/usr/bin/env python3
"""Draws floorplans written by the benchmark runner (-Dpoems.placement=DIR).

usage: plot_floorplans.py OUT.pdf COLUMNS FILE[:Title] ...
Each FILE has lines "module,x,y,width,height". The enclosing rectangle is drawn
in gray, so the dead space is visible; the dead space is computed from the file.
"""
import csv
import math
import os
import sys

import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle

INK, INK2, DEAD, FILL, EDGE, SURF = '#0b0b0b', '#52514e', '#c3c2b7', '#cde2fb', '#256abf', '#ffffff'


def main():
    out, columns = sys.argv[1], int(sys.argv[2])
    items = [a.split(':', 1) if ':' in a else (a, os.path.basename(a).split('_')[0]) for a in sys.argv[3:]]
    rows = math.ceil(len(items) / columns)
    plt.rcParams.update({'font.family': 'DejaVu Sans', 'font.size': 9})
    fig, axes = plt.subplots(rows, columns, figsize=(1.75 * columns, 1.9 * rows), facecolor=SURF)
    axes = [axes] if rows * columns == 1 else list(axes.flat)
    for ax, (path, title) in zip(axes, items):
        modules = [tuple(int(v) for v in r[1:5]) for r in csv.reader(open(path)) if r]
        width = max(x + w for x, y, w, h in modules)
        height = max(y + h for x, y, w, h in modules)
        used = sum(w * h for x, y, w, h in modules)
        # draw the longer side horizontally
        transpose = height > width
        if transpose:
            modules = [(y, x, h, w) for x, y, w, h in modules]
            width, height = height, width
        ax.add_patch(Rectangle((0, 0), width, height, facecolor=DEAD, edgecolor='none'))
        line = 0.4 if len(modules) > 100 else 0.6
        for x, y, w, h in modules:
            ax.add_patch(Rectangle((x, y), w, h, facecolor=FILL, edgecolor=EDGE, linewidth=line))
        ax.set_xlim(0, width)
        ax.set_ylim(0, height)
        ax.set_aspect('equal')
        ax.axis('off')
        ax.set_title('%s  (%.2f %%)' % (title, 100.0 * (width * height / used - 1)), color=INK, fontsize=9,
                     loc='left')
    for ax in axes[len(items):]:
        ax.axis('off')
    fig.tight_layout()
    fig.savefig(out, facecolor=SURF, bbox_inches='tight')


if __name__ == '__main__':
    main()
