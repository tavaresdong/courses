#!/bin/bash

set -e

tmpname=performance_measure_tmp

if [ $# -ne 1 ]
then
    # If input argument wrongly set, output 0
    echo 0
else
    url=$1
    wget -q -O ${tmpname} $url
    # If donwload failed, output 0
    if [ $? -ne 0 ]
    then
        echo 0
    fi
    wc -c ${tmpname} | awk {'print $1'}
    rm ${tmpname}
fi
