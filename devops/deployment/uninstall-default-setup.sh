#!/bin/bash

DEPLOYMENT_FOLDER=$PWD
DEVOPS_FOLDER="$(dirname "$DEPLOYMENT_FOLDER")"
ROOT_FOLDER="$(dirname "$DEVOPS_FOLDER")"
SERVER_FOLDER=$ROOT_FOLDER/server

cat misc/logo.txt

echo ''
echo 'Uninstalling default setup'
rm -rf $DEPLOYMENT_FOLDER/nginx/conf.d
rm -rf $DEPLOYMENT_FOLDER/nginx/letsencrypt
rm $DEPLOYMENT_FOLDER/sxcms/.env
cd $DEPLOYMENT_FOLDER/nginx
docker-compose down -v
cd $DEPLOYMENT_FOLDER/grafana
docker-compose down -v
cd $DEPLOYMENT_FOLDER/prometheus
docker-compose down -v
cd $DEPLOYMENT_FOLDER/keycloak
docker-compose down -v
cd $DEPLOYMENT_FOLDER/sxcms
docker-compose down -v
cd $DEPLOYMENT_FOLDER/mariadb
docker-compose down -v
