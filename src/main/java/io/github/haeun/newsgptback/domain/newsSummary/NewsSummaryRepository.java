package io.github.haeun.newsgptback.domain.newsSummary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsSummaryRepository extends JpaRepository<NewsSummary, SummaryId> {
    Optional<NewsSummary> findByIdSiteIdAndIdUrlNum(Long siteId, String urlNum);
}
