package io.github.haeun.newsgptback.domain.newsSummary;

import io.github.haeun.newsgptback.domain.projection.NewsSummaryMetaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NewsSummaryRepository extends JpaRepository<NewsSummary, SummaryId> {
    Optional<NewsSummary> findByIdSiteIdAndIdUrlNum(Long siteId, String urlNum);

    @Query("""
            SELECT ns.summary.id AS summaryId, ns.promptVersion AS promptVersion
              FROM NewsSummary ns
             WHERE ns.id.siteId = :siteId
               AND ns.id.urlNum = :urlNum
            """)
    Optional<NewsSummaryMetaProjection> findSummaryIdById(Long siteId, String urlNum);
}
