package ru.practicum.ewm.main.service.location;

import ru.practicum.ewm.main.model.events.Location;
import ru.practicum.ewm.main.model.events.dto.LocationDto;

import java.util.Locale;

public interface LocationService {

    Location saveLocation(LocationDto location);
}
