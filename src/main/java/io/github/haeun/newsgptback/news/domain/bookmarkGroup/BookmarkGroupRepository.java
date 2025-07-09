package io.github.haeun.newsgptback.news.domain.bookmarkGroup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkGroupRepository extends JpaRepository<BookmarkGroup, Long> {
    List<BookmarkGroup> findAllByUserIdOrderByDisplayOrder(long userId);
    Optional<BookmarkGroup> findByUserIdAndName(long userId, String name);
    Optional<BookmarkGroup> findByIdAndUserId(long groupId, long userId);
}
