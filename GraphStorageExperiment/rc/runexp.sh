#!/bin/bash
#

query_folder=$1
result_folder=$2

if [ ! -d $result_folder ]
then
  mkdir $result_folder
fi

for qf in $query_folder/*
do
  qf_bn=`basename $qf`
  result_path=$result_folder/$qf_bn

  if [ ! -d $result_path ]
  then
    mkdir $result_path
  fi

  for query in $qf/*
  do
    if [ ! -f $query ]
    then
      continue
    fi
    base_name=`basename $query`
    output_file=$result_path/$base_name

    if [ -f $output_file ]
    then
      continue
    fi

    echo Running $query to $output_file
    timeout -s 9 180s java -Xmx4096m -cp bin:lib/nxparser.jar:lib/je-3.3.69.jar:lib/lidaq_web_repos.jar sjtu.apex.gse.experiment.Experiment cfg-web $query $output_file
  done
done
