#!/bin/bash


if [ $# -ne 1 ]
then
    # If input argument wrongly set, output 0
    echo 0
    exit
else
    url=$1
    tmpname=$(mktemp -q)    
    wget -t 10 -T 10 -q -O ${tmpname} $url
    # If donwload failed, output 0
    if [ $? -ne 0 ]
    then
        echo 0
    else
        wc -c ${tmpname} | awk {'print $1'}
    fi
    rm -f ${tmpname}
fi

exit
