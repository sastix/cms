setup

# Clone the repository and build the project

```
git clone https://github.com/sastix/cms.git
cd cms
mvn clean install
```

# mariadb, keycloak with docker-compose

Setup of the mariadb and keycloak for the development environment is done with docker-compose.

```
cd devops/dev-environment
docker-compose up -d
```

This will bring up two containers, one for MariaDB and one for Keycloak which are already provisioned.

!!! Caution
You can change the values of the ports used in the .env file to avoid any conflicts. Default Keycloak and MariaDB passwords can be customized there, too.

# Change the required configuration to the server properties

```
cd server
```

You probably want to change the following configuration:

- server.port : the development server's port (default is 9082)
- cms.volume: the folder where the cms will save the resources (you should avoid permission denied issues by specifying a user owned folder)
- spring.datasource.url: by default MariaDB is initialized in docker with a database named sastix_cms_docker. So if the docker-compose instructions are followed as described above the value provided should be jdbc:mysql://localhost:3306/sastix_cms_docker

# Spin up the spring-boot development server

In the server folder use

```
mvn spring-boot:run
```

# Example usage

## Get a Keycloak token

By using the default docker-compose (.env) values you could acquire a JWT token by:

```
curl -d "client_id=cms-server" -d "client_secret=6bdcbcb3-457c-4d86-b5c3-5b9cda7198da" -d "username=cms-admin" -d "password=cms-admin" -d "grant_type=password" "http://localhost:8080/auth/realms/CMS/protocol/openid-connect/token"
```

The default user/password combinations in the CMS realm are:

```
cms-admin / cms-admin , with role admin
cms-creator / cms-creator, with role creator
cms-consumer / cms-consumer, with role consumer
```

## Make an authenticated request

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

## Login to the Swagger interface

Head to ```http://localhost:9082/swagger-ui.html``` . You will be redirected to the Keycloak interface and after you login you will be able to use the API interface to make authenticated requests.