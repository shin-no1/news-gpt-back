package io.github.haeun.newsgptback.service;

import io.github.haeun.newsgptback.domain.Site.Site;
import io.github.haeun.newsgptback.domain.newsSummary.NewsSummary;
import io.github.haeun.newsgptback.domain.newsSummary.NewsSummaryRepository;
import io.github.haeun.newsgptback.domain.newsSummary.SummaryId;
import io.github.haeun.newsgptback.domain.newsSummaryHistory.NewsSummaryHistory;
import io.github.haeun.newsgptback.domain.newsSummaryHistory.NewsSummaryHistoryRepository;
import io.github.haeun.newsgptback.domain.projection.NewsSummaryMetaProjection;
import io.github.haeun.newsgptback.dto.GptResponse;
import io.github.haeun.newsgptback.dto.NewsResponse;
import io.github.haeun.newsgptback.enums.HistorySource;
import io.github.haeun.newsgptback.gpt.GptClient;
import io.github.haeun.newsgptback.parser.JsoupNewsParser;
import io.github.haeun.newsgptback.record.NewsInfo;
import io.github.haeun.newsgptback.util.UriUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@AllArgsConstructor
@Service
public class NewsService {
    private final JsoupNewsParser jsoupNewsParser;
    private final GptClient gptClient;
    private final NewsSummaryHistoryRepository newsSummaryHistoryRepository;
    private final NewsSummaryRepository newsSummaryRepository;

    /**
     * 뉴스 기사 URL을 입력받아, 기사 내용을 크롤링하고 GPT를 통해 요약 결과를 생성
     *
     * @param url 뉴스 기사 원문 URL
     * @return 요약된 뉴스 응답 객체
     */
    public NewsResponse getNewsResponse(String url) {
        long startTime = System.currentTimeMillis();
        NewsInfo newsInfo = jsoupNewsParser.parse(url);
        if (newsInfo == null) return null;

        Long summaryId = getSummaryId(newsInfo);

        GptResponse gptResponse;
        if (summaryId == null) {
            gptResponse = gptClient.summarize(newsInfo);
            if (gptResponse == null) return null;
        } else {
            NewsSummaryHistory newsSummaryHistory = newsSummaryHistoryRepository.findById(summaryId).get();
            gptResponse = new GptResponse();
            gptResponse.setSummary(newsSummaryHistory.getSummary());
            gptResponse.setTopic(newsSummaryHistory.getTopic());
            gptResponse.setKeywords(newsSummaryHistory.getKeywords());
        }

        NewsResponse newsResponse = new NewsResponse(newsInfo.title(), gptResponse.getSummary(), gptResponse.getTopic(), gptResponse.getKeywords(), url);
        long endTime = System.currentTimeMillis();
        saveLog(newsResponse, Math.round(endTime - startTime), summaryId == null);
        return newsResponse;
    }

    private Long getSummaryId(NewsInfo newsInfo) {
        Site site = new Site();
        site.setId(1L);
        Optional<NewsSummaryMetaProjection> newsSummaryOptional = newsSummaryRepository.findSummaryIdById(site.getId(), UriUtils.getUrlNum(site, newsInfo.url()));
        if (newsSummaryOptional.isEmpty()) {
            return null;
        }
        NewsSummaryMetaProjection newsSummary = newsSummaryOptional.get();
        if (newsSummary.getPromptVersion().equals(gptClient.promptVersion)) {
            return newsSummary.getSummaryId();
        }
        return null;
    }


    private void saveLog(NewsResponse newsResponse, int responseTimeMs, boolean isNew) {
        // 현재 site_id = 0, status = SUCCESS 고정
        NewsSummaryHistory history = new NewsSummaryHistory();
        Site site = new Site();
        site.setId(1L);
        String urlNum = UriUtils.getUrlNum(site, newsResponse.getUrl());

        history.setSite(site);
        history.setTitle(newsResponse.getTitle());
        history.setUrlNum(urlNum);
        history.setUrl(newsResponse.getUrl());
        history.setPromptVersion(String.valueOf(gptClient.promptVersion));
        history.setTitle(newsResponse.getTitle());
        history.setSummary(newsResponse.getSummary());
        history.setTopic(newsResponse.getTopic());
        history.setKeywords(newsResponse.getKeywords());
        history.setResponseTimeMs(responseTimeMs);
        history.setSource(isNew ? HistorySource.NEW : HistorySource.CACHE);

        newsSummaryHistoryRepository.save(history);

        if (isNew) {
            SummaryId summaryId = new SummaryId(site.getId(), urlNum);
            Optional<NewsSummary> optional = newsSummaryRepository.findById(summaryId);
            if (optional.isPresent()) {
                NewsSummary existing = optional.get();
                existing.setPromptVersion(gptClient.promptVersion);
                existing.setSummary(history);
                newsSummaryRepository.save(existing); // UPDATE
            } else {
                NewsSummary newsSummary = new NewsSummary();
                newsSummary.setId(summaryId);
                newsSummary.setSite(site);
                newsSummary.setSummary(history);
                newsSummary.setPromptVersion(gptClient.promptVersion);
                newsSummaryRepository.save(newsSummary); // INSERT
            }
        }
    }


}
