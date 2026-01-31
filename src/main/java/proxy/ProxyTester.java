package proxy;

import http.CustomHttpClient;
import http.CustomHttpClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyTester {
    private static final Logger logger = LoggerFactory.getLogger(ProxyTester.class);

    public static void main(String[] args) {
        CustomHttpClient client = new CustomHttpClientImpl();

        String testUrl = "https://leonbets.com";

        try {
            String body = client.getAsync(testUrl).join();
            logger.info("Response body length: {}", (body != null ? body.length() : 0));
            logger.info("Response body: {}", body);
        } catch (Exception e) {
            logger.error("Request failed", e);
        }
    }
}
