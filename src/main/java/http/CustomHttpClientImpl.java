package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proxy.ProxyAuthenticator;
import proxy.ProxyConfig;
import proxy.ProxyConfigLoader;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class CustomHttpClientImpl implements CustomHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(CustomHttpClientImpl.class);

    private final HttpClient client;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);

    public CustomHttpClientImpl() {
        ProxyConfig proxyConfig = ProxyConfigLoader.loadFromClasspathProperties();

        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(DEFAULT_TIMEOUT);

        boolean usingProxy = false;
        if (proxyConfig != null && proxyConfig.host() != null && proxyConfig.port() > 0) {
            builder.proxy(ProxySelector.of(new InetSocketAddress(proxyConfig.host(), proxyConfig.port())));
            if (proxyConfig.hasAuth()) {
                builder.authenticator(new ProxyAuthenticator(proxyConfig.username(), proxyConfig.password()));
            }
            usingProxy = true;
        }

        this.client = builder
                .version(HttpClient.Version.HTTP_1_1)
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .build();

        if (usingProxy) {
            logger.info("CustomHttpClient configured to use proxy {}:{}", proxyConfig.host(), proxyConfig.port());
        } else {
            logger.info("CustomHttpClient configured without proxy");
        }
    }

    @Override
    public CompletableFuture<String> getAsync(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(DEFAULT_TIMEOUT)
                .GET()
                .build();
        return sendAsync(request).thenApply(HttpResponse::body);
    }

    private CompletableFuture<HttpResponse<String>> sendAsync(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
