single-host-docker-compose

# Deploy Keycloak

In the ```devops/deployment/keycloak``` folder edit the .env variables and execute:

```
docker-compose up -d
```

This will bring up a Keycloak instance that uses the .env values provided.

# Keycloak configuration

Login to keycloak and create a new realm for the sastix-cms deployment. Create the corresponding client for the CMS server application. Afterwards create the CMS users and assign them with the corresponding roles (admin, creator, consumer).