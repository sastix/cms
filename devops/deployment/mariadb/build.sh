#!/usr/bin/env bash

docker build -t cmsmariadb:1.0 .

docker tag cmsmariadb:1.0 sastix/cmsmariadb:1.0
docker push sastix/cmsmariadb:1.0
