cat src/main/resources/seawulf.slurm | sshpass -p "mhw1015sz15" ssh -o StrictHostKeyChecking=no borwang@login.seawulf.stonybrook.edu 'source /etc/profile.d/modules.sh; module load slurm; module load anaconda/3; module load mpi4py/3.0.3; cd /gpfs/scratch/borwang/test_ssh_submit; sbatch'