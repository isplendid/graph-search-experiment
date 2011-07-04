EXPECTED_ARGS=4

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage : `basename $0` <root path> <output filename> <mining frequency> <selection frequency>"
  exit $E_BADARGS
fi
query_root=$1
out_file=$2
freq=$3
freq_sel=$4
java -Xmx4096m -cp bin:lib/je-3.3.69.jar sjtu.apex.gse.exp.sgm.ext.ExternalSubgraphMining cfg-web-3trim $query_root $out_file tmp/sgm/gSpan $freq $freq_sel
