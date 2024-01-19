#!/bin/bash
set -e

pushd messaging-utilities-3.4
mvn clean
popd 

pushd account-mgmt-service 
mvn clean
popd

pushd report-mgmt-service
mvn clean
popd

pushd token-mgmt-service
mvn clean
popd

pushd payment-mgmt-service
mvn clean
popd

pushd facade
mvn clean
popd 
