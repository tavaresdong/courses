#!/bin/bash

set -e

# Print message to stderr if usage error
# use 1>&2 to redirect echo's output to stderr
if [ $# -eq 0 ]
then
    echo "Usage: $0 file1 file2 ..." 1>&2
fi


# Iterate over all parameters(ending with .txt)
# And convert them to .png with the same preceding name
for param in "$@"
do
    # If the file does not exist, output an error msg
    if [ ! -e $param ]
    then
        echo "File: $param does not exist!" 1>&2
    fi

    # Remove the shortest match of 'txt' from right
    filename=${param%txt}

    # If the tmporary file exists, remove it
    tmpname=${filename}tmp
    if [ -e $tmpname ]
    then
        rm $tmpname
    fi

    # Extract width and height from the file
    width=`head -n1 $param`
    height=`head -n2 $param | tail -n1`
    echo "# ImageMagick pixel enumeration: $width,$height,255,srgb" > $tmpname

    w=0
    h=0
    count=0
    while read -r line
    do
        if [ $count -ge 2 ]
        then
            # This is a valid line, process it
            pixel=${line// /,}
            echo "$w,$h: ($pixel)" >> $tmpname
            w=$[$w + 1]
            if [ $w -eq $width ]
            then
                w=0
                h=$[$h + 1]
            fi
        fi
        count=$[$count + 1]
    done < "$param"
    convert txt:$tmpname ${filename}png
    rm $tmpname
done

echo "done processing all files"


