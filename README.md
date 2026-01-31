## Proxy HTTP client

The HTTP client reads proxy settings from `src/main/resources/application.properties`.

- If `proxy.host` and `proxy.port` are set → requests go through the proxy.
- If they are missing / empty → requests go directly (no proxy).

Optional proxy auth:

```properties
proxy.host=YOUR_PROXY_HOST
proxy.port=YOUR_PROXY_PORT
proxy.user=YOUR_PROXY_USER
proxy.pass=YOUR_PROXY_PASS
```

Run:

```bash
mvn -DskipTests package
java -cp target/classes Main
```
