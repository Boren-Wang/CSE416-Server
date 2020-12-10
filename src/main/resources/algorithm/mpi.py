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
numberOfDistrictings = int(sys.argv[2])
populationDifference = sys.argv[3]
compactnessGoal = sys.argv[4]

part1 = numberOfDistrictings // 2
part2 = (numberOfDistrictings // 2) + (numberOfDistrictings % 2)

print("This is node {}".format(rank))

if rank == 0:
    numberOfDistrictings = part1
    print("Number of districtings for node {} is {}".format(rank, part1))
    command = "time python ./algorithm/main_seawulf.py"+" "+state+" "+str(numberOfDistrictings)+" "+compactnessGoal+" "+compactnessGoal
    os.system(command)

if rank == 1:
    numberOfDistrictings = part2
    print("Number of districtings for node {} is {}".format(rank, part2))
    command = "time python ./algorithm/main_seawulf.py"+" "+state+" "+str(numberOfDistrictings)+" "+compactnessGoal+" "+compactnessGoal
    os.system(command)

comm.barrier()
print("Completed in {:.2f}s for rank {}:".format(time.time() - start_time, rank))