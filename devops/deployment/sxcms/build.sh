#!/usr/bin/env bash

docker build -t sxcms:1.0 .

docker tag sxcms:1.0 sastix/sxcms:1.0
docker push sastix/sxcms:1.0
