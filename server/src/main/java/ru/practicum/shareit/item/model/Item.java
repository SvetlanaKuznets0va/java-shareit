package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int ownerId;
    private String name;
    private String description;
    private boolean available;
    private Integer requestId;

    public Item(int ownerId, String name, String description, boolean available, Integer requestId) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
