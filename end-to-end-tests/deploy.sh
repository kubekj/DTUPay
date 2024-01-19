#!/bin/bash
set -e
docker image prune -f
docker-compose up -d rabbitMq
sleep 10
docker-compose up -d facade account-mgmt-service report-mgmt-service token-mgmt-service payment-mgmt-service

