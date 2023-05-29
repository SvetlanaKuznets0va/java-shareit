package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private int id;
    private int ownerId;
    private String name;
    private String description;
    private boolean available;

    public Item(int ownerId, String name, String description, boolean available) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
