package api;

import http.CustomHttpClient;
import http.CustomHttpClientImpl;

import java.util.concurrent.CompletableFuture;

import static api.LeonUrlsUtil.buildEventAllUrl;
import static api.LeonUrlsUtil.buildLeagueChangesUrl;

public class LeonApiImpl implements LeonApi {
    private final CustomHttpClient client;

    public LeonApiImpl() {
        this.client = new CustomHttpClientImpl();
    }

    // /api-2/betline/sports?ctag=en-US&flags=urlv2
    @Override
    public CompletableFuture<String> getSportsAsync(String url) {
        return client.getAsync(url);
    }

    // /api-2/betline/changes/all
    @Override
    public CompletableFuture<String> getLeagueDataAsync(long leagueId) {
        return client.getAsync(buildLeagueChangesUrl(leagueId));
    }

    // /api-2/betline/event/all
    @Override
    public CompletableFuture<String> getEventDataAsync(long eventId) {
        return client.getAsync(buildEventAllUrl(eventId));
    }
}
