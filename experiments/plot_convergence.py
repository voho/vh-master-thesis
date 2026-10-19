#!/usr/bin/env python3
"""usage: plot_convergence.py OUT.svg BENCH1,BENCH2,... TIME_S DIR:Label:#hex [DIR:Label:#hex ...]
Median (and interquartile band) of the best-so-far dead space over seeds vs time."""
import sys, os, glob, csv
import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

USED = {'MCNCapte':46561628,'MCNCxerox':19350296,'MCNChp':8830584,'MCNCami33':1156449,
        'MCNCami49':35445424,'n100':179501,'n200':175696,'n300':273170,'n30b':197781,
        'n50b':203053,'n100b':160126,'n100c':171966,'n200b':174593,'n200c':170129}
INK, INK2, GRID, SURF = '#0b0b0b', '#52514e', '#e4e3df', '#ffffff'

out, benches, T = sys.argv[1], sys.argv[2].split(','), float(sys.argv[3])
series = [a.split(':') for a in sys.argv[4:]]
times = np.logspace(np.log10(0.05), np.log10(T), 300)

plt.rcParams.update({'font.family':'DejaVu Sans','font.size':9,'axes.edgecolor':GRID,
                     'axes.labelcolor':INK2,'xtick.color':INK2,'ytick.color':INK2})
fig, axes = plt.subplots(1, len(benches), figsize=(2.1*len(benches)+0.4, 2.5), facecolor=SURF)
axes = np.atleast_1d(axes)
for ax, b in zip(axes, benches):
    ax.set_facecolor(SURF)
    ends = []
    for d, label, color in series:
        curves = []
        for f in sorted(glob.glob(os.path.join(d, b + '_*.csv'))):
            rows = [(int(r[0])/1000.0, 100.0*int(r[1])/USED[b]) for r in csv.reader(open(f)) if r]
            if not rows: continue
            t = np.array([r[0] for r in rows]); v = np.array([r[1] for r in rows])
            idx = np.searchsorted(t, times, side='right') - 1
            curves.append(np.where(idx >= 0, v[np.clip(idx, 0, None)], np.nan))
        if not curves: continue
        c = np.array(curves)
        ok = np.sum(~np.isnan(c), axis=0) * 2 >= len(curves)
        med = np.where(ok, np.nanmedian(c, axis=0), np.nan)
        q1 = np.where(ok, np.nanpercentile(c, 25, axis=0), np.nan)
        q3 = np.where(ok, np.nanpercentile(c, 75, axis=0), np.nan)
        ax.fill_between(times, q1, q3, color=color, alpha=0.10, linewidth=0)
        ax.plot(times, med, color=color, linewidth=1.6, solid_capstyle='round', solid_joinstyle='round', label=label)
        last = med[~np.isnan(med)][-1]
        ax.plot([times[-1]], [last], 'o', color=color, markersize=5, markeredgecolor=SURF, markeredgewidth=1.5)
        ends.append((last, label))
    ax.set_xscale('log')
    ax.set_title(b.replace('MCNC', ''), color=INK, fontsize=10, loc='left', fontweight='bold')
    ax.set_xlabel('time [s]')
    ax.grid(True, which='major', color=GRID, linewidth=0.8)
    ax.set_axisbelow(True)
    for s in ['top', 'right']: ax.spines[s].set_visible(False)
    ax.set_xlim(0.05, T * 1.02)
    lo = min(e[0] for e in ends); hi = max(e[0] for e in ends)
    ax.set_ylim(0, max(hi * 2.2, lo + 2))
    # direct end labels (ink, not series color), placed in data order and pushed apart
    # so that they do not overlap (at least 8 % of the height of the axes)
    top = ax.get_ylim()[1]
    gap = 0.08 * top
    placed = []
    for value, label in sorted(ends):
        y = value if not placed else max(value, placed[-1] + gap)
        placed.append(y)
        ax.annotate('%.2f%%' % value, (T, value), xytext=(T * 1.12, y), textcoords='data',
                    va='center', ha='left', color=INK2, fontsize=8, annotation_clip=False)
axes[0].set_ylabel('dead space [%]')
handles, labels = axes[0].get_legend_handles_labels()
fig.legend(handles, labels, loc='upper center', ncol=len(labels), frameon=False, labelcolor=INK, fontsize=9,
           bbox_to_anchor=(0.5, 1.02))
fig.tight_layout(rect=(0, 0, 0.95, 0.9), w_pad=3.5)
fig.savefig(out, facecolor=SURF, bbox_inches='tight')
print('saved', out)
