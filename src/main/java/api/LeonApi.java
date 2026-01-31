package api;

import java.util.concurrent.CompletableFuture;

public interface LeonApi {
    CompletableFuture<String> getSportsAsync(String url);

    CompletableFuture<String> getLeagueDataAsync(long leagueId);

    CompletableFuture<String> getEventDataAsync(long eventId);
}
