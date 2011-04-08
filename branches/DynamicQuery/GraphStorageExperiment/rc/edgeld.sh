#!/bin/bash

java -Xmx4096m -cp bin:lib/nxparser.jar:lib/je-3.3.69.jar sjtu.apex.gse.experiment.edge.EdgeLoad data_all_spoc.nq.gz tmp/edge tmp/id
