#!/usr/bin/env bash

#SBATCH --job-name=test_ssh_submit
#SBATCH --output=test_ssh_submit-latest.log
#SBATCH --ntasks-per-node=40
#SBATCH --nodes=2
#SBATCH --time=00:05:00
#SBATCH --partition=short-40core

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

# python main.py GEORGIA 5000 100 0.9
state = sys.argv[1]
numberOfDistrictings = sys.argv[2]
populationDifference = sys.argv[3]
compactnessGoal = sys.argv[4]

print("This is node {}".format(rank))

if rank == 0:
    print("We have {} nodes in our cluster.".format(size))
    nodes = [x for x in range(size)]
    print('We will be scattering files across nodes:', nodes) # The file is main.py

data = [x for x in range(numberOfDistrictings)]
data = comm.scatter(data, root=0)
print('rank', rank, 'has data:', data)

if rank != 0:
    for i in data:
        print("Starting generating districting plan #{}".format(i))
        command = "time python main.py"+" "+state+" "+numberOfDistrictings+" "+compactnessGoal+" "+compactnessGoal
        os.system(command)

comm.barrier()
print("Completed in {:.2f}s for rank {}:".format(time.time() - start_time, rank))
EOF

mpirun -np 2 python mpi.py $1 $2 $3