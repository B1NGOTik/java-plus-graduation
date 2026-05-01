package ru.yandex.practicum.service.location;

import ru.yandex.practicum.event.location.LocationDto;
import ru.yandex.practicum.model.Location;

public interface LocationService {
    Location saveLocation(LocationDto location);
}
