Introduction

My master's thesis project (written in English, partly in Czech). It solves the floorplanning (rectangle packing) problem by using the POEMS evolutionary algorithm.

This repository is primarily aimed as a backup site, but may be useful when searching for an inspiration.
Usage

Usage can be displayed by running:

java -jar program.jar

All command line arguments are mandatory:

    input directory (expect text files with benchmarks, fixed format, see examples)
    output directory (here will be placed output directories for each benchmark)
    verbose mode (true/false)
    number of test runs
    iterations
    generations
    nich� count (length of action sequences)
    nich� size (number of individuals in each population nich�)
    smart-fit prototype generated (true/false) 

Example:

java -jar program.jar benchmark/ benchmark/result/ true 5 1000 500 5 50 true

Demo

Note: You can create animated GIFs from the output directory by the following script:

#!/bin/bash
rm *.gif
for file in i_*.svg
do
  convert ${file} anim_${file}.gif
done
gifsicle --delay 50 --loop -O3 -D background --background "#000000" anim_*.gif > floorplan.gif 
rm anim_*.gif

Note: You can convert SVG images to PDF using the Inkscape:

#!/bin/bash
rm *.pdf
for file in i_*.svg
do
  inkscape -z --file=${file} --export-pdf=${file}.pdf
done