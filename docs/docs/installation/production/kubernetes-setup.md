---
sidebar_position: 3
---

# Setup with Kubernetes

The Kubernetes deployment offers the most advantages compared to the
other local installations. It offers horizontal scaling and can
handle more traffic by assigning more resources. The autoscaling
feature offers incomparable value for the handling of traffic with
the appropriate resources.

Due to the multitude of options and the complexity of a Kubernetes
installation probably some deployments may vary. For example there
may be a need to change the persistent volumes depending on the
deployment's requirements. These instructions are opinionated and
indicative and serve as a general guide. The administrator can tweak
the configuration to get optimal results.

The components that need to be deployed are:

- **The SQL database**: MariaDB is used by default but it can be changed
to use some other MySQL option.
- **The CMS server**: The core Spring Boot application.
- **The reverse proxy/Flutter application server**: NGINX is the
preferred choice because of the throughput it can achieve, the
general acceptance but other options may be used, too. It is used as
a reverse proxy and as a server for the Flutter application.
- (Optional but recommended) **Keycloak**: The Open Source Identity
and Access Management. This is the preferred solution if there is
no other OpenID server that can be used.
- (Optional but recommended) **Monitoring stack**: The monitoring
stack is recommended for all deployments that should handle real
traffic. It is composed of the Prometheus-Grafana stack and can be
configured to monitor and alert the deployment.

## The SQL database

The SQL database is used by the CMS server and optionally by the
Keycloak server. The suggested option is MariaDB. It is a robust
open-source SQL database and can offer highly available services
using a clustered deployment.

There is a multitude of common configurations to deploy a clustered
MariaDB. For example:

- [MariaDB fest2020](https://mariadb.org/fest2020/kubernetes/).
- [Bitnami MariaDB Galera image](https://github.com/bitnami/bitnami-docker-mariadb-galera).

:::info

A clustered SQL database is the best way to avoid running out of
available SQL connections while handling traffic from many users. It
can also be autoscaled with appropriate configuration.

:::

The artifacts to deploy a simple MariaDB instance on Kubernetes are
provided in the `devops/deployment/mariadb/kubernetes` folder. They
can be deployed on a running Kubernetes cluster by getting to the
project's root folder and executing:

```
cd devops/deployment/mariadb/kubernetes
kubectl apply -f db-data-persistentvolumeclaim.yaml
kubectl apply -f db-deployment.yaml
kubectl apply -f db-service.yaml
```

## The CMS server

The CMS server uses the aforementioned SQL database and provides the
core functionality. It can be autoscaled with ease and uses a
distributed caching to serve many requests with efficiency. An
indicative example of autoscaling the CMS server can be found in this
[article](https://medium.com/@iskitsas/autoscale-a-java-cms-app-with-kubernetes-the-microk8s-approach-from-docker-to-kubernetes-7c021f7d8333).

The artifacts for the deployment of the Sastix CMS server to
Kubernetes are in the `devops/deployment/sxcms/kubernetes` folder.
The basic CMS server can be deployed by getting to the project's root
folder and running:

```
cd devops/deployment/mariadb/kubernetes
kubectl apply -f sxcms-data-persistentvolumeclaim.yaml
kubectl apply -f sastix-cms-deployment.yaml
kubectl apply -f sastix-cms-service.yaml
```

A file called `autoscaler-sample.yaml` is also provided with an
indicative example of the autoscaling features.

## The reverse proxy/Flutter application server

The NGINX container can be deployed either standalone to handle
traffic or as a deployment behind an Ingress controller. A simple
Kubernetes yaml file is provided in the folder
`devops/deployment/nginx/kubernetes`. It can be deployed by building
the Docker image of sxcms-nginx, by getting to the root folder of the
project and executing:

```
cd devops/deployment/nginx/kubernetes
kubectl apply -f nginx-deployment.yaml
```

## The monitoring stack

The monitoring stack consists of a Grafana and a Prometheus
deployment. It can be deployed with numerous different configurations
depending on the needs of the project.

- Prometheus can be deployed by using the community helm chart:

```
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add stable https://kubernetes-charts.storage.googleapis.com/
helm repo update
```

- For Grafana, a simple deployment yaml is found at
`devops/deployment/grafana/kubernetes` and can be deployed by getting
to the project root folder and executing:<br/>
```
cd devops/deployment/grafana/kubernetes
kubectl apply -f grafana-deployment.yaml
```
This will bring up a single Grafana instance.<br/>
:::danger

This Grafana instance is initialized with a volume of type emptyDir,
which means that when the pod gets killed the data from the Grafana
dashboards will be lost.

:::

If the service is reached via an Ingress Controller, it may be
preferrable to use NGINX-Ingress because of its easy integration with
these monitoring tools
([for example](https://kubernetes.github.io/ingress-nginx/user-guide/monitoring/)).