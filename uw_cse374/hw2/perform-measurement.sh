#!/bin/bash

set -e


if [ $# -ne 1 ]
then
    # If input argument wrongly set, output 0
    echo 0
else
    url=$1
    tmpname=$(mktemp -q)    
    wget -t 10 -T 10 -q -O ${tmpname} $url
    # If donwload failed, output 0
    if [ $? -ne 0 ]
    then
        echo 0
    fi
    wc -c ${tmpname} | awk {'print $1'}
    rm -f ${tmpname}
fi
