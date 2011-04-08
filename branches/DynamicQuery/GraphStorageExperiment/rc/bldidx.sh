#!/bin/bash

java -Xmx4096m -cp bin:lib/nxparser.jar:lib/je-3.3.69.jar sjtu.apex.gse.indexer.file.AtomicPatternIndexService cfg-test data_all_spoc.nq.gz
