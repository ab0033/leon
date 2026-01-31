package api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class LeonUrlsUtil {
    private LeonUrlsUtil() {
    }

    // /api-2/betline/sports?ctag=en-US&flags=urlv2
    public static final String GET_SPORTS = "https://leonbets.com/api-2/betline/sports?ctag=en-US&flags=urlv2";

    private static final String CTAG = "en-US";

    // /api-2/betline/changes/all
    private static final String LEAGUE_CHANGES_BASE = "https://leonbets.com/api-2/betline/changes/all";
    private static final String LEAGUE_VTAG = "9c2cd386-31e1-4ce9-a140-28e9b63a9300";
    private static final String LEAGUE_FLAGS = "reg,urlv2,orn2,mm2,rrc,nodup";

    // /api-2/betline/event/all
    private static final String EVENT_ALL_BASE = "https://leonbets.com/api-2/betline/event/all";
    private static final String EVENT_ALL_FLAGS = "reg,urlv2,orn2,mm2,rrc,nodup,smgv2,outv2,wd3";

    public static String buildLeagueChangesUrl(long leagueId) {
        return LEAGUE_CHANGES_BASE
                + "?ctag=" + url(CTAG)
                + "&vtag=" + url(LEAGUE_VTAG)
                + "&league_id=" + url(String.valueOf(leagueId))
                + "&hideClosed=true"
                + "&flags=" + url(LEAGUE_FLAGS);
    }

    public static String buildEventAllUrl(long eventId) {
        return EVENT_ALL_BASE
                + "?ctag=" + url(CTAG)
                + "&eventId=" + url(String.valueOf(eventId))
                + "&flags=" + url(EVENT_ALL_FLAGS);
    }

    private static String url(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
