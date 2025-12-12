package ru.practicum.server.item.dto.item;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemForItemRequestsDto {
    private Long id;
    private String name;
    private Long ownerId;
}
