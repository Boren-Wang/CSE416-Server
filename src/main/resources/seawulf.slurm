#!/usr/bin/env bash

#SBATCH --job-name=job
#SBATCH --output=%j.log
#SBATCH --ntasks-per-node=24
#SBATCH --nodes=2
#SBATCH --time=12:00:00
#SBATCH --partition=long-24core

echo I am using the following nodes: $SLURM_JOB_NODELIST
echo My job ID is: $SLURM_JOB_ID

cat << EOF > mpi.py

import os
import time
import sys

from mpi4py import MPI

start_time = time.time()

comm = MPI.COMM_WORLD
size = comm.Get_size()
rank = comm.Get_rank()

# python main.py GEORGIA 80 0.03 0.5
state = sys.argv[1]
numberOfDistrictings = int(sys.argv[2])
populationDifference = sys.argv[3]
compactnessGoal = sys.argv[4]
jobId = sys.argv[5]

part1 = numberOfDistrictings // 2
part2 = (numberOfDistrictings // 2) + (numberOfDistrictings % 2)

print("This is node {}".format(rank))

if rank == 0:
    numberOfDistrictings = part1
    print("Number of districtings for node {} is {}".format(rank, part1))
    command = "time python ./algorithm/main_seawulf.py"+" "+jobId+" "+state+" "+str(numberOfDistrictings)+" "+populationDifference+" "+compactnessGoal+" "+str(rank)
    os.system(command)

if rank == 1:
    numberOfDistrictings = part2
    print("Number of districtings for node {} is {}".format(rank, part2))
    command = "time python ./algorithm/main_seawulf.py"+" "+jobId+" "+state+" "+str(numberOfDistrictings)+" "+populationDifference+" "+compactnessGoal+" "+str(rank)
    os.system(command)

comm.barrier()
print("Completed in {:.2f}s for rank {}:".format(time.time() - start_time, rank))
EOF

mpirun -np 2 python mpi.py $1 $2 $3 $4 $SLURM_JOB_ID
