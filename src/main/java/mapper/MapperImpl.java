package mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dto.Event;
import dto.LeagueData;
import dto.Sport;

import java.util.List;
import java.util.Objects;

public class MapperImpl implements Mapper {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    public List<Sport> toSport(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse sports json", e);
        }
    }

    @Override
    public Event toEventData(String json) {
        try {
            return objectMapper.readValue(json, Event.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse event data json", e);
        }
    }

    @Override
    public List<Long> getEventIds(String json) {
        if (json == null || json.isBlank()) return List.of();

        LeagueData leagueData;
        try {
            leagueData = objectMapper.readValue(json, LeagueData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse league data json", e);
        }

        if (leagueData == null || leagueData.data() == null || leagueData.data().isEmpty()) return List.of();

        return leagueData.data().stream()
                .filter(Objects::nonNull)
                .mapToLong(LeagueData.LeagueDataItem::id)
                .limit(2)
                .boxed()
                .toList();
    }
}
