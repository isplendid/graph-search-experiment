#!/bin/bash

java -Xmx4096m -cp bin:lib/nxparser.jar:lib/lidaq_web_repos.jar:lib/je-3.3.69.jar sjtu.apex.gse.experiment.querygen.SimpleQueryGenerator cfg-test tmp/q/edge.sel tmp/q/init
