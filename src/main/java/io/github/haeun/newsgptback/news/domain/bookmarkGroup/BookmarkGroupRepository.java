package io.github.haeun.newsgptback.news.domain.bookmarkGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkGroupRepository extends JpaRepository<BookmarkGroup, Long> {
    //    List<BookmarkGroup> findAllByUserIdOrderByDisplayOrder(long userId);
    @Query("select bg from BookmarkGroup bg where bg.user.id = :userId order by bg.displayOrder")
    List<BookmarkGroup> findAllByUserIdOrderByDisplayOrder(@Param("userId") long userId);

    Optional<BookmarkGroup> findByUserIdAndName(long userId, String name);

    Optional<BookmarkGroup> findByIdAndUserId(long groupId, long userId);
}
