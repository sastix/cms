---
sidebar_position: 1
---

# Create a resource

You can create a resource using Swagger interface or the curl
equivalent call.

- The Swagger interface provides the

```
POST /cms/v1.0/createResource
```

which can be used with body:

```
{
  "resourceAuthor": "Test Author",
  "resourceExternalURI": "https://upload.wikimedia.org/wikipedia/commons/thumb/8/85/GSoC-icon.svg/1200px-GSoC-icon.svg.png",
  "resourceMediaType": "image/png",
  "resourceName": "logo.png",
  "resourceTenantId": "zaq12345"
}
```

- The curl alternative is:

```
curl -X POST "http://localhost:9082/cms/v1.0/createResource" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"resourceAuthor\": \"Test Author\", \"resourceExternalURI\": \"https://upload.wikimedia.org/wikipedia/commons/thumb/8/85/GSoC-icon.svg/1200px-GSoC-icon.svg.png", \"resourceMediaType\": \"image/png\", \"resourceName\": \"logo.png\", \"resourceTenantId\": \"zaq12345\"}"
```

The response will be similar to:

```
{
  "resourceUID": "d6b4a0c8-zaq12345",
  "author": "Test Author",
  "resourceURI": "a28d4846-zaq12345/logo.png",
  "resourcesList": null
}
```

Using the `resourceURI` the resource can be accessed either in the
browser or using any HTTP client in:

```
http://localhost:9082/cms/v1.0/getData/a28d4846-zaq12345/logo.png
```