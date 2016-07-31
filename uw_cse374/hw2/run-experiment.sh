#!/bin/bash


if [ $# -ne 2 ]
then
    echo "Need one argument for list of urls and one for output"
fi

if [ ! -e $1 ]
then
    echo "Input file $1 does not exist "
fi

if [ -e $2 ]
then
    rm -f $2
fi

rank=1
while read -r line
do
    url=$line
    echo "Performing measurement on <$url>"
    size=$(./perform-measurement.sh $url)  
    if [ -z $size ]
    then
        echo "...failed"
    else
        echo "...success"
        echo "$rank $url $size" >> $2
    fi
    rank=$[$rank + 1]
done < "$1"

