package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemUpdateDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }
    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }
    public boolean hasAvailable() {
        return available;
    }
}
