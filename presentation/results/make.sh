#!/bin/bash
# Draws the end results compared in the README: the best floorplans of the original edition (2011,
# from the figures of its thesis) and of late acceptance POEMS (2026, from experiments/placements).
# Both pictures of a benchmark are drawn at the same scale (the modules cover the same number of
# pixels), with the longer side horizontal. Requires pdftoppm (poppler) and Pillow.
set -e
cd "$(dirname "$0")/../.."
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT

# benchmark, figure of the original edition, its dead space in % (as reported there),
# floorplan of LA-POEMS, area of the modules in pixels (smaller for the elongated floorplans)
while read -r name figure dead placement area; do
  pdftoppm -png -singlefile -scale-to 2400 "thesis/figures/$figure.pdf" "$tmp/2011_$name"
  python3 presentation/animate.py picture "$tmp/2011_$name.png" "presentation/results/2011_$name.png" --area="$area" --dead="$dead"
  python3 presentation/animate.py picture "experiments/placements/$placement.csv" "presentation/results/2026_$name.png" --area="$area"
done <<EOF
apte  apte    0.8  F2/MCNCapte_103    7400
xerox xerox   2.3  F2/MCNCxerox_101   47000
hp    hp      3.9  F2/MCNChp_101      26500
ami33 ami33   1.6  F2/MCNCami33_101   47000
ami49 ami49   2.1  LONG/MCNCami49_101 47000
n100  n100a   1.98 LONG/n100_101      47000
n200  n200a   3.45 LONG/n200_104      47000
n300  n300/66 3.78 LONG/n300_105      47000
EOF
