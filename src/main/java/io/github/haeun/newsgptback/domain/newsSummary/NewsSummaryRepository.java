package io.github.haeun.newsgptback.domain.newsSummary;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsSummaryRepository extends JpaRepository<NewsSummary, SummaryId> {
}
