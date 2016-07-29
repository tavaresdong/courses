#!/bin/bash

set -e

timer=/usr/bin/time
exe_moon=./moonlight
exe_rose=./rosebud
void=/dev/null
moonlight_data=${exe_moon}.dat
rosebud_data=${exe_rose}.dat

if [ ! -e $moonlight_data ]
then
    echo "#N time" >> $moonlight_data 
    for (( i=100; i <= 15000; i += 100))
    do
        time_elapsed_moon=$(($timer -f "%e" $exe_moon $i > $void) 2>&1)
        echo "$i $time_elapsed_moon" >> $moonlight_data
    done
fi

if [ ! -e $rosebud_data ]
then
    echo "#N time" >> $rosebud_data
    for (( i=100; i <= 15000; i += 100))
    do
        time_elapsed_rose=$(($timer -f "%e" $exe_rose $i > $void) 2>&1)
        echo "$i $time_elapsed_rose" >> $rosebud_data
    done
fi

# Plotting the two data files collected, provided they all exist
if [ -e $moonlight_data ] && [ -e $rosebud_data ]  && [ -e plot.gp ]
then
    gnuplot -p -c plot.gp
fi

    
echo "done"
