package ru.practicum.ewm.main.repository.locations;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.model.events.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
