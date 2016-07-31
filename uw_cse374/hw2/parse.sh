#!/bin/bash

if [ $# -lt 2 ]
then
    echo "Fewer than 2 arguments provided" 1>&2
else
    input=$1
    output=$2
    if [ ! -e $input ]
    then
        echo "Input file: $input does not exist!" 1>&2
        exit
    fi
    url_lines=$(mktemp -q)

    grep "size=\"2\"><strong>.*\"http:\/\/.*\"" $input \
    | sed 's/^.*http/http/' \
    | sed 's/\".*$//' \
    > $output
    echo "parse urls donme"
fi
