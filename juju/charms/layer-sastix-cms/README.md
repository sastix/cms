[![Build Status](https://travis-ci.org/sastix/cms.svg?branch=master)](https://travis-ci.org/sastix/cms)
# Spring Boot CMS
A general purpose java Content Management System based on Spring Boot

## Features

- Versioning
- Caching
- Distributed Unique id generator
- Clustering
- REST api
- PDF export

## Usage

You need to deploy this CMS and relate it to a database:

	juju deploy cs:~sastix/sastix-cms
        juju deploy mariadb
        juju add-relation mariadb sastix-cms
        juju expose sastix-cms

Sastix CMS should now be available at http://<sastix-cms>:9082


