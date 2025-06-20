package io.github.haeun.newsgptback.domain.newsSummary;

import io.github.haeun.newsgptback.domain.Site.Site;
import io.github.haeun.newsgptback.domain.newsSummaryHistory.NewsSummaryHistory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "news_summary",
        indexes = @Index(name = "idx_site_url_prompt", columnList = "site_id, url_num, prompt_version"))
@Getter
@Setter
@NoArgsConstructor
public class NewsSummary {

    @EmbeddedId
    private SummaryId id;

    @MapsId("siteId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary_id", nullable = false)
    private NewsSummaryHistory summary;

    @Column(name = "prompt_version", nullable = false, length = 50)
    private String promptVersion;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
