#!/usr/bin/env bash

#SBATCH --job-name=data_preprocess
#SBATCH --output=data_preprocess.log
#SBATCH --ntasks-per-node=40
#SBATCH --nodes=1
#SBATCH --time=04:00:00
#SBATCH -p short-40core


python data_preprocess.py

