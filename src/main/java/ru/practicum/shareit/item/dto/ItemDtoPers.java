package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoPers {
    private Integer id;
    private Integer ownerId;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;

}
