package dto;

import java.util.List;

public record Sport(
        String name,
        List<Region> regions
) {
}
