package http;

import java.util.concurrent.CompletableFuture;

public interface CustomHttpClient {
    CompletableFuture<String> getAsync(String url);

}
