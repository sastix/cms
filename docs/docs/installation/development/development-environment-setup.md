---
sidebar_position: 1
---

# Docker setup

## Prerequisites

To be able to use the development environment the following utilities
should be available:

- Docker.
- docker-compose.
- Maven.
- A Java 11 JDK.
- Flutter.

## Clone the repository and build the project

```
git clone https://github.com/eellak/gsoc2021-sastixcms
cd gsoc2021-sastixcms
mvn clean install
```

## Setup MariaDB and Keycloak with docker-compose

The setup of MariaDB and Keycloak is done using docker-compose.

```
cd devops/dev-environment
docker-compose up -d
```

This will bring up two containers, one for MariaDB and one for
Keycloak which are already provisioned.

:::caution

You can change the values of the ports used in the .env file to
avoid any conflicts. Default Keycloak and MariaDB passwords can be customized there, too.

:::

## Change the required configuration to the server properties

You can change the server configuration by editing the `application.properties` files in
the `src/main/resources` of the `server` folder.

:::info

You should to change the following values:
- server.port: the development server's port (default is 9082).
- cms.volume: the folder where the cms will save the resources (you should avoid permission
issues by specifying a user owned folder).
- spring.datasource.url: by default MariaDB is initialized in docker with a database named
sastix_cms_docker. So if the docker-compose instructions are followed as described above
the value provided should be jdbc:mysql://localhost:3306/sastix_cms_docker.
- keycloak.enabled: Set to true to use the default Keycloak configuration. If this is not
changed the server will accept unauthenticated requests.

:::

## Spin up the spring-boot development server

In the `server` folder use:

```
mvn spring-boot:run
```

## Spin up the Flutter client

To run the Flutter client one needs to have Flutter installed on
one's system. The installation is described in the
[documentation](https://flutter.dev/docs/get-started/install). After
installing Flutter you need to set the environment variables in the
`assets/config/.env` file. Copying from the
`assets/config/.env.example` file will start the Flutter application
using the default values for the Docker development environment.

:::caution

The FLUTTER_CLIENT_ID must be the same as the server Keycloak client.
The default development environment for it is `cms-server`.

:::

Flutter can work with devices, emulators and browsers. For example
the Chrome browser can be used by setting the `CHROME_EXECUTABLE`
environment variable to the Chrome executable.

After that the Flutter application for the web can be started with:

```
cd fclient
flutter pub get
flutter run -d chrome
```

## Example usage

### Default Keycloak configuration

By using the default docker-compose (.env) values you could acquire a JWT token by:

```
curl -d "client_id=cms-server" -d "client_secret=6bdcbcb3-457c-4d86-b5c3-5b9cda7198da" -d "username=cms-admin" -d "password=cms-admin" -d "grant_type=password" "http://localhost:8080/auth/realms/CMS/protocol/openid-connect/token"
```

The default username/password combinations in the CMS realm are:

```
cms-admin / cms-admin , with role admin
cms-creator / cms-creator, with role creator
cms-consumer / cms-consumer, with role consumer
```

:::danger

The default configuration should never be used in production or on a publicly
available URL. This is a major security issue.

:::

### Make an authenticated request

The call above will provide a JSON response with an access JWT token and a refresh JWT token.

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

The requests are authenticated by checking the "Authorization: Bearer <access_token>" header. For more information refer to the authentication notes.

### Login to the Swagger interface

The Swagger Interface is exposed to ```http://localhost:9082/swagger-ui.html```.

If Keycloak is enabled you will be redirected to the Keycloak interface
and after you login you will be able to use the API interface to make authenticated requests.