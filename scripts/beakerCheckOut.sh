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
           echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" 
	         echo "Please use all options"
	         echo ""
           echo " This script will reserve a beaker box  "
           echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" 
           echo ""
           echo "Arguments will be passed to bkr workflow-simple.  Available options are:"
           echo "--username=         :: Your beaker username."
           echo "--password=         :: Your beaker password."
           echo "--arch=             :: i386, x86_64 etc."
           echo "--family=           :: DISTRO FAMILY, RedHatEnterprixeLinux4/5/6"
           echo "--tag=              :: TAG, RELEASED,STABLE"  
           echo "--variant=          :: RHEL6 has Server,Workstation,ComputeNode,Client"
           echo "--task=             :: Additional beaker task executed after provision"       
}

USERNAME=""
PASSWORD=""
ARCH=""
FAMILY=""
TAG=""
VARIANT=""
TASK=""

for i in $*
 do
 case $i in
      --username=*)
         echo "Setting arg: $i"
         USERNAME=$i
         ;;
      --password=*)
         echo "Setting password"
         PASSWORD=$i
         ;;
      --arch=*)
         echo "Setting arg: $i"
         ARCH=$i
         ;;
      --family=*)
        echo "Setting arg: $i"
        FAMILY=$i
         ;;
      --tag=*)
        echo "Setting arg: $i"
        TAG=$i
          ;;
      --variant=*)
        echo "Setting arg: $i"
        VARIANT=$i
          ;;
      --task=*)
        echo "Adding arg to tasks: $i"
        TASK=${TASK}" "$i
          ;;
      *)
        echo "Unknown Option: $i"
         usage
        exit 1
        ;;
 esac
done


if [[ -z $USERNAME ]] || [[ -z $PASSWORD ]] || [[ -z $ARCH ]] || [[ -z $FAMILY ]] || [[ -z $TAG ]] || [[ -z $VARIANT ]] || [[ -z $TASK ]]  ; then
 usage
 exit 1
fi


bkr workflow-simple $USERNAME $PASSWORD $ARCH $FAMILY $TAG $VARIANT $TASK --task=/distribution/reservesys > job || (echo "bkr workflow-simple $USERNAME --password=***** $ARCH $FAMILY $TAG $VARIANT $TASK --task=/distribution/reservesys " && cat job && exit 1)

echo "===================== JOB DETAILS ================"
echo "bkr workflow-simple $USERNAME --password=***** $ARCH $FAMILY $TAG $VARIANT $TASK --task=/distribution/reservesys "
cat job
echo "===================== JOB DETAILS ================"
JOB=`cat job | cut -d \' -f 2`

echo "===================== JOB ID ================"
echo $JOB
echo "===================== JOB ID ================"

pwd


bkr job-results $JOB $USERNAME $PASSWORD > job-result
PROVISION_RESULT=`xmlstarlet sel -t --value-of "//task[@name='/distribution/install']/@result" job-result`
PROVISION_STATUS=`xmlstarlet sel -t --value-of "//task[@name='/distribution/install']/@status" job-result`
echo "===================== PROVISION STATUS ================"
PREV_STATUS="Hasn't Started Yet."
while [ $PROVISION_RESULT != "Pass" ] || [ $PROVISION_RESULT != "Warn" ];
do
 bkr job-results $JOB $USERNAME $PASSWORD > job-result
 PROVISION_RESULT=$(xmlstarlet sel -t --value-of "//task[@name='/distribution/install']/@result" job-result)
 PROVISION_STATUS=$(xmlstarlet sel -t --value-of "//task[@name='/distribution/install']/@status" job-result)
 if [ "$PREV_STATUS" == "$PROVISION_STATUS" ]; then
    echo -n "."
    sleep 60
 elif [ $PROVISION_STATUS == "Running" ]; then
    echo
    echo "RUNNING"
    echo "Provision Status: $PROVISION_STATUS"
    echo "Provision Result: $PROVISION_RESULT"
    date
    PREV_STATUS=$PROVISION_STATUS
    sleep 60
 elif [ $PROVISION_STATUS == "None" ]; then
    echo
    echo "RESULT = None: JOB is running"
    date
    PREV_STATUS=$PROVISION_STATUS
    sleep 60
 elif [ $PROVISION_RESULT == "Pass" ]; then
    echo
    echo "RESULT = Pass: JOB has completed"
    break
 elif [ $PROVISION_RESULT == "Warn" ]; then
    echo
    echo "RESULT = WARN: JOB FAILED"
    exit 1
    break
 else 
   echo
   echo "Provision Status: $PROVISION_STATUS"
   echo "Provision Result: $PROVISION_RESULT"
   date
   PREV_STATUS=$PROVISION_STATUS
   sleep 60
 fi
done
echo "===================== PROVISION STATUS ================"




SETUP_RESULT=`xmlstarlet sel -t --value-of "//task[@name='/CoreOS/rhsm/Install/subscription-manager-env']/@result" job-result`
SETUP_STATUS=`xmlstarlet sel -t --value-of "//task[@name='/CoreOS/rhsm/Install/subscription-manager-env']/@status" job-result`
echo "===================== AUTOMATION PREREQ STATUS ================"
PREV_STATUS="Hasn't Started Yet."
while [ $SETUP_RESULT != "Pass" ] || [ $SETUP_RESULT != "Warn" ];
do
 bkr job-results $JOB $USERNAME $PASSWORD > job-result
 SETUP_RESULT=$(xmlstarlet sel -t --value-of "//task[@name='/CoreOS/rhsm/Install/subscription-manager-env']/@result" job-result)
 SETUP_STATUS=$(xmlstarlet sel -t --value-of "//task[@name='/CoreOS/rhsm/Install/subscription-manager-env']/@status" job-result)
 if [ "$PREV_STATUS" == "$SETUP_STATUS" ]; then
    echo -n "."
    sleep 60
 elif [ $SETUP_STATUS == "Running" ]; then
    echo 
    echo "RUNNING"
    date
    PREV_STATUS=$SETUP_STATUS
    sleep 60
 elif [ $SETUP_STATUS == "None" ]; then
    echo
    echo "RESULT = None: JOB is running"
    date
    PREV_STATUS=$SETUP_STATUS
    sleep 60
 elif [ $SETUP_RESULT == "Pass" ]; then
    echo
    echo "RESULT = Pass: JOB has completed"
    break
 elif [ $SETUP_RESULT == "Warn" ]; then
    echo
    echo "RESULT = WARN: JOB FAILED"
    exit 1
    break
 else 
    echo
    echo "Setup Status: $SETUP_STATUS"
    echo "Setup Result: $SETUP_RESULT"
    date
    PREV_STATUS=$SETUP_STATUS
    sleep 60
 fi
done
echo
echo "===================== AUTOMATION PREREQ STATUS ================"

HOSTNAME=`xmlstarlet sel -t --value-of "//recipe/@system" job-result`
echo "HOSTNAME = $HOSTNAME"
echo $HOSTNAME > hostname
