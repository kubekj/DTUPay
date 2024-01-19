#!/bin/bash
set -e

# Build and install the libraries
# abstracting away from using the
# RabbitMq message queue
pushd messaging-utilities-3.4
./build.sh
popd 

# Build the services
pushd account-mgmt-service
./build.sh
popd

pushd report-mgmt-service
./build.sh
popd

pushd token-mgmt-service
./build.sh
popd

pushd payment-mgmt-service
./build.sh
popd

pushd facade
./build.sh
popd 
