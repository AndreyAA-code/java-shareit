package ru.practicum.server.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.user.model.User;

@Entity
@Table(name = "items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @NotNull
    private Boolean available;

    @JoinColumn(name = "request_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemRequest request;

}
