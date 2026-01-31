package dto;

import java.util.List;

public record Region(
        List<League> leagues
) {
}
