package mapper;

import dto.Event;
import dto.Sport;

import java.util.List;

public interface Mapper {
    List<Sport> toSport(String json);

    Event toEventData(String json);

    List<Long> getEventIds(String json);
}

