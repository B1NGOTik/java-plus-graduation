package ru.practicum.ewm.main.service.location;

import ru.practicum.ewm.main.model.events.Location;
import ru.practicum.ewm.main.model.events.dto.LocationDto;

public interface LocationService {

    Location saveLocation(LocationDto location);
}
