---
sidebar_position: 1
---

# Overview

The CMS can be deployed with different options and configurations.

## Plain installation

The CMS components can be deployed on a host machine using native
packages. 

:::caution

This method is discouraged because it can cause a lot of
issues with other packages and is deprived of all the advantages
that a Docker installation would offer.

:::

## Docker installation

The CMS components can all be deployed on a Docker host with no other
prerequisite than Docker. All components are containerized and there
would be no conflicts with other deployed services.

:::info

This is the suggested method for single-node installation.

:::

## Kubernetes installation

The Kubernetes installation offers many advantages over the single
host one. The deployment of the CMS server can be scaled on demand
and the bottlenecks of SQL connections or of the network bandwidth
can be faced with ease.

:::info

This is the suggested method of installation for scalability. It can
handle practically any load using the horizontal scaling features
offered by Kubernetes.

:::