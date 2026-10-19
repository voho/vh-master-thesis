Introduction
============

My master's thesis project (written in English, partly in Czech). It solves the floorplanning (rectangle packing) problem by using the POEMS evolutionary algorithm. This repository is primarily aimed as a backup site, but may be useful when searching for an inspiration.

The thesis exists in two editions:

* the original edition (2011), preserved in the git history (for example `git show 5906204:thesis/thesis.pdf > original.pdf`);
* the revised edition (2026, [thesis/thesis.pdf](thesis/thesis.pdf)), prepared with the help of the AI assistant Claude: a faster and corrected implementation, an analysis of the genetic algorithm of POEMS, a new algorithm (late acceptance POEMS, LA-POEMS) and a new experimental evaluation. Everything needed to reproduce its experiments is in [experiments/](experiments/README.md).

In the experiments of the revised edition (equal time budgets of one minute, new seeds, held-out instances), late acceptance POEMS reduces the dead space of the original POEMS by 57-73 % on the benchmarks with 33 to 300 modules (for example from 11.0 % to 3.4 % on n300) and is significantly better than an improved POEMS on most instances.

Build
-----

The project is built by Maven (Java 8 or newer), which also runs the unit tests:

```bash
mvn package
java -jar target/program.jar
```

Usage
-----

Usage can be displayed by running:

```bash
java -jar program.jar
```

The first nine command line arguments are mandatory:

* input directory (expect text files with benchmarks, fixed format, see examples)
* output directory (here will be placed output directories for each benchmark)
* verbose mode (true/false)
* number of test runs
* iterations
* generations
* niché count (length of action sequences)
* niché size (number of individuals in each population niché)
* smart-fit prototype generated (true/false) 

They can be followed by these optional arguments:

* `--algorithm=poems|lapoems` the solver: POEMS with the genetic algorithm (default) or late acceptance POEMS (Chapter 3 of the revised thesis)
* `--time=SECONDS` time limit of each run (late acceptance POEMS makes I * G steps if no time limit is set)
* `--seed=NUMBER` seed of the random number generator (makes the runs reproducible)
* `--history=LENGTH` late acceptance POEMS: use late acceptance with this history length (0 = automatic) instead of simulated annealing

For late acceptance POEMS, the niche count N is the maximal number of actions in one step. The settings evaluated in the thesis are switched by Java system properties (`-Dpoems.*`, see the appendix of the thesis).

Usage example
-------------

```bash
java -jar program.jar benchmark/ benchmark/result/ true 5 1000 500 5 50 true
java -jar program.jar benchmark/ benchmark/result/ false 5 0 0 2 0 true --algorithm=lapoems --time=60
```

Benchmarking
------------

`program.Benchmark` solves a single benchmark with a fixed seed and time limit (in milliseconds) and prints one CSV line (benchmark, seed, dead space in %, unused area, total area, used area, width, height, evaluations, iterations, time, validation result). The final floorplan is always checked (all modules present once, original or rotated dimensions, no overlaps).

```bash
java -cp target/program.jar program.Benchmark benchmark/n100.txt 1 30000 1000000000 1000 3 50
java -cp target/program.jar -Dpoems.algorithm=lapoems program.Benchmark benchmark/n100.txt 1 30000 0 0 2 0
```

Results by image
----------------

Here you can see some animated results!

![n200](https://github.com/voho/vh-master-thesis/raw/master/presentation/n200loop.gif)
![n300](https://github.com/voho/vh-master-thesis/raw/master/presentation/n300loop.gif)

Animated GIF creation
---------------------

Note: You can create animated GIFs from the output directory by the following script:

```bash
#!/bin/bash
rm *.gif
for file in i_*.svg
do
  convert ${file} anim_${file}.gif
done
gifsicle --delay 50 --loop -O3 -D background --background "#000000" anim_*.gif > floorplan.gif 
rm anim_*.gif
```

Note: You can convert SVG images to PDF using the Inkscape:

```bash
#!/bin/bash
rm *.pdf
for file in i_*.svg
do
  inkscape -z --file=${file} --export-pdf=${file}.pdf
done
```
