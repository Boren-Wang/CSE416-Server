echo "$1"
sshpass -p "mhw1015sz15" ssh -o StrictHostKeyChecking=no borwang@login.seawulf.stonybrook.edu 'source /etc/profile.d/modules.sh; module load slurm; scancel '"$1"''