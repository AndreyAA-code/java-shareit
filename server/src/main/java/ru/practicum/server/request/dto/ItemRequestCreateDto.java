package ru.practicum.server.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestCreateDto {

    @NotBlank
    private String description;
}
