#!/bin/bash

DEPLOYMENT_FOLDER=$PWD
DEVOPS_FOLDER="$(dirname "$DEPLOYMENT_FOLDER")"
ROOT_FOLDER="$(dirname "$DEVOPS_FOLDER")"
SERVER_FOLDER=$ROOT_FOLDER/server
cat misc/logo.txt

echo ''
read -p "What is the deployment's DNS or IP address: " domain
echo ''
read -p "Do you want a Keycloak enabled deployment [Y/N]: " keycloak_y
echo ''
read -p "Do you want the monitoring stack to be deployed [Y/N]: " monitoring_y
echo ''
echo '================= Building images ================='
echo ''
echo 'Building MariaDB image'
docker build -t sastix/cmsmariadb:1.0 $DEPLOYMENT_FOLDER/mariadb

echo 'Building Sastix-CMS image'
# docker run --rm -v $ROOT_FOLDER:/work -w /work maven:3.6.3-openjdk-11 bash -c "cd /work && mvn clean install -DskipTests && cd server && mvn clean package -DskipTests"
docker build -f $DEPLOYMENT_FOLDER/sxcms/Dockerfile -t sastix/sxcms:1.0 $ROOT_FOLDER/server

echo 'Building NGINX image'
docker build -f $DEPLOYMENT_FOLDER/nginx/Dockerfile -t sxcms-nginx $DEPLOYMENT_FOLDER/nginx

echo '================= Starting database ================='
cd $DEPLOYMENT_FOLDER/mariadb
docker-compose up -d

echo '================= Starting Sastix-CMS server ================='
cd $DEPLOYMENT_FOLDER/sxcms

cp .env.example .env
sed -i "s/DOMAIN/$domain/g" .env

if [ "$keycloak_y" = "Y" ] || [ "$keycloak_y" = "y" ] ; then
    sed -i "s/false/true/g" .env
fi

docker-compose up -d

if [ "$monitoring_y" = "Y" ] || [ "$monitoring_y" = "y" ] ; then
    cd $DEPLOYMENT_FOLDER/prometheus
    docker-compose up -d
    cd $DEPLOYMENT_FOLDER/grafana
    docker-compose up -d
fi

if [ "$keycloak_y" = "Y" ] || [ "$keycloak_y" = "y" ] ; then
    cd $DEPLOYMENT_FOLDER/keycloak
    docker-compose up -d
fi

cd $DEPLOYMENT_FOLDER/nginx
mkdir conf.d

if ([ "$keycloak_y" = "Y" ] || [ "$keycloak_y" = "y" ]) && ([ "$monitoring_y" = "Y" ] || [ "$monitoring_y" = "y" ]) ; then
    cp conf.d.templates/sxcms-with-monitoring-and-auth.conf conf.d/sxcms.conf
    cp conf.d.templates/monitoring.conf conf.d/monitoring.conf
fi

if ([ "$keycloak_y" = "Y" ] || [ "$keycloak_y" = "y" ]) && ([ "$monitoring_y" = "N" ] || [ "$monitoring_y" = "n" ]) ; then
    cp conf.d.templates/sxcms-with-auth.conf conf.d/sxcms.conf
fi

if ([ "$keycloak_y" = "N" ] || [ "$keycloak_y" = "n" ]) && ([ "$monitoring_y" = "Y" ] || [ "$monitoring_y" = "y" ]) ; then
    cp conf.d.templates/sxcms-with-monitoring.conf conf.d/sxcms.conf
    cp conf.d.templates/monitoring.conf conf.d/monitoring.conf
fi

if ([ "$keycloak_y" = "N" ] || [ "$keycloak_y" = "n" ]) && ([ "$monitoring_y" = "N" ] || [ "$monitoring_y" = "n" ]) ; then
    cp conf.d.templates/sxcms-plain.conf conf.d/sxcms.conf
fi

sed -i "s/DOMAIN/$domain/g" conf.d/sxcms.conf

docker-compose up -d