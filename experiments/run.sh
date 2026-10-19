#!/bin/bash
# Runs one experiment: every benchmark with every seed, a few runs in parallel.
# The results (one CSV line per run, see program.Benchmark) are appended to
# experiments/results/LABEL.csv.
#
# usage: experiments/run.sh LABEL CLASSPATH TIME_MS I G N S "SEEDS" "BENCHMARKS" PARALLEL [JAVA_OPTIONS...]
# example (the original parameters of POEMS, 30 s, seeds 1-8):
#   experiments/run.sh E00 target/program.jar 30000 1000000000 1000 3 50 "1 2 3 4 5 6 7 8" \
#     "MCNCapte MCNCxerox MCNChp MCNCami33 MCNCami49 n100 n200 n300" 3
DIR=$(cd "$(dirname "$0")/.." && pwd)
LABEL=$1; CP=$2; T=$3; I=$4; G=$5; N=$6; S=$7; SEEDS=$8; BENCHES=$9; PAR=${10}; shift 10
mkdir -p "$DIR/experiments/results"
OUT="$DIR/experiments/results/$LABEL.csv"
for b in $BENCHES; do for s in $SEEDS; do echo "$b $s"; done; done | \
  xargs -P "$PAR" -n 2 sh -c "java -XX:+UseSerialGC -Xms256m -Xmx1g $* -cp $CP program.Benchmark $DIR/benchmark/\$0.txt \$1 $T $I $G $N $S 2>/dev/null >> $OUT"
