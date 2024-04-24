#!/usr/bin/env bash

# Test we can access the db container allowing for start
for i in {1..50}; do mysql -u root -p${MYSQL_ROOT_PASSWORD} -h db -e "show databases" && s=0 && break || s=$? && sleep 2; done
if [ ! $s -eq 0 ]; then exit $s; fi

# Load patches
mysql -u root -p${MYSQL_ROOT_PASSWORD} <

