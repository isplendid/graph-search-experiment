#!/bin/bash
#

EXPECTED_ARGS=4

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage : `basename $0` <query folder> <result folder> <out folder> <index number>"
  exit $E_BADARGS
fi

query_folder=$1
result_folder=$2
out_folder=$3
index_no=$4

if [ ! -d $result_folder ]
then
  mkdir $result_folder
fi

if [ ! -d $out_folder ]
then
  mkdir $out_folder
fi

for qf in $query_folder/*
do
  qf_bn=`basename $qf`
  result_path=$result_folder/$qf_bn
  out_path=$out_folder/$qf_bn

  if [ ! -d $result_path ]
  then
    mkdir $result_path
  fi

  if [ ! -d $out_path ]
  then
    mkdir $out_path
  fi

  for query in $qf/*
  do
    if [ ! -f $query ]
    then
      continue
    fi
    base_name=`basename $query`
    result_file=$result_path/$base_name
    output_file=$out_path/$base_name

    if [ -f $result_file ]
    then
      continue
    fi

    echo Running $query to $result_file
    timeout -s 9 180s java -Xmx4096m -cp bin:lib/nxparser.jar:lib/je-3.3.69.jar:lib/lidaq_web_repos.jar:lib/openrdf-sesame-2.3.2-onejar.jar sjtu.apex.gse.exp.Experiment cfg-web-cmplx$index_no $query $result_file $output_file > /dev/null 2>&1
  done
done
