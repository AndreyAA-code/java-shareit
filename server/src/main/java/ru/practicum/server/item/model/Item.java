package ru.practicum.server.item.model;

import jakarta.persistence.*;
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
    private String name;
    private String description;

    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    private Boolean available;

    @JoinColumn(name = "request_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemRequest request;

}
