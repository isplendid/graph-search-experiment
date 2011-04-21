#!/bin/bash

root=$1
output=$2

for dataset in $root/*
do
  echo Search in path $dataset ...
  if [ -d $dataset ]
  then
    for query in $dataset/*/*
    do
      if [ ! -f $query ]
      then
        continue
      fi
      line=`cat $query`
      
      echo -e "$query\t $line" >> $output
    done
  fi
done
