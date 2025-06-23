package io.github.haeun.newsgptback.news.domain.newsSummary;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SummaryId implements Serializable {

    @Column(name = "site_id")
    private Long siteId;

    @Column(name = "url_num", length = 255)
    private String urlNum;
}