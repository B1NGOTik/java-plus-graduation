package ru.practicum.ewm.main.model.comment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    Events event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User author;

    @Column(name = "comment_text")
    String text;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommentStatus status;
}