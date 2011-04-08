#!/bin/bash

java -cp bin:lib/je-3.3.69.jar -Xmx4096m sjtu.apex.gse.experiment.querygen.RandomEdgeSelection cfg-test tmp/q/edge.sel
