#!/bin/bash

mkdir tmp/q/$2
java -Xmx4096m -cp bin:lib/nxparser.jar:lib/lidaq_web_repos.jar:lib/je-3.3.69.jar sjtu.apex.gse.experiment.querygen.ComplexQueryGenerator cfg-test tmp/q/$1 tmp/q/$2 $3 $4
