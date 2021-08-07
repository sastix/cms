#!/bin/bash

IFS=$'\n' read -d '' -r -a lines < $1

n=1

if test -f "test.properties"; then
    source test.properties
fi

for i in "${lines[@]}"
do
    echo "Testing ${i}"
    bash -c "/benchmarking/wrk/wrk -t${NUMBER_OF_THREADS:-1} -c${NUMBER_OF_CONNECTIONS:-2} -d${TEST_DURATION_IN_SECONDS:-30}s ${i}" > "results/test${n}.txt" 2>&1
    echo "Output saved at results/test${n}.txt"
    n=$((n+1))
done
