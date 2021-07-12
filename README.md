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

## Use of Keycloak

The default installation sets Keycloak security disabled. To enable Keycloak security:

- Set ```keycloak.enabled=true``` at the application.properties file.
- Configure the other keycloak properties as needed.

Afterwards, the calls get authenticated using the Authentication header in the form of:

```
Authentication: Bearer $JWT_ACCESS_TOKEN
```

### Use of the development environment

A ready to go solution is provided for ease of use during experimenting and developing.

The setup of the mariadb and keycloak for the development environment is done with docker-compose.

```
cd devops/dev-environment
docker-compose up -d
```

This will bring up two containers, one for MariaDB and one for Keycloak which are already provisioned.

!!! Caution
You can change the values of the ports used in the .env file to avoid any conflicts. Default Keycloak and MariaDB passwords can be customized there, too.

Change the required configuration to the server properties (application.properties).

```
cd server
```

You probably want to change the following configuration:

- server.port : the development server's port (default is 9082)
- cms.volume: the folder where the cms will save the resources (you should avoid permission denied issues by specifying a user owned folder)
- spring.datasource.url: by default MariaDB is initialized in docker with a database named sastix_cms_docker. So if the docker-compose instructions are followed as described above the value provided should be jdbc:mysql://localhost:3306/sastix_cms_docker

Spin up the spring-boot development server using:

```
mvn spring-boot:run
```

### Examples

An access and a refresh token is provided by using the corresponding Keycloak endpoint, for example:

```
curl -d "client_id=cms-server" -d "client_secret=6bdcbcb3-457c-4d86-b5c3-5b9cda7198da" -d "username=cms-admin" -d "password=cms-admin" -d "grant_type=password" "http://localhost:8080/auth/realms/CMS/protocol/openid-connect/token"
```
will return as a response body a JSON object with an access and a refresh token.

After the grant of the access and the refresh token, the access token can be used to make authenticated calls:

```
export TOKEN=<access_token>
```

- Check the API version information:

```
curl -X 'GET'   'http://localhost:9082/apiversion'   -H 'accept: application/json' -v -H "Authorization: Bearer $TOKEN"
```

- Create a resource:

```
curl -v -X POST "http://localhost:9082/cms/v1.0/createResource" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"resourceAuthor\": \"Test Author\", \"resourceExternalURI\": \"https://commons.wikimedia.org/wiki/Category:PNG_files#/media/File:Flederspekrp.png\", \"resourceMediaType\": \"image/png\", \"resourceName\": \"logo.png\", \"resourceTenantId\": \"zaq12345\"}" -H "Authorization: Bearer $TOKEN"
```

# Deployment

You can deploy the CMS by running the installation script ```devops/deployment/install-default-setup.sh```. You will be prompted to type:

- The DNS or IP of the server.
- Your preference on using the Keycloak server.
- Your preference on using the monitoring stack.

The only requirement to deploy the solution is to have Docker and docker-compose installed.

After deployment all services can be accessed via the NGINX reverse proxy. In case you have a DNS domain registered and used it during the installation you can get TLS certificates using the command:

```
docker exec -it sxcms-nginx certbot --nginx -d <DOMAIN_NAME>
```

Some significant points of interest are:

- If you have enabled the monitoring stack you can login to the Grafana interface using:

```
DOMAIN_NAME/grafana/
```

You can login using the admin/admin username/password combination and you will be prompted to change the password.
Two dashboards, monitoring the Spring Boot CMS server and the NGINX reverse proxy, are already configured and provisioned.

- If you have enabled Keycloak enabled you can login to the Keycloak admin interface using:

```
DOMAIN_NAME/auth/
```

You can login using the admin/Pa55word username/password combination and we strongly suggest that you change it.

- The three default username/password combinations (cms-admin/cms-admin, cms-creator/cms-creator, cms-consumer/cms-consumer) are already provisioned and we strongly suggest that you change these, too.

- The client secret for the CMS is already set. You can change it in the Keycloak interface and in ```devops/deployment/sxcms/.env``` file and execute ```docker-compose up -d``` to apply the changes.

- You can make requests (authenticated if Keycloak is enabled) to the CMS-API using the base URL

```
DOMAIN_NAME/cms/
```

and

```
DOMAIN_NAME/apiversion/
```

- You can setup custom alerts using the Grafana interface. ( [Grafana Alerts](https://grafana.com/docs/grafana/latest/alerting/old-alerting/create-alerts/) )

## Features in pipeline
- Frontend
- Enable authorized requests and other security features
- User management
- Archive API (delete 'stale' resources from disk but keep metadata in DB)
- Graphql 
- PDF export
- Support Permalinks
