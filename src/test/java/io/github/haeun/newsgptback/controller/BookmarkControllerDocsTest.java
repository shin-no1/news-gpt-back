package io.github.haeun.newsgptback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.haeun.newsgptback.common.enums.UserRole;
import io.github.haeun.newsgptback.common.util.JwtTestUtil;
import io.github.haeun.newsgptback.config.TestJwtConfig;
import io.github.haeun.newsgptback.config.TestMockConfig;
import io.github.haeun.newsgptback.news.domain.user.User;
import io.github.haeun.newsgptback.news.domain.user.UserRepository;
import io.github.haeun.newsgptback.news.dto.*;
import io.github.haeun.newsgptback.news.service.BookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import({TestMockConfig.class, TestJwtConfig.class})
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class BookmarkControllerDocsTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookmarkService bookmarkService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    private String accessToken;

    @BeforeEach
    void setup() {
        User user = new User(1L, "user@email.com", "testuser", "tupw15741", UserRole.USER, true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        accessToken = jwtTestUtil.generateAccessToken();
    }

    private List<BookmarkResponse> createTestBookmarks() {
        return List.of(
                new BookmarkResponse(1L, 1L, "https://test-news1.com", "Breaking News 1", "Summary of news 1", "Politics",
                        List.of("Election", "Government")),
                new BookmarkResponse(2L, 1L, "https://test-news2.com", "Breaking News 2", "Summary of news 2", "Technology",
                        List.of("AI", "Innovation", "Tech")),
                new BookmarkResponse(3L, 2L, "https://test-news3.com", "Breaking News 3", "Summary of news 3", "Sports",
                        List.of("Football", "Championship", "Score"))
        );
    }

    @Test
    void getBookmark() throws Exception {
        // given
        List<BookmarkResponse> mockResponse = createTestBookmarks();

        // when
        Mockito.when(bookmarkService.getBookmarks(anyLong(), anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(get("/api/bookmarks")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("groupId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-bookmarks",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        getRequestAuthHeader(),
                        queryParameters(
                                parameterWithName("groupId").description("북마크 그룹 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("북마크 ID"),
                                fieldWithPath("[].title").description("뉴스 제목"),
                                fieldWithPath("[].summary").description("뉴스 요약"),
                                fieldWithPath("[].topic").description("뉴스 주제"),
                                fieldWithPath("[].keywords").description("키워드 목록"),
                                fieldWithPath("[].url").description("뉴스 URL"),
                                fieldWithPath("[].groupId").description("그룹 ID")
                        )
                ));
    }

    @Test
    void saveBookmark() throws Exception {
        // given
        BookmarkRequest request = new BookmarkRequest(3L, 4L);
        BookmarkSaveResponse mockResponse = new BookmarkSaveResponse(3L, 4L);

        // when
        Mockito.when(bookmarkService.saveBookmark(any(), anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(post("/api/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("save-bookmark",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        getRequestAuthHeader(),
                        requestFields(
                                fieldWithPath("summaryHistoryId").description("북마크 생성할 컨텐츠 ID"),
                                fieldWithPath("groupId").description("생성할 북마크의 GROUP_ID")
                        ),
                        responseFields(
                                fieldWithPath("bookmarkId").description("생성된 북마크 ID"),
                                fieldWithPath("groupId").description("생성된 북마크의 GROUP_ID")
                        )
                ));
    }

    @Test
    void getBookmarkGroup() throws Exception {
        // given
        List<BookmarkGroupsResponse> mockResponse = List.of(
                new BookmarkGroupsResponse(1L, 240101L, "기본"),
                new BookmarkGroupsResponse(2L, 240102L, "관심 뉴스"),
                new BookmarkGroupsResponse(3L, 240103L, "읽을 거리"),
                new BookmarkGroupsResponse(4L, 240104L, "중요한 소식")
        );

        // when
        Mockito.when(bookmarkService.getBookmarkGroup(anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(get("/api/bookmark-groups")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-bookmark-groups",
                        preprocessResponse(prettyPrint()),
                        getRequestAuthHeader(),
                        responseFields(
                                fieldWithPath("[].id").description("북마크 그룹 ID"),
                                fieldWithPath("[].name").description("그룹 이름"),
                                fieldWithPath("[].displayOrder").description("그룹 순서(ASC)")
                        )
                ));
    }

    @Test
    void putBookmark() throws Exception {
        // given
        long bookmarkId = 1L;
        BookmarkPutRequest request = new BookmarkPutRequest(1L, 2L);
        List<BookmarkResponse> mockResponse = List.of(
                new BookmarkResponse(1L, 2L, "https://example.com/news1", "Global Economic Summit Results",
                        "Leaders agree on new climate initiatives and trade policies", "Economy",
                        List.of("Economy", "Climate", "Trade")),
                new BookmarkResponse(2L, 1L, "https://example.com/news2", "Tech Innovation Breakthrough",
                        "Revolutionary quantum computing advancement announced", "Technology",
                        List.of("Quantum", "Computing", "Innovation")),
                new BookmarkResponse(3L, 1L, "https://example.com/news3", "Healthcare System Reform",
                        "New healthcare policies to be implemented nationwide", "Health",
                        List.of("Healthcare", "Policy", "Reform"))
        );

        // when
        Mockito.when(bookmarkService.getBookmarks(anyLong(), anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders
                        .put("/api/bookmarks/{bookmarkId}", bookmarkId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("put-bookmark",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        getRequestAuthHeader(),
                        pathParameters(
                                parameterWithName("bookmarkId").description("수정할 북마크 ID")
                        ),
                        requestFields(
                                fieldWithPath("groupId").description("현재 그룹 ID"),
                                fieldWithPath("afterGroupId").description("이동할 그룹 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("북마크 ID"),
                                fieldWithPath("[].title").description("뉴스 제목"),
                                fieldWithPath("[].summary").description("뉴스 요약"),
                                fieldWithPath("[].topic").description("뉴스 주제"),
                                fieldWithPath("[].keywords").description("키워드 목록"),
                                fieldWithPath("[].url").description("뉴스 URL"),
                                fieldWithPath("[].groupId").description("그룹 ID")
                        )
                ));
    }

    @Test
    void putMultiBookmark() throws Exception {
        // given
        BookmarkMultiPutRequest request = new BookmarkMultiPutRequest(List.of(1L, 2L), 1L, 2L);
        List<BookmarkResponse> mockResponse = List.of(
                new BookmarkResponse(1L, 2L, "https://example.com/news1", "Global Economic Summit Results",
                        "Leaders agree on new climate initiatives and trade policies", "Economy",
                        List.of("Economy", "Climate", "Trade")),
                new BookmarkResponse(2L, 2L, "https://example.com/news2", "Tech Innovation Breakthrough",
                        "Revolutionary quantum computing advancement announced", "Technology",
                        List.of("Quantum", "Computing", "Innovation")),
                new BookmarkResponse(3L, 2L, "https://example.com/news3", "Healthcare System Reform",
                        "New healthcare policies to be implemented nationwide", "Health",
                        List.of("Healthcare", "Policy", "Reform"))
        );

        // when
        Mockito.when(bookmarkService.getBookmarks(anyLong(), anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/bookmarks/group")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("put-multi-bookmark",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        getRequestAuthHeader(),
                        requestFields(
                                fieldWithPath("bookmarkIds").description("이동할 북마크 ID 목록"),
                                fieldWithPath("groupId").description("현재 그룹 ID"),
                                fieldWithPath("afterGroupId").description("이동할 그룹 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("북마크 ID"),
                                fieldWithPath("[].title").description("뉴스 제목"),
                                fieldWithPath("[].summary").description("뉴스 요약"),
                                fieldWithPath("[].topic").description("뉴스 주제"),
                                fieldWithPath("[].keywords").description("키워드 목록"),
                                fieldWithPath("[].url").description("뉴스 URL"),
                                fieldWithPath("[].groupId").description("그룹 ID")
                        )
                ));
    }

    @Test
    void deleteBookmark() throws Exception {
        // given
        long bookmarkId = 1L;

        // then
        mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/api/bookmarks/{bookmarkId}", bookmarkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete-bookmark",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        getRequestAuthHeader(),
                        pathParameters(
                                parameterWithName("bookmarkId").description("삭제할 북마크 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("삭제 완료 메시지")
                        )
                ));
    }

    @Test
    void saveBookmarkGroup() throws Exception {
        // given
        BookmarkGroupRequest request = new BookmarkGroupRequest("새 그룹");
        List<BookmarkGroupsResponse> mockResponse = List.of(
                new BookmarkGroupsResponse(1L, 240501L, "기본"),
                new BookmarkGroupsResponse(2L, 240502L, "새 그룹")
        );

        // when
        Mockito.when(bookmarkService.getBookmarkGroup(anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/bookmark-groups")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("save-bookmark-group",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        getRequestAuthHeader(),
                        requestFields(
                                fieldWithPath("name").description("북마크 그룹 이름")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("북마크 그룹 ID"),
                                fieldWithPath("[].name").description("그룹 이름"),
                                fieldWithPath("[].displayOrder").description("그룹 순서(ASC)")
                        )
                ));
    }

    @Test
    void renameBookmarkGroup() throws Exception {
        // given
        long groupId = 2L;
        BookmarkGroupRequest request = new BookmarkGroupRequest("변경된 그룹명");
        List<BookmarkGroupsResponse> mockResponse = List.of(
                new BookmarkGroupsResponse(1L, 240501L, "기본"),
                new BookmarkGroupsResponse(2L, 240502L, "변경된 그룹명")
        );

        // when
        Mockito.when(bookmarkService.getBookmarkGroup(anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/bookmark-groups/{groupId}", groupId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("rename-bookmark-group",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        getRequestAuthHeader(),
                        pathParameters(
                                parameterWithName("groupId").description("수정할 그룹 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").description("변경할 그룹 이름")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("북마크 그룹 ID"),
                                fieldWithPath("[].name").description("그룹 이름"),
                                fieldWithPath("[].displayOrder").description("그룹 순서(ASC)")
                        )
                ));
    }

    @Test
    void deleteBookmarkGroup() throws Exception {
        // given
        long groupId = 2L;
        List<BookmarkGroupsResponse> mockResponse = List.of(
                new BookmarkGroupsResponse(1L, 240501L, "기본")
        );

        // when
        Mockito.when(bookmarkService.getBookmarkGroup(anyLong())).thenReturn(mockResponse);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/api/bookmark-groups/{groupId}", groupId)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete-bookmark-group",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        getRequestAuthHeader(),
                        pathParameters(
                                parameterWithName("groupId").description("삭제할 그룹 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("북마크 그룹 ID"),
                                fieldWithPath("[].name").description("그룹 이름"),
                                fieldWithPath("[].displayOrder").description("그룹 순서(ASC)")
                        )
                ));
    }

    private RequestHeadersSnippet getRequestAuthHeader() {
        return requestHeaders(
                headerWithName("Authorization").description("JWT 인증 토큰")
        );
    }

}
