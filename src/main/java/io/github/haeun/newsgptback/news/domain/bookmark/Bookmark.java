package io.github.haeun.newsgptback.news.domain.bookmark;

import io.github.haeun.newsgptback.news.domain.newsSummaryHistory.NewsSummaryHistory;
import io.github.haeun.newsgptback.news.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "bookmark")
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary_history_id", nullable = false)
    private NewsSummaryHistory newsSummaryHistory;

    @Column(nullable = false)
    private Long groupId;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Bookmark(User user, NewsSummaryHistory newsSummaryHistory, Long groupId) {
        this.user = user;
        this.newsSummaryHistory = newsSummaryHistory;
        this.groupId = groupId;
    }
}