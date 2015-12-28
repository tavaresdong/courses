#!/bin/bash

set -e

python ibm_model1.py

python ibm_model2.py

python eval_alignment.py dev.key ibm1.out

python eval_alignment.py dev.key ibm2.out
