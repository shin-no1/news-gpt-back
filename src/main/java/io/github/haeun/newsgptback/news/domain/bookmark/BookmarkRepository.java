package io.github.haeun.newsgptback.news.domain.bookmark;

import io.github.haeun.newsgptback.news.dto.BookmarkResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findAllByIdInAndUserId(List<Long> ids, Long userId);

    @Query("SELECT new io.github.haeun.newsgptback.news.dto.BookmarkResponse(" +
            "b.id, b.groupId, " +
            "n.url, n.title, n.summary, n.topic, n.keywords) " +
            "FROM Bookmark b " +
            "JOIN b.newsSummaryHistory n " +
            "WHERE b.user.id = :userId AND b.groupId = :groupId " +
            "ORDER BY b.id DESC")
    List<BookmarkResponse> findBookmarkWithSummaryByUserIdAndGroupId(@Param("userId") long userId,
                                                                     @Param("groupId") long groupId);

    @Modifying
    @Query("DELETE FROM Bookmark b WHERE b.user.id = :userId AND b.id = :bookmarkId")
    int deleteByUserAndId(@Param("userId") long userId, @Param("bookmarkId") long bookmarkId);

    @Modifying
    @Query("UPDATE Bookmark b SET b.groupId = :newGroupId WHERE b.user.id = :userId AND b.groupId = :groupId")
    void updateGroupIdByUserIdAndGroupId(@Param("newGroupId") long newGroupId,
                                         @Param("userId") long userId,
                                         @Param("groupId") long groupId);

}
