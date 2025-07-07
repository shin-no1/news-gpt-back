package io.github.haeun.newsgptback.common.config;

import io.github.haeun.newsgptback.common.filter.MdcFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {
    /**
     * MDC (Mapped Diagnostic Context) 필터를 등록하여 각각의 들어오는 HTTP 요청에 대해
     * 로깅 컨텍스트에 고유한 추적 ID를 포함시킵니다.
     *
     * @return {@link MdcFilter}를 등록하도록 구성된 {@link FilterRegistrationBean}으로,
     * 필터 체인에서 가장 높은 우선순위로 설정되어 요청을 초기에 처리합니다.
     */
    @Bean
    public FilterRegistrationBean<MdcFilter> loggingMdcFilter() {
        FilterRegistrationBean<MdcFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new MdcFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
