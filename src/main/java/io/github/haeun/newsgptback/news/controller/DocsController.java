package io.github.haeun.newsgptback.news.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocsController {
    @GetMapping("/.well-known/appspecific/com.chrome.devtools.json")
    public ResponseEntity<Void> ignoreChromeDevtoolsRequest() {
        return ResponseEntity.ok().build(); // 200 OK 응답
    }
}
