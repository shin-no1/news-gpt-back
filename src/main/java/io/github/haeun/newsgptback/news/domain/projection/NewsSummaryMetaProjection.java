package io.github.haeun.newsgptback.news.domain.projection;

public interface NewsSummaryMetaProjection {
    Long getSummaryId();
    String getPromptVersion();
}
