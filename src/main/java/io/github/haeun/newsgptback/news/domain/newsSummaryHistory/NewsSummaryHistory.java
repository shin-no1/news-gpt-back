package io.github.haeun.newsgptback.news.domain.newsSummaryHistory;

import io.github.haeun.newsgptback.common.converter.StringListJsonConverter;
import io.github.haeun.newsgptback.news.domain.site.Site;
import io.github.haeun.newsgptback.common.enums.HistorySource;
import io.github.haeun.newsgptback.news.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "news_summary_history",
        indexes = @Index(name = "idx_site_urlnum_id", columnList = "site_id, url_num, id"))
@Getter
@Setter
@NoArgsConstructor
public class NewsSummaryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "url_num", nullable = false, length = 255)
    private String urlNum;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(name = "prompt_version", nullable = false, length = 50)
    private String promptVersion;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(length = 100)
    private String topic;

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "json")
    private List<String> keywords;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Enumerated(EnumType.STRING)
    private HistorySource source = HistorySource.NEW;

    @Enumerated(EnumType.STRING)
    private Status status = Status.SUCCESS;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        SUCCESS, FAILED
    }
}