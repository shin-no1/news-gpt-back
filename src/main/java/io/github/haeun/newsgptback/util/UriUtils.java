package io.github.haeun.newsgptback.util;

import io.github.haeun.newsgptback.domain.Site.Site;

import java.net.URI;

public class UriUtils {
    public static String getUrlNum(Site site, String url) {
        URI uri = URI.create(url);
        if (site.getId() == 1) {
            String[] split = uri.getPath().split("/");
            return split[split.length - 1];
        }
        return null;
    }
}
