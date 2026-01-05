package com.qwarty.i18n.controller;

import com.qwarty.i18n.service.I18nService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/i18n")
public class I18nController {

    private final I18nService i18nService;

    @GetMapping("/global")
    public ResponseEntity<Map<String, String>> getGlobalTranslations() {
        Map<String, String> translations = i18nService.getGlobalI18n();
        return ResponseEntity.ok(translations);
    }

    @GetMapping("/{screen}")
    public ResponseEntity<Map<String, String>> getTranslations(@PathVariable String screen) {
        Map<String, String> translations = i18nService.getScreenI18n(screen);
        return ResponseEntity.ok(translations);
    }
}
