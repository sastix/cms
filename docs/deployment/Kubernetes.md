Kubernetes

# Deploy Keycloak

Change the values of Keycloak environment variables in the ```devops/deployment/keycloak/kubernetes/keycloak-deployment.yaml``` file and apply with:

```
kubectl apply -f devops/deployment/keycloak/kubernetes/keycloak-deployment.yaml
```

This will deploy a cluster of 3 Keycloak servers, storing data to the provided database and sharing a Session Cache. This deployment can later be exposed for example with an ingress-nginx like so:

```
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: auth-ingress
  annotations:
    nginx.ingress.kubernetes.io/backend-protocol: "https"
spec:
  tls:
    - hosts:
      - keycloak-auth
      secretName: auth-tls-secret
  rules:
  - host: "example.com"
    http:
      paths:
      - pathType: Prefix
        path: "/"
        backend:
          service:
            name: keycloak
            port:
              number: 8443
```

!!! CAUTION
- You may want to change the KEYCLOAK_PASSWORD for the admin user and change it again from the GUI or protect it with a kubernetes secret.
- The DB_VENDOR can be changed to use another of your choice.