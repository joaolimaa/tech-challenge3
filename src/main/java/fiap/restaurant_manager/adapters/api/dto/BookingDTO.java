package fiap.restaurant_manager.adapters.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fiap.restaurant_manager.domain.enums.StatusBooking;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.LocalDateTime;

public record BookingDTO(@JsonIgnore  @ReadOnlyProperty Long id,
                         Long restaurantId,
                         Long userId,
                         LocalDateTime bookingDate,
                         Integer peopleQuantity,
                         StatusBooking status) {

    public BookingDTO(Long restaurantId, Long userId, LocalDateTime bookingDate,
                      Integer peopleQuantity, StatusBooking status) {
        this(null, restaurantId, userId, bookingDate, peopleQuantity, status);
    }
}
