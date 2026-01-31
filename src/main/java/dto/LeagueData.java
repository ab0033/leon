package dto;

import java.util.List;

public record LeagueData(
        List<LeagueDataItem> data
) {
    public record LeagueDataItem(
            long id
    ) {
    }
}
