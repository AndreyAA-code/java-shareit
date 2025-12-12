package ru.practicum.server.request.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "item_requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @JoinColumn(name = "requestor")
    @ManyToOne(fetch = FetchType.LAZY)
    private User requestor;

    @Builder.Default
    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();
}