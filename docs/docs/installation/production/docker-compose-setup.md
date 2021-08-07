---
sidebar_position: 2
---

# Setup with docker-compose

You can deploy the CMS by running the installation script
```devops/deployment/install-default-setup.sh```.
You will be prompted to type:

- The DNS or IP of the server.
- Your preference on using the Keycloak server.
- Your preference on using the monitoring stack.

The only requirement to deploy the solution is to have Docker and
docker-compose installed.

After deployment all services can be accessed via the NGINX reverse
proxy.

In case you have a DNS domain registered and used it during the
installation you can get TLS certificates using the command:

```
docker exec -it sxcms-nginx certbot --nginx -d <DOMAIN_NAME>
```

Some significant points of interest are:

- If you have enabled the monitoring stack you can login to the
Grafana interface using:

```
DOMAIN_NAME/grafana/
```

You can login using the admin/admin username/password combination
and you will be prompted to change the password.
Two dashboards, monitoring the Spring Boot CMS server and the NGINX
reverse proxy, are already configured and provisioned.

- If you have enabled Keycloak enabled you can login to the Keycloak
admin interface using:

```
DOMAIN_NAME/auth/
```

You can login using the admin/Pa55w0rd username/password combination
and we strongly suggest that you change it.

- The three default username/password combinations
(cms-admin/cms-admin, cms-creator/cms-creator,
cms-consumer/cms-consumer) are already provisioned and
**we strongly suggest that you change these**, too.

- The client secret for the CMS is already set. You can change it in
the Keycloak interface and in ```devops/deployment/sxcms/.env``` file
and execute ```docker-compose up -d``` to apply the changes.

- You can make requests (authenticated if Keycloak is enabled) to the
CMS-API using the base URL

```
DOMAIN_NAME/cms/
```

and

```
DOMAIN_NAME/apiversion/
```

- You can setup custom alerts using the Grafana interface.
( [Grafana Alerts](https://grafana.com/docs/grafana/latest/alerting/old-alerting/create-alerts/) )

:::danger

It is of critical importance for the security of the production setup
to:

- Use TLS.
- Change the default username/password combinations.
- Change the client secret both on the Keycloak interface and on the
server properties.

:::