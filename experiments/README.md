Experiments
===========

This directory contains everything needed to reproduce the experiments of
Chapter 5 of the revised thesis (`thesis/thesis.pdf`): the runner, the raw
results of every run, and the scripts that create the tables and figures.

Layout
------

| path | content |
|---|---|
| `run.sh` | runs one experiment (all benchmarks x all seeds, a few runs in parallel) |
| `results/*.csv` | raw results, one line per run (see below) |
| `results/*.txt` | outputs of the instrumented builds (GA analysis, rotation, single actions) |
| `traces/*/` | convergence traces of the final comparison: one file per run, lines "time [ms], unused area" at every improvement |
| `placements/` | floorplans of the final LA-POEMS runs: one file per run, lines "module, x, y, width, height" |
| `patches/` | changes of the instrumented and ablation builds, relative to the commits given below |
| `make_all_tables.py` | creates all tables of Chapter 5 from `results/` and `traces/` |
| `make_all_figures.py` | creates the figures of Chapter 5 from `results/`, `traces/` and `placements/` |
| `make_table.py`, `make_final_table.py`, `make_speed_table.py`, `make_ttt_table.py`, `make_literature_table.py`, `table_gaanalysis.py` | the individual table generators |
| `plot_placer.py`, `plot_convergence.py`, `plot_floorplans.py` | the individual figure generators |
| `java/PlacerBench.java` | microbenchmark of the placement (Fig. 5.1) |

A line of a results file has the fields: benchmark, seed, dead space [%],
unused area, total area, area of modules, width, height, evaluations,
iterations, time [ms] and the result of the independent validation of the
floorplan (`ok`).

Reproducing
-----------

```bash
mvn package
# the original parameters of POEMS, 30 s, seeds 1-8, three runs in parallel
experiments/run.sh E00 target/program.jar 30000 1000000000 1000 3 50 "1 2 3 4 5 6 7 8" \
  "MCNCapte MCNCxerox MCNChp MCNCami33 MCNCami49 n100 n200 n300" 3
# LA-POEMS in its final configuration (the defaults), 60 s, seeds 101-108
experiments/run.sh F2 target/program.jar 60000 0 0 2 1000 "101 102 103 104 105 106 107 108" \
  "MCNCapte MCNCxerox MCNChp MCNCami33 MCNCami49 n100 n200 n300" 3 -Dpoems.algorithm=lapoems
# the first design of LA-POEMS: late acceptance (L = 1000), steps of at most 3 actions
experiments/run.sh L01 target/program.jar 30000 0 0 3 1000 "1 2 3 4 5 6" \
  "MCNCxerox MCNChp MCNCami33 MCNCami49 n100 n200 n300" 3 \
  -Dpoems.algorithm=lapoems -Dpoems.t0=0 -Dpoems.local=0
# all tables and figures (scripts need Python 3 with scipy, numpy and matplotlib)
python3 experiments/make_all_tables.py experiments/results
python3 experiments/make_all_figures.py
```

The runner arguments are `TIME_MS I G N S`. For POEMS, `I` is the maximal
number of iterations (10^9 = limited by the time only), `G` the generations,
`N` the niche count and `S` the niche size. For LA-POEMS (`-Dpoems.algorithm=lapoems`,
`lahc` is an alias), `I` and `G` are unused, `N` is the maximal number of
actions in a step and `S` the late acceptance history length (0 = automatic).
All runs used `-XX:+UseSerialGC -Xms256m -Xmx1g` on OpenJDK 21 and ran three
at a time on a four-core virtual machine (Intel Xeon, 2.8 GHz). The time limit
is measured from the start of the optimisation, so runs with a time limit are
reproducible only up to the number of steps made within it; runs with a limit
on the iterations (POEMS) or evaluations (`-Dpoems.evaluations`, LA-POEMS) are
reproducible exactly. In the table below, the paths of traces and placements
are relative to this directory.

Builds
------

The experiments that need older versions of the code name the commit (and a
patch from `patches/`, applied with `git apply`). The screening experiments of
LA-POEMS and POEMS ran with builds of commits `9d37a86` to `e3d9735`; their
results are reproduced exactly by the current code with the options given
below, which was checked by evaluation-limited runs (`-Dpoems.evaluations=300000`
or a fixed number of iterations, n100, seed 1) for every build used. Commit
`a0abf17` changed the defaults of `poems.fast` (true), `poems.t0` (0.01),
`poems.t1` (0.00001) and `poems.local` (0.5); the options below give the values
of these switches explicitly where they differ from the new defaults.

| build | commit | content |
|---|---|---|
| original | `5751596` | the original implementation with a seedable random number generator and the benchmark runner |
| original + trace | `5751596` + `patches/basetrace.patch` | as above, with the convergence trace |
| optimised | `678d1b2` | the original algorithm, faster implementation (identical results) |
| corrected | `d943c48` | the optimised implementation with the defects corrected |
| corrected, original prototype | `d943c48` + `patches/noproto.patch` | ablation |
| corrected, original identity check | `d943c48` + `patches/norot.patch` | ablation |
| GA instrumentation | `d6138ff` + `patches/instrument.patch` | counters of the genetic algorithm |
| GA instrumentation, original identity check | `d6138ff` + `patches/instrument_norot.patch` | as above, with the original identity check |
| action statistics | `97de782` + `patches/action_stats.patch` | outcome of single-action steps of LA-POEMS |
| current | this commit | everything else |

Experiments
-----------

Benchmarks: "main" = MCNCapte MCNCxerox MCNChp MCNCami33 MCNCami49 n100 n200 n300,
"main-apte" = the same without MCNCapte, "held-out" = n30b n50b n100b n100c n200b n200c.

<!-- INDEX -->
| result | thesis | build | time | I G N S | seeds | benchmarks | options |
|---|---|---|---|---|---|---|---|
| `E00_base_30s` | Tables 5.1, 5.2 | original | 30 s | 10^9 1000 3 50 | 1-8 | main | |
| `E01_perf_30s` | Tables 5.1-5.3 | optimised | 30 s | 10^9 1000 3 50 | 1-8 | main | |
| `E02_fix_30s` | Tables 5.1, 5.3 | corrected | 30 s | 10^9 1000 3 50 | 1-8 | main | |
| `A01_fix_noproto` | Section 5.3 | corrected, original prototype | 30 s | 10^9 1000 3 50 | 1-8 | n100 n200 | |
| `A02_fix_norot` | Section 5.3 | corrected, original identity check | 30 s | 10^9 1000 3 50 | 1-8 | n100 n200 | |
| `gaanalysis.txt` | Table 3.2 | GA instrumentation | 20 s | 10^9 1000 3 50 | 1 | main | `-Dpoems.fast=true` |
| `rotation_effect.txt` | Table 5.4 | GA instrumentation (`FIXED`), with the original identity check (`NOROT`) | 20 s | 10^9 1000 3 50 | 1 | xerox ami33 n100 n300 | |
| `action_stats.txt` | Table 5.6 | action statistics | 10 s | 0 0 3 1000 | 1 | ami33 n100 n300 | `-Dpoems.algorithm=lahc` |
| `placer_orig.csv`, `placer_tree.csv`, `placer_array.csv` | Fig. 5.1 | `java/PlacerBench.java` compiled against the original build (`placer_orig.csv`, argument `tree`) and against `d6138ff` (`placer_tree.csv` with `tree`, `placer_array.csv` with `array`) | | | | | |
| `E08_fast_G1000` | Tables 5.1, 5.5, 5.10 | current | 30 s | 10^9 1000 3 50 | 1-6 | main-apte | `-Dpoems.fast=true` |
| `E09_fast_G300` | Table 5.5 | current | 30 s | 10^9 300 3 50 | 1-6 | main-apte | `-Dpoems.fast=true` |
| `E10_fast_G100` | Table 5.5 | current | 30 s | 10^9 100 3 50 | 1-6 | main-apte | `-Dpoems.fast=true` |
| `E11_fast_dedupe` | Table 5.5 | current | 30 s | 10^9 1000 3 50 | 1-6 | main-apte | `-Dpoems.fast=true -Dpoems.dedupe=true` |
| `E12_fast_move` | Table 5.5 | current | 30 s | 10^9 1000 3 50 | 1-6 | main-apte | `-Dpoems.fast=true -Dpoems.move=true` |
| `E13_fast_pat100` | Table 5.5 | current | 30 s | 10^9 1000 3 50 | 1-6 | main-apte | `-Dpoems.fast=true -Dpoems.patience=100` |
| `E14_fast_lahc20` | Table 5.5 | current | 30 s | 10^9 1000 3 50 | 1-6 | main-apte | `-Dpoems.fast=true -Dpoems.lahc=20` |
| `E15_fast_first` | Table 5.5 | current | 30 s | 10^9 1000 3 50 | 1-6 | main-apte | `-Dpoems.fast=true -Dpoems.first=true` |
| `L01_h1000_k3` | Tables 5.7, 5.8, 5.10 | current | 30 s | 0 0 3 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0 -Dpoems.local=0` |
| `L02_h5000_k3` | Table 5.7 | current | 30 s | 0 0 3 5000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0 -Dpoems.local=0` |
| `L04_h200_k3` | Table 5.7 | current | 30 s | 0 0 3 200 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0 -Dpoems.local=0` |
| `L05_sa_1e-3` | Table 5.7 | current | 30 s | 0 0 3 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0.001 -Dpoems.t1=0.000001 -Dpoems.local=0` |
| `L06_sa_1e-2` | Tables 5.7, 5.9 | current | 30 s | 0 0 3 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0.01 -Dpoems.t1=0.00001 -Dpoems.local=0` |
| `L07_weights` | Table 5.8 | current | 30 s | 0 0 3 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.weights=3,2,1,3,1,1,2 -Dpoems.t0=0 -Dpoems.local=0` |
| `L08_k1` | Table 5.8 | current | 30 s | 0 0 1 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0 -Dpoems.local=0` |
| `L09_k2` | Table 5.8 | current | 30 s | 0 0 2 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0 -Dpoems.local=0` |
| `L10_restart50k` | Table 5.8 | current | 30 s | 0 0 3 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.restart=50000 -Dpoems.t0=0 -Dpoems.local=0` |
| `L11_local50` | Table 5.8 | current | 30 s | 0 0 3 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.local=0.5 -Dpoems.t0=0` |
| `L12_local25` | Table 5.8 | current | 30 s | 0 0 3 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.local=0.25 -Dpoems.t0=0` |
| `L13_auto20k` | Table 5.7 | current | 30 s | 0 0 3 0 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0 -Dpoems.local=0` |
| `L14_auto40k` | Table 5.7 | current | 30 s | 0 0 3 0 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.ratio=40000 -Dpoems.t0=0 -Dpoems.local=0` |
| `L15_sa_3e-3` | Table 5.7 | current | 30 s | 0 0 3 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0.003 -Dpoems.t1=0.000001 -Dpoems.local=0` |
| `L16_sa_1e-2_adaptive` | Table 5.9 | current | 30 s | 0 0 3 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0.01 -Dpoems.t1=0.00001 -Dpoems.adaptive=true -Dpoems.local=0` |
| `L17_sa_1e-2_local50` | Table 5.9 | current | 30 s | 0 0 3 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0.01 -Dpoems.t1=0.00001 -Dpoems.local=0.5` |
| `L18_sa_1e-2_k1` | Table 5.9 | current | 30 s | 0 0 1 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0.01 -Dpoems.t1=0.00001 -Dpoems.local=0` |
| `L19_sa_1e-2_k2` | Table 5.9 | current | 30 s | 0 0 2 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0.01 -Dpoems.t1=0.00001 -Dpoems.local=0` |
| `L20_auto40k_restart` | Table 5.9 | current | 30 s | 0 0 3 0 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.ratio=40000 -Dpoems.restart=50000 -Dpoems.t0=0 -Dpoems.local=0` |
| `L21_sa_1e-2_k2_local50` | Tables 5.1, 5.9, 5.10 | current | 30 s | 0 0 2 1000 | 1-6 | main-apte | `-Dpoems.algorithm=lapoems -Dpoems.t0=0.01 -Dpoems.t1=0.00001 -Dpoems.local=0.5` |
| `F0_orig_main` | Tables 5.11, 5.13, Fig. 5.2 | original + trace | 60 s | 10^9 1000 3 50 | 101-108 | main | `-Dpoems.trace=traces/F0` |
| `F0_orig_heldout` | Table 5.12 | original + trace | 60 s | 10^9 1000 3 50 | 101-105 | held-out | `-Dpoems.trace=traces/F0` |
| `F1_improved_main` | Tables 5.11, 5.13, Fig. 5.2 | current | 60 s | 10^9 100 3 50 | 101-108 | main | `-Dpoems.fast=true -Dpoems.trace=traces/F1` |
| `F1_improved_heldout` | Table 5.12 | current | 60 s | 10^9 100 3 50 | 101-105 | held-out | `-Dpoems.fast=true -Dpoems.trace=traces/F1` |
| `F2_lapoems_main` | Tables 5.11, 5.13, 5.14, Figs. 5.2, 5.3 | current | 60 s | 0 0 2 1000 | 101-108 | main | `-Dpoems.algorithm=lapoems -Dpoems.t0=0.01 -Dpoems.t1=0.00001 -Dpoems.local=0.5 -Dpoems.trace=traces/F2 -Dpoems.placement=placements/F2` |
| `F2_lapoems_heldout` | Table 5.12 | current | 60 s | 0 0 2 1000 | 101-105 | held-out | as `F2_lapoems_main` |
| `LONG_lapoems` | Table 5.14, Fig. 5.3 | current | 300 s | 0 0 2 1000 | 101-105 | ami33 ami49 n100 n200 n300 | `-Dpoems.algorithm=lapoems -Dpoems.t0=0.01 -Dpoems.t1=0.00001 -Dpoems.local=0.5 -Dpoems.trace=traces/LONG -Dpoems.placement=placements/LONG` |
<!-- /INDEX -->
