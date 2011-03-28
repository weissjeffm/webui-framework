#!/bin/bash
# Copyright (c) 2010 Red Hat, Inc.
#
# This software is licensed to you under the GNU General Public License,
# version 2 (GPLv2). There is NO WARRANTY for this software, express or
# implied, including the implied warranties of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
# along with this software; if not, see
# http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
#
# Red Hat trademarks are not licensed under GPLv2. No permission is
# granted to use or replicate Red Hat trademarks that are incorporated
# in this software or its documentation.
#
# written by whayutin@redhat.com & jmolet@redhat.com

function usage()
{
  echo "This script will reserve a beaker box using bkr workflow-simple."
  echo 
  echo "Available options are:"
  echo "--help                                     Prints this message and then exits"
  echo "--timeout=TIMEOUT                          The timeout in minutes to wait for a beaker box (default: 180)"
  echo 
  echo "The following options are avalable to bkr workflow-simple:"
  echo "--username=USERNAME                        specify user"
  echo "--password=PASSWORD                        specify password"
  echo "--prettyxml                                print the xml in pretty format"
  echo "--debug                                    print the jobxml that it would submit"
  echo "--dryrun                                   Don't submit job to scheduler"
  echo "--arch=ARCH                                Include this Arch in job"
  echo "--distro=DISTRO                            Use this Distro for job"
  echo "--family=FAMILY                            Pick latest distro of this family for job"
  echo "--variant=VARIANT                          Pick distro with this variant for job"
  echo "--machine=MACHINE                          Require this machine for job"
  echo "--package=PACKAGE                          Include tests for Package in job"
  echo "--tag=TAG                                  Pick latest distro matching this tag for job"
  echo "--retention_tag=RETENTION_TAG              Specify data retention policy for this job, defaults to Scratch"
  echo "--repo=REPO                                Include this repo in job"
  echo "--task=TASK                                Include this task in job"
  echo "--taskparam=TASKPARAM                      Set task params 'name=value'"
  echo "--type=TYPE                                Include tasks of this type in job"
  echo "--systype=SYSTYPE                          Specify the System Type (Machine, Laptop, etc..)"
  echo "--keyvalue=KEYVALUE                        Specify a system that matches this key/value Example: NETWORK=e1000"
  echo "--whiteboard=WHITEBOARD                    Set the whiteboard for this job"
  echo "--wait                                     wait on job completion"
  echo "--nowait                                   Do not wait on job completion [Default]"
  echo "--clients=CLIENTS                          Specify how many client hosts to be involved in multihost test"
  echo "--servers=SERVERS                          Specify how many server hosts to be involved in multihost test"
  echo "--install=INSTALL                          Specify Package to install, this will add /distribution/pkginstall."
  echo "--cc=CC                                    Specify additional email addresses to notify"
  echo "--dump                                     Turn on ndnc/kdump. (which one depends on the family)"
  echo "--method=METHOD                            Installation source method (nfs/http) (optional)"
  echo "--priority=PRIORITY                        Set the priority to this (Low,Medium,Normal,High,Urgent) (optional)"
  echo "--kernel_options=KERNEL_OPTIONS            Boot arguments to supply (optional)"
  echo "--kernel_options_post=KERNEL_OPTIONS_POST  Boot arguments to supply post install (optional)"
  echo "--product=PRODUCT                          This should be a unique identifierf or a product"   
}

TIMEOUT="180"
USERNAME=""
PASSWORD=""
ARCH=""
FAMILY=""
TASKS=""
OTHERARGS=""

for i in $*
  do
  case $i in
      --help)
         usage
         exit 0
         ;;
      --timeout=*)
         TIMEOUT=$(echo $i | sed -e s/--timeout=//g)
         ;; 
      --username=*)
         echo "Setting Arg: $i"
         USERNAME=$i
         ;;
      --password=*)
         echo "Setting Password."
         PASSWORD=$i
         ;;
      --arch=*)
         echo "Setting Arg: $i"
         ARCH=$i
         ;;
      --family=*)
        echo "Setting Arg: $i"
        FAMILY=$i
         ;;
      --task=*)
        echo "Adding arg to tasks: $i"
        TASKS=${TASKS}" "$i
        ;;
      *)
        echo "Adding $i to other bkr workflow-simple args."
        OTHERARGS=${OTHERARGS}" "$i
        ;;
  esac
done

#echo "args: $@"
#echo "USERNAME: $USERNAME"
#echo "PASSWORD: $PASSWORD"
#echo "ARCH: $ARCH"
#echo "FAMILY: $FAMILY"
#echo "TASKS: $TASKS"
#echo "OTHERARGS: $OTHERARGS"
#echo "TIMEOUT: $TIMEOUT"


if [[ -z $USERNAME ]] || [[ -z $PASSWORD ]] || [[ -z $ARCH ]] || [[ -z $FAMILY ]] || [[ -z $TASKS ]]  ; then
  echo "bkr workflow-simple requires that a username, password, arch, family, and task be given."
  echo
  usage
  exit 1
fi


bkr workflow-simple $USERNAME $PASSWORD $ARCH $FAMILY $TASKS --task=/distribution/reservesys $OTHERARGS > job || (echo "bkr workflow-simple $USERNAME --password=***** $ARCH $FAMILY $TASK --task=/distribution/reservesys $OTHERARGS " && cat job && exit 1)

echo "===================== JOB DETAILS ================"
echo "bkr workflow-simple $USERNAME --password=***** $ARCH $FAMILY $TASKS --task=/distribution/reservesys $OTHERARGS"
cat job
echo "===================== JOB DETAILS ================"
JOB=`cat job | cut -d \' -f 2`

echo "===================== JOB ID ================"
echo $JOB
echo "===================== JOB ID ================"

echo "===================== PROVISION STATUS ================"
echo "Timeout: $TIMEOUT minutes"
PREV_STATUS="Hasn't Started Yet."
TIME="0"
while [ $TIME -lt $TIMEOUT ]; do
  bkr job-results $JOB $USERNAME $PASSWORD > job-result
  PROVISION_RESULT=$(xmlstarlet sel -t --value-of "//task[@name='/distribution/install']/@result" job-result)
  PROVISION_STATUS=$(xmlstarlet sel -t --value-of "//task[@name='/distribution/install']/@status" job-result)
  if [ $PROVISION_RESULT == "Pass" ]; then
    echo
    echo "Job has completed."
    echo "Provision Status: $PROVISION_STATUS"
    echo "Provision Result: $PROVISION_RESULT"
    break
  elif [ $PROVISION_RESULT == "Warn" ]; then
    echo
    echo "Job FAILED!"
    echo "Provision Status: $PROVISION_STATUS"
    echo "Provision Result: $PROVISION_RESULT"
    exit 1
    break
  elif [ "$PREV_STATUS" == "$PROVISION_STATUS" ]; then
    echo -n "."
    TIME=$(expr $TIME + 1)
    sleep 60
  else 
   echo
   echo "Provision Status: $PROVISION_STATUS"
   echo "Provision Result: $PROVISION_RESULT"
   date
   PREV_STATUS=$PROVISION_STATUS
   echo "Timeout timer reset."
   TIME="0"
   sleep 60
  fi
done
if [[ $TIME -eq $TIMEOUT ]]; then
  echo "Timeout reached."
  bkr job-cancel $JOB $USERNAME $PASSWORD
  exit 1
fi
echo "===================== PROVISION STATUS ================"

JOB_HOSTNAME=`xmlstarlet sel -t --value-of "//recipe/@system" job-result`
rm -Rf hostname
echo "JOB_HOSTNAME = $JOB_HOSTNAME"
echo $JOB_HOSTNAME > hostname

DISTRO=`xmlstarlet sel -t --value-of "//recipe/@distro" job-result`
echo $DISTRO

TASKS=$(echo $TASKS | sed -e s/--task=//g)
for TASK in $TASKS; do
  echo "===================== $TASK STATUS ================"
  PREV_STATUS="Hasn't Started Yet."
  while [ true ]; do
    bkr job-results $JOB $USERNAME $PASSWORD > job-result
    TASK_RESULT=$(xmlstarlet sel -t --value-of "//task[@name='$TASK']/@result" job-result)
    TASK_STATUS=$(xmlstarlet sel -t --value-of "//task[@name='$TASK']/@status" job-result)
    if [ $TASK_RESULT == "Pass" ]; then
      echo
      echo "Job has completed."
      echo "Task Status: $TASK_STATUS"
      echo "Task Result: $TASK_RESULT"
      break
    elif [[ $TASK_RESULT == "Warn" ]] || [[ $TASK_RESULT == "Fail" ]]; then
      EXIT_RESULT=$(xmlstarlet sel -t --value-of "//task[@name='$TASK']/results/result[@path='rhts_task/exit']/@result" job-result)
      if [ $EXIT_RESULT == "Pass" ]; then
        echo
        echo "Job has completed."
        echo "Task Status: $TASK_STATUS"
        echo "Task Result: $EXIT_RESULT"
        break
      else
        echo
        echo "Job FAILED!"
        echo "Task Status: $TASK_STATUS"
        echo "Task Result: $TASK_RESULT"
        exit 1
        break
      fi
    elif [ "$PREV_STATUS" == "$TASK_STATUS" ]; then
      echo -n "."
      sleep 60
    else 
      echo
      echo "Task Status: $TASK_STATUS"
      echo "Task Result: $TASK_RESULT"
      date
      PREV_STATUS=$TASK_STATUS
      sleep 60
    fi
  done
  echo
  echo "===================== $TASK STATUS ================"
done

