package com.qwarty.i18n.service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class I18nService {

    private final MessageSource messageSource;
    private final ResourceLoader resourceLoader;

    /**
     * Load translations from messages.properties, which is intended to be available globally
     */
    public Map<String, String> getGlobalI18n() {
        Locale locale = LocaleContextHolder.getLocale();

        Set<String> keys = loadKeys("i18n/messages", locale);

        Map<String, String> result = new TreeMap<>();
        for (String key : keys) {
            result.put(key, messageSource.getMessage(key, null, locale));
        }

        return result;
    }

    /**
     * Load translations for a particular screen
     */
    public Map<String, String> getScreenI18n(String screen) {
        Locale locale = LocaleContextHolder.getLocale();

        Set<String> keys = new HashSet<>();
        keys.addAll(loadKeys("i18n/screens/" + screen, locale));

        Map<String, String> result = new TreeMap<>();
        for (String key : keys) {
            result.put(key, messageSource.getMessage(key, null, locale));
        }

        return result;
    }

    /**
     * Retrieves all the keys for a particular screen given the .properties file's basename and a locale.
     *
     * e.g. For the login screen with en_US locale, this will retrieve keys in:
     *      login_en_US.properties
     *      login_en.properties
     *      login.properties
     */
    private Set<String> loadKeys(String basename, Locale locale) {
        List<String> candidates = new ArrayList<>();

        if (!locale.getCountry().isEmpty()) {
            candidates.add(
                    basename + "_" + locale.getLanguage() + "_" + locale.getCountry()); // e.g. login_en_US.properties
        }
        if (!locale.getLanguage().isEmpty()) {
            candidates.add(basename + "_" + locale.getLanguage()); // e.g. login_en.properties
        }
        candidates.add(basename); // e.g. login.properties

        Set<String> keys = new HashSet<>();
        for (String candidate : candidates) {
            try {
                Resource resource = resourceLoader.getResource("classpath:" + candidate + ".properties");

                if (!resource.exists()) continue;

                Properties props = new Properties();
                props.load(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
                keys.addAll(props.stringPropertyNames());
            } catch (Exception ignored) {
                // do not throw an error, simply continue on processing the next resource candidate
            }
        }

        return keys;
    }
}
