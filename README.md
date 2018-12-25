[![Build Status](https://travis-ci.org/sastix/cms.svg?branch=master)](https://travis-ci.org/sastix/cms)
# Sastix CMS

Sastix CMS is a Spring Boot application having several content management features. It exposes specific REST APIs to store, cache and lock content (resources). A resource can be a simple html file, an image, a video or any other file. Under common/api package you can find the API interfaces used in the project. It is a good place to start looking. 

These APIs have two kind of implementations: A client service and server service.The client can be used/imported as a separated library/dependency in any project and will provide all the rest calls needed towards the underlying server for managing and retrieving the content through specific locking and caching mechanisms. The relative code is under “client” project. The server side service implements the same interfaces and can have additional implementations needed for the core CMS platform. The relative code is under “server” project.

In order to be able to run locally the Sastix CMS server you should have at least jdk 1.8 installed
and a MySQL instance running. Sastix CMS is using ORM and you can find the relative
configuration under application.properties. You should define a schema in your database in
order to be able to run the platform successfully. 

## Features
- REST APIs
- Versioning
- Dictributed Caching
- Distributed Locking
- Distributed Unique ID generator
- Multi-node support for scaling

## Easy start using docker
At first you will need to have installed docker and docker-compose. Under ubuntu this can be done using commands:
```
$ sudo apt  install docker.io
$ sudo apt install docker-compose
# Run the following to enable non root user to run docker
$ sudo usermod -aG docker ${USER}
$ su - ${USER}
$ id -nG
```
You can use two existing docker images to easily start. You will need a MariaDB instance and the Spring Boot CMS. More info for these images can be found here:
- https://hub.docker.com/r/sastix/cmsmariadb 
- https://hub.docker.com/r/sastix/sxcms

Checkout Sastix CMS repo locally and locate the folder devops/deployment:
https://github.com/sastix/cms/tree/develop/devops/deployment

You will find two folders, mariadb and sxcms. Inside you will find a docker-compose.yml for each one.

First compose the mariadb to start the DB server and a management web interface (adminer):
```
$ cd devops/deployment/mariadb
$ docker-compose pull
$ docker-compose up -d
# If you want to see logs run:
$ docker-compose logs -f
```

This compose file will start a mariadb instance at port 3306 and an adminer at 8080. From a browser go to:
http://localhost:8080 
and use these credentials:
- username: root
- password: sastixcms

If connected successfully, you will also see a database with the name: sastix_cms_docker

To test the connection with a mysql client, open a terminal and run:
```
mysql -u root -p -h 127.0.0.1
```

When asked for a password use the same with before: sastixcms

Now that you hve the DB up and running you can start the Spring Boot app:
```
$ cd devops/deployment/sxcms
$ docker-compose pull
$ docker-compose up -d
# If you want to see logs run:
$ docker-compose logs -f
```

Check the version of Sastix CMS through the browser or curl from a terminal:
```
curl http://localhost:9082/apiversion
```

If everything is up and running you will get this response:
```json
{"minVersion":1.0,"maxVersion":1.0,"versionContexts":{"1.0":"/cms/v1.0"}}
```

## APIs explained

TBD

### Examples


## Features in pipeline
- Frontend
- Archive API (delete 'stale' resources from disk but keep metadata in DB)
- Graphql 
- PDF export
- Support Permalinks