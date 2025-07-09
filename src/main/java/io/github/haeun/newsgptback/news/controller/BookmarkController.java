package io.github.haeun.newsgptback.news.controller;

import io.github.haeun.newsgptback.news.domain.user.User;
import io.github.haeun.newsgptback.news.dto.*;
import io.github.haeun.newsgptback.news.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/bookmarks")
    public ResponseEntity<?> getBookmark(@AuthenticationPrincipal User user,
                                         @RequestParam Long groupId) {
        List<BookmarkResponse> bookmarks = bookmarkService.getBookmarks(user.getId(), groupId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookmarks);
    }

    @PostMapping("/bookmarks")
    public ResponseEntity<?> saveBookmark(@AuthenticationPrincipal User user,
                                          @RequestBody BookmarkRequest bookmarkRequest) {
        BookmarkSaveResponse bookmarkSaveResponse = bookmarkService.saveBookmark(bookmarkRequest, user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookmarkSaveResponse);
    }

    @PutMapping("/bookmarks/{bookmarkId}")
    public ResponseEntity<?> putBookmark(@AuthenticationPrincipal User user,
                                         @PathVariable long bookmarkId,
                                         @RequestBody BookmarkPutRequest bookmarkRequest) {
        bookmarkService.putBookmark(user.getId(), bookmarkId, bookmarkRequest);
        List<BookmarkResponse> bookmarks = bookmarkService.getBookmarks(user.getId(), bookmarkRequest.getGroupId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookmarks);
    }

    @PutMapping("/bookmarks/group")
    public ResponseEntity<?> putMultiBookmark(@AuthenticationPrincipal User user,
                                              @RequestBody BookmarkMultiPutRequest bookmarkRequest) {
        bookmarkService.putMultiBookmark(user.getId(), bookmarkRequest);
        List<BookmarkResponse> bookmarks = bookmarkService.getBookmarks(user.getId(), bookmarkRequest.getGroupId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookmarks);
    }

    @DeleteMapping("/bookmarks/{bookmarkId}")
    public ResponseEntity<?> deleteBookmark(@AuthenticationPrincipal User user,
                                            @PathVariable Long bookmarkId) {
        bookmarkService.deleteBookmark(user.getId(), bookmarkId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("삭제가 완료되었습니다.");
    }

    @GetMapping("/bookmark-groups")
    public ResponseEntity<?> getBookmarkGroup(@AuthenticationPrincipal User user) {
        List<BookmarkGroupsResponse> bookmarkGroupsResponses = bookmarkService.getBookmarkGroup(user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookmarkGroupsResponses);
    }

    @PostMapping("/bookmark-groups")
    public ResponseEntity<?> saveBookmarkGroup(@AuthenticationPrincipal User user,
                                               @RequestBody BookmarkGroupRequest bookmarkRequest) {
        bookmarkService.saveBookmarkGroup(bookmarkRequest, user.getId());
        List<BookmarkGroupsResponse> bookmarkGroupsResponses = bookmarkService.getBookmarkGroup(user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookmarkGroupsResponses);
    }

    @PutMapping("/bookmark-groups/{groupId}")
    public ResponseEntity<?> renameBookmarkGroup(@AuthenticationPrincipal User user,
                                                 @PathVariable long groupId,
                                                 @RequestBody BookmarkGroupRequest bookmarkGroupRequest) {
        bookmarkService.renameBookmarkGroup(user.getId(), groupId, bookmarkGroupRequest.getName());
        List<BookmarkGroupsResponse> bookmarkGroupsResponses = bookmarkService.getBookmarkGroup(user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookmarkGroupsResponses);
    }

    @DeleteMapping("/bookmark-groups/{groupId}")
    public ResponseEntity<?> deleteBookmarkGroup(@AuthenticationPrincipal User user,
                                                 @PathVariable Long groupId) {
        bookmarkService.deleteGroup(user.getId(), groupId);
        List<BookmarkGroupsResponse> bookmarkGroupsResponses = bookmarkService.getBookmarkGroup(user.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookmarkGroupsResponses);
    }

}
