#!/bin/bash

set -euo pipefail

# alternatively:
# zcat "$1" | parallel --block 2M -j4 --pipe --round-robin "gzip > $2/part_{#}.gz"

zcat "$1" | split  -l20000000 -a 3 -d --filter='gzip > $FILE.gz' - "$2"/part_
