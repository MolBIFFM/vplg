#!/bin/bash
# start_all_jobs_on_all_nodes_for_whole_PDB.sh -- submit the whole PDB to the pbs queue
# 
# Run this script from the shell after preparing the MPI-version of the PDB file lists.
#
# This script does not require any parameters, so just do: './pbs_start_all_jobs_on_all_nodes_whole_PDB.sh'
#
# written by ts, 2013
#

APPTAG="[START_ALL_MPI]"
MYHOME="/home/ts"
PLCC_CLUSTER_DIR="$MYHOME/software/plcc_cluster"

DEBUG_START_ALL_MPI="false"

## settings
SINGLE_JOB_SCRIPT="pbs_start_vplg_via_mpi4py_jobs_for_one_node.sh"

echo "$APPTAG ----- Starting all jobs via openpbs -----"
echo "$APPTAG Assuming plcc_cluster is installed at '$PLCC_CLUSTER_DIR'."
echo "$APPTAG Preparing to submit all jobs to the openpbs queue..."

FIRST_FILE_LIST="$PLCC_CLUSTER_DIR/status/MPIfilelistnum000"
if [ ! -f "$FIRST_FILE_LIST" ]; then
    echo "$APPTAG ERROR: Could not find the first PDB file list at '$FIRST_FILE_LIST'. Did you run the script to generate the lists?"
    exit 1
fi


if [ "$DEBUG_START_ALL_MPI" = "true" ]; then
	  FLIST="$PLCC_CLUSTER_DIR/status/MPIfilelistnum001"
	  ENV_VARIABLES="PDBFILELIST=$FLIST"
	  CMD="qsub $SINGLE_JOB_SCRIPT -v $ENV_VARIABLES"
	  echo "$APPTAG DEBUG: Submitting job: '$CMD'"
	  $CMD
	echo "$APPTAG WARNING: DEBUG active, submitted only the first job file, all others were ignored."
else
	## submit a job for each file list
	for FLIST in $PLCC_CLUSTER_DIR/status/MPIfilelistnum*;
	do
	  ENV_VARIABLES="PDBFILELIST=$FLIST"
	  CMD="qsub $SINGLE_JOB_SCRIPT -v $ENV_VARIABLES"
	  echo "$APPTAG Submitting job: '$CMD'"
	  $CMD
	done

	echo "$APPTAG Submitted all protein graph computation jobs."
fi


#GRAPHLETSIM_JOB_SCRIPT="pbs_start_vplg_graphletsimilarity_whole_db.sh"
## Note: we should only submit the following after all jobs have FINISHED, not after all jobs have been submitted. Therefore, I comment this out for now.
#CMDGRAPHLETSIM="qsub $GRAPHLETSIM_JOB_SCRIPT"
#echo "$APPTAG Submitting job: '$CMDGRAPHLETSIM'"
#$CMDGRAPHLETSIM
#echo "$APPTAG Submitted final job to compute all graphlet similarites."

echo "$APPTAG Submitted all jobs. Exiting."

exit
