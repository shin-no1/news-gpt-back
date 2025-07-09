package io.github.haeun.newsgptback.news.domain.bookmarkGroup;

import io.github.haeun.newsgptback.news.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(
        name = "bookmark_group",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BookmarkGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Long displayOrder;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public BookmarkGroup(User user, String name, Long displayOrder) {
        this.user = user;
        this.name = name;
        this.displayOrder = displayOrder;
    }
}