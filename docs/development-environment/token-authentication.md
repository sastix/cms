token-authentication

# Default users

```
cms-admin / cms-admin , with role admin
cms-creator / cms-creator, with role creator
cms-consumer / cms-consumer, with role consumer
```

# Default clients

The CMS server client is:

```
client-id : cms-server
client-secret : 6bdcbcb3-457c-4d86-b5c3-5b9cda7198da
```

# curl access/refresh token

```
curl -d "client_id=cms-server" -d "client_secret=6bdcbcb3-457c-4d86-b5c3-5b9cda7198da" -d "username=cms-admin" -d "password=cms-admin" -d "grant_type=password" "http://localhost:8080/auth/realms/CMS/protocol/openid-connect/token"
```