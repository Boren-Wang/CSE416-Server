#!/usr/bin/env bash

sshpass -p "mhw1015sz15" scp borwang@login.seawulf.stonybrook.edu:"$1"_0.json src/main/resources/results/
sshpass -p "mhw1015sz15" scp borwang@login.seawulf.stonybrook.edu:"$1"_1.json src/main/resources/results/
sshpass -p "mhw1015sz15" scp borwang@login.seawulf.stonybrook.edu:"$1".log src/main/resources/results/
