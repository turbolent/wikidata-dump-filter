#!/bin/bash

set -euo pipefail

cd $(dirname "$0")

out="${1%%/}"
parallel -j 3 -L 10 --eta "mvn exec:java -Dexec.args=\" \
  --output ${out}/out-{#}.gz \
  --gzip-output true \
  --predicate-blacklist data/unique-identifier-properties.txt \
  --predicate-blacklist data/schema-properties.txt {} \
  \""

