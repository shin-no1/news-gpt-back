package io.github.haeun.newsgptback.news.service;

import io.github.haeun.newsgptback.common.enums.errorCode.BookmarkErrorCode;
import io.github.haeun.newsgptback.common.exception.CustomException;
import io.github.haeun.newsgptback.news.domain.bookmark.Bookmark;
import io.github.haeun.newsgptback.news.domain.bookmark.BookmarkRepository;
import io.github.haeun.newsgptback.news.domain.bookmarkGroup.BookmarkGroup;
import io.github.haeun.newsgptback.news.domain.bookmarkGroup.BookmarkGroupRepository;
import io.github.haeun.newsgptback.news.domain.newsSummaryHistory.NewsSummaryHistory;
import io.github.haeun.newsgptback.news.domain.user.User;
import io.github.haeun.newsgptback.news.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 북마크와 북마크 그룹을 관리하는 서비스 클래스
 * 사용자의 북마크 저장, 조회, 수정, 삭제 및 북마크 그룹 관리 기능을 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkGroupRepository bookmarkGroupRepository;

    private final String DEFAULT_GROUP_NAME = "기본";

    /**
     * 특정 사용자의 특정 그룹에 속한 북마크 목록을 조회
     *
     * @param userId  사용자 ID
     * @param groupId 그룹 ID
     * @return 북마크 목록. 결과가 없을 경우 빈 리스트 반환
     */
    @Transactional(readOnly = true)
    public List<BookmarkResponse> getBookmarks(long userId, long groupId) {
        List<BookmarkResponse> bookmarks = bookmarkRepository.findBookmarkWithSummaryByUserIdAndGroupId(userId, groupId);
        if (ObjectUtils.isEmpty(bookmarks)) {
            return new ArrayList<>();
        }
        return bookmarks;
    }

    /**
     * 새로운 북마크를 저장
     *
     * @param bookmarkRequest 북마크 저장 요청 정보
     * @param userId          사용자 ID
     * @return 저장된 북마크 정보
     */
    @Transactional
    public BookmarkSaveResponse saveBookmark(BookmarkRequest bookmarkRequest, long userId) {
        Bookmark bookmark = bookmarkRepository.save(new Bookmark(
                new User(userId),
                new NewsSummaryHistory(bookmarkRequest.getSummaryHistoryId()),
                bookmarkRequest.getGroupId()
        ));
        return new BookmarkSaveResponse(bookmark.getId(), bookmark.getGroupId());
    }

    /**
     * 기존 북마크의 그룹을 변경
     *
     * @param userId          사용자 ID
     * @param bookmarkId      북마크 ID
     * @param bookmarkRequest 북마크 수정 요청 정보
     * @throws CustomException 북마크나 대상 그룹이 존재하지 않는 경우
     */
    @Transactional
    public void putBookmark(long userId, long bookmarkId, BookmarkPutRequest bookmarkRequest) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new CustomException(BookmarkErrorCode.BOOKMARK_NOT_FOUND));

        if (!bookmark.getUser().getId().equals(userId)) {
            throw new CustomException(BookmarkErrorCode.BOOKMARK_NOT_FOUND);
        }

        BookmarkGroup targetGroup = bookmarkGroupRepository.findByIdAndUserId(bookmarkRequest.getAfterGroupId(), userId)
                .orElseThrow(() -> new CustomException(BookmarkErrorCode.BOOKMARK_GROUP_NOT_FOUND));

        bookmark.setGroupId(targetGroup.getId());
        bookmarkRepository.save(bookmark);
    }

    /**
     * 여러 북마크의 그룹을 한 번에 변경
     *
     * @param userId          사용자 ID
     * @param bookmarkRequest 다중 북마크 수정 요청 정보
     * @throws CustomException 북마크나 대상 그룹이 존재하지 않는 경우, 또는 잘못된 일괄 작업 요청인 경우
     */
    @Transactional
    public void putMultiBookmark(long userId, BookmarkMultiPutRequest bookmarkRequest) {
        List<Bookmark> bookmarks = bookmarkRepository.findAllByIdInAndUserId(bookmarkRequest.getBookmarkIds(), userId);

        if (bookmarks.size() != bookmarkRequest.getBookmarkIds().size()) {
            throw new CustomException(BookmarkErrorCode.BOOKMARK_INVALID_BATCH_OPERATION);
        }

        BookmarkGroup targetGroup = bookmarkGroupRepository.findByIdAndUserId(bookmarkRequest.getAfterGroupId(), userId)
                .orElseThrow(() -> new CustomException(BookmarkErrorCode.BOOKMARK_GROUP_NOT_FOUND));

        for (Bookmark bookmark : bookmarks) {
            bookmark.setGroupId(targetGroup.getId());
        }

        bookmarkRepository.saveAll(bookmarks);
    }

    /**
     * 특정 북마크를 삭제
     *
     * @param userId     사용자 ID
     * @param bookmarkId 삭제할 북마크 ID
     * @throws CustomException 북마크가 존재하지 않는 경우
     */
    @Transactional
    public void deleteBookmark(long userId, long bookmarkId) {
        int deletedCount = bookmarkRepository.deleteByUserAndId(userId, bookmarkId);
        if (deletedCount == 0) {
            throw new CustomException(BookmarkErrorCode.BOOKMARK_NOT_FOUND);
        }
    }

    /**
     * 사용자의 북마크 그룹 목록을 조회
     * 그룹이 없는 경우 기본 그룹을 생성하여 반환
     *
     * @param userId 사용자 ID
     * @return 북마크 그룹 목록
     */
    @Transactional(readOnly = true)
    public List<BookmarkGroupsResponse> getBookmarkGroup(long userId) {
        List<BookmarkGroup> bookmarkGroups = getBookmarkGroupByUser(userId);
        return bookmarkGroups.stream()
                .map(group -> new BookmarkGroupsResponse(group.getDisplayOrder(), group.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 새로운 북마크 그룹을 생성
     *
     * @param bookmarkGroupRequest 북마크 그룹 생성 요청 정보
     * @param userId               사용자 ID
     * @throws CustomException 그룹 개수가 제한을 초과하거나 중복된 그룹명인 경우
     */
    @Transactional
    public void saveBookmarkGroup(BookmarkGroupRequest bookmarkGroupRequest, long userId) {
        if (getBookmarkGroupByUser(userId).size() >= 10) {
            throw new CustomException(BookmarkErrorCode.MAX_BOOKMARK_GROUP_LIMIT_EXCEEDED);
        }
        User user = new User(userId);
        if (bookmarkGroupRepository.findByUserIdAndName(userId, bookmarkGroupRequest.getName()).isPresent()) {
            throw new CustomException(BookmarkErrorCode.DUPLICATE_BOOKMARK_GROUP_NAME);
        }
        bookmarkGroupRepository.save(new BookmarkGroup(
                user,
                bookmarkGroupRequest.getName(),
                getGroupDisplayOrderFromNow()
        ));
    }

    /**
     * 북마크 그룹의 이름을 변경
     *
     * @param userId  사용자 ID
     * @param groupId 그룹 ID
     * @param newName 새로운 그룹명
     * @throws CustomException 그룹이 존재하지 않거나, 기본 그룹인 경우, 또는 중복된 그룹명인 경우
     */
    @Transactional
    public void renameBookmarkGroup(long userId, long groupId, String newName) {
        BookmarkGroup bookmarkGroup = bookmarkGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(BookmarkErrorCode.BOOKMARK_GROUP_NOT_FOUND));

        if (DEFAULT_GROUP_NAME.equals(bookmarkGroup.getName())) {
            throw new CustomException(BookmarkErrorCode.BOOKMARK_GROUP_CANNOT_BE_MODIFIED);
        }

        if (!bookmarkGroup.getUser().getId().equals(userId)) {
            throw new CustomException(BookmarkErrorCode.BOOKMARK_GROUP_NOT_FOUND);
        }

        if (bookmarkGroupRepository.findByUserIdAndName(userId, newName).isPresent()) {
            throw new CustomException(BookmarkErrorCode.DUPLICATE_BOOKMARK_GROUP_NAME);
        }

        bookmarkGroup.setName(newName);
        bookmarkGroupRepository.save(bookmarkGroup);
    }

    /**
     * 북마크 그룹을 삭제
     * 해당 그룹의 북마크들은 기본 그룹으로 이동
     *
     * @param userId  사용자 ID
     * @param groupId 삭제할 그룹 ID
     * @throws CustomException 그룹이 존재하지 않거나 기본 그룹을 삭제하려는 경우
     */
    @Transactional
    public void deleteGroup(Long userId, Long groupId) {
        BookmarkGroup group = bookmarkGroupRepository.findByIdAndUserId(groupId, userId)
                .orElseThrow(() -> new CustomException(BookmarkErrorCode.BOOKMARK_GROUP_NOT_FOUND));

        if (DEFAULT_GROUP_NAME.equals(group.getName())) {
            throw new CustomException(BookmarkErrorCode.BOOKMARK_GROUP_CANNOT_BE_DELETED);
        }

        long defaultGroupId = bookmarkGroupRepository.findByUserIdAndName(userId, DEFAULT_GROUP_NAME).get().getId();
        bookmarkRepository.updateGroupIdByUserIdAndGroupId(defaultGroupId, userId, groupId);
        bookmarkGroupRepository.delete(group);
    }

    private List<BookmarkGroup> getBookmarkGroupByUser(long userId) {
        List<BookmarkGroup> bookmarkGroups = bookmarkGroupRepository.findAllByUserIdOrderByDisplayOrder(userId);
        if (ObjectUtils.isEmpty(bookmarkGroups)) {
            BookmarkGroup bookmarkGroup = bookmarkGroupRepository.save(new BookmarkGroup(
                    new User(userId),
                    DEFAULT_GROUP_NAME,
                    getGroupDisplayOrderFromNow())
            );
            bookmarkGroups = Collections.singletonList(bookmarkGroup);
        }
        return bookmarkGroups;
    }

    private Long getGroupDisplayOrderFromNow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        return Long.parseLong(LocalDateTime.now().format(formatter));
    }
}
