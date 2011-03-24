#!/bin/bash

URL="http://hudson.rhq.lab.eng.bos.redhat.com:8080/hudson/view/Entitlement/job/rhsm-beaker-on-premises/build"

#rhel6-server-x86_64
JSON[0]="{\"parameter\": [{\"name\": \"CLIENT1_ARCH\",         \"value\": \"x86_64\"} , \
                          {\"name\": \"CLIENT1_DistroFamily\", \"value\": \"RedHatEnterpriseLinux6\"} , \
                          {\"name\": \"CLIENT1_VARIANT\",      \"value\": \"Server\"} , \
          ], \"\": \"\"}"

#rhel6-client-x86_64
JSON[1]="{\"parameter\": [{\"name\": \"CLIENT1_ARCH\",         \"value\": \"x86_64\"} , \
                          {\"name\": \"CLIENT1_DistroFamily\", \"value\": \"RedHatEnterpriseLinux6\"} , \
                          {\"name\": \"CLIENT1_VARIANT\",      \"value\": \"Client\"} , \
          ], \"\": \"\"}"

#rhel6-workstation-x86_64
JSON[2]="{\"parameter\": [{\"name\": \"CLIENT1_ARCH\",         \"value\": \"x86_64\"} , \
                          {\"name\": \"CLIENT1_DistroFamily\", \"value\": \"RedHatEnterpriseLinux6\"} , \
                          {\"name\": \"CLIENT1_VARIANT\",      \"value\": \"Workstation\"} , \
          ], \"\": \"\"}"

#rhel6-computenode-x86_64
JSON[3]="{\"parameter\": [{\"name\": \"CLIENT1_ARCH\",         \"value\": \"x86_64\"} , \
                          {\"name\": \"CLIENT1_DistroFamily\", \"value\": \"RedHatEnterpriseLinux6\"} , \
                          {\"name\": \"CLIENT1_VARIANT\",      \"value\": \"ComputeNode\"} , \
          ], \"\": \"\"}"

#rhel6-server-i386
JSON[4]="{\"parameter\": [{\"name\": \"CLIENT1_ARCH\",         \"value\": \"i386\"} , \
                          {\"name\": \"CLIENT1_DistroFamily\", \"value\": \"RedHatEnterpriseLinux6\"} , \
                          {\"name\": \"CLIENT1_VARIANT\",      \"value\": \"Server\"} , \
          ], \"\": \"\"}"

#rhel6-client-i386
JSON[5]="{\"parameter\": [{\"name\": \"CLIENT1_ARCH\",         \"value\": \"i386\"} , \
                          {\"name\": \"CLIENT1_DistroFamily\", \"value\": \"RedHatEnterpriseLinux6\"} , \
                          {\"name\": \"CLIENT1_VARIANT\",      \"value\": \"Client\"} , \
          ], \"\": \"\"}"

#rhel6-workstation-i386
JSON[6]="{\"parameter\": [{\"name\": \"CLIENT1_ARCH\",         \"value\": \"i386\"} , \
                          {\"name\": \"CLIENT1_DistroFamily\", \"value\": \"RedHatEnterpriseLinux6\"} , \
                          {\"name\": \"CLIENT1_VARIANT\",      \"value\": \"Workstation\"} , \
          ], \"\": \"\"}"

#rhel6-server-ppc64
JSON[7]="{\"parameter\": [{\"name\": \"CLIENT1_ARCH\",         \"value\": \"ppc64\"} , \
                          {\"name\": \"CLIENT1_DistroFamily\", \"value\": \"RedHatEnterpriseLinux6\"} , \
                          {\"name\": \"CLIENT1_VARIANT\",      \"value\": \"Server\"} , \
          ], \"\": \"\"}"

#rhel6-server-s390x
JSON[8]="{\"parameter\": [{\"name\": \"CLIENT1_ARCH\",         \"value\": \"s390x\"} , \
                          {\"name\": \"CLIENT1_DistroFamily\", \"value\": \"RedHatEnterpriseLinux6\"} , \
                          {\"name\": \"CLIENT1_VARIANT\",      \"value\": \"Server\"} , \
          ], \"\": \"\"}"


for i in `seq 0 $(expr ${#JSON[@]} - 1)`; do
  echo $job
  echo "Queing Job:  curl -X POST $URL -d token=hudsonbeaker-remote --data-urlencode json='${JSON[$i]}'"
  curl -X POST $URL -d token=hudsonbeaker-remote --data-urlencode json="${JSON[$i]}"
  echo
done


##rhel6-server-ia64
#JSON[7]="{\"parameter\": [{\"name\": \"CLIENT1_ARCH\",         \"value\": \"ia64\"} , \
#                          {\"name\": \"CLIENT1_DistroFamily\", \"value\": \"RedHatEnterpriseLinux6\"} , \
#                          {\"name\": \"CLIENT1_VARIANT\",      \"value\": \"Server\"} , \
#          ], \"\": \"\"}"
