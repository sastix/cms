---
sidebar_position: 1
---

# Get benchmark reports

Benchmarking tests can be run by using the provided utilities in the
`devops/benchmarking` folder.

The configuration of the tests can be changed to perform different
checks. The files that should be changed are:

- `test.properties` : Variables about the threads the script will
use, e.t.c.
- `urls.txt` : The URLs that the tests will be performed against.

After the configuration is finished the tests can be run with:

```
cd devops/benchmarking
docker-compose up -d
```

:::info

No prerequisites are required. A Docker image will be built and the
tests will run in a Docker container.

:::

After the tests are completed the results are available in the
results folder with a file per URL.