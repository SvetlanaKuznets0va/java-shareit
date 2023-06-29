package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestRespDto {
    private int id;
    private String description;
    private LocalDateTime created;
    private List<InnerItemDto> items;

    @Data
    @AllArgsConstructor
    public static class InnerItemDto {
        private Integer id;
        private String name;
        private String description;
        private boolean available;
        private Integer requestId;
    }
}
