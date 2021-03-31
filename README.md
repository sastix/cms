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
- Distributed Caching
- Distributed Locking
- Distributed Unique ID generator
- Multi-node support for scaling

## Easy start using docker
At first you will need to have installed docker and docker-compose. Under ubuntu this can be done using these commands:
```
$ sudo apt  install docker.io
$ sudo apt install docker-compose
# Run the following to enable non root user to run docker
$ sudo usermod -aG docker ${USER}
$ su - ${USER}
$ id -nG
```
You can use the following docker images to easily start:
- https://hub.docker.com/r/sastix/cmsmariadb 
- https://hub.docker.com/r/sastix/sxcms

Checkout Sastix CMS git-repo locally and locate the folder devops/deployment:
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

When asked for a password use the same with before: `sastixcms`

Now that you have the DB up and running you can start the Spring Boot app:
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

Swagger has been integrated so you can easily find the available APIs and try them. Follow this url after starting the server:
- http://localhost:9082/swagger-ui.html

Sastix CMS controllers:
- [api-version-controller](http://localhost:9082/swagger-ui.html#/api-version-controller)
- [cache-controller](http://localhost:9082/swagger-ui.html#/api-version-controller)
- [lock-controller](http://localhost:9082/swagger-ui.html#/lock-controller)
- [resource-controller](http://localhost:9082/swagger-ui.html#/resource-controller)


### Examples

#### Create a resource using swagger-ui or curl

Follow this link: http://localhost:9082/swagger-ui.html#/resource-controller and collapse the API menu: POST /cms/v1.0/createResource

Click the button on the right "Try it out".

Under CreateResourceDTO param, insert:

```json
{
  "resourceAuthor": "Test Author",
  "resourceExternalURI": "https://commons.wikimedia.org/wiki/Category:PNG_files#/media/File:Flederspekrp.png",
  "resourceMediaType": "image/png",
  "resourceName": "logo.png",
  "resourceTenantId": "zaq12345"
}
```

The curl alternative is:
```
curl -X POST "http://localhost:9082/cms/v1.0/createResource" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"resourceAuthor\": \"Test Author\", \"resourceExternalURI\": \"https://commons.wikimedia.org/wiki/Category:PNG_files#/media/File:Flederspekrp.png\", \"resourceMediaType\": \"image/png\", \"resourceName\": \"logo.png\", \"resourceTenantId\": \"zaq12345\"}"
```

Execute and you will get the following response:

```json
{
  "resourceUID": "d6b4a0c8-zaq12345",
  "author": "Test Author",
  "resourceURI": "a28d4846-zaq12345/logo.png",
  "resourcesList": null
}
```

In order to test the created resource you can open a browser and follow the link where you include the resourceURI:

```
http://localhost:9082/cms/v1.0/getData/a28d4846-zaq12345/logo.png
```

## Features in pipeline
- Frontend
- Enable authorized requests and other security features
- User management
- Archive API (delete 'stale' resources from disk but keep metadata in DB)
- Graphql 
- PDF export
- Support Permalinks
