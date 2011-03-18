#!/bin/bash
#

query_folder=tmp/q/init
result_folder=tmp/res/init

if [ ! -d $result_folder ]
then
  mkdir $result_folder
fi

for query in $query_folder/*
do
  base_name=`basename $query`
  output_file=$result_folder/$base_name

  echo Running $query to $output_file
  java -Xmx400m -cp bin:lib/nxparser.jar:lib/je-3.3.69.jar:lib/lidaq_web_repos.jar sjtu.apex.gse.experiment.Experiment cfg-web $query $output_file 
done
