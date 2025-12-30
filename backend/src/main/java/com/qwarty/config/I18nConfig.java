package com.qwarty.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class I18nConfig {

    @Bean
    public MessageSource messageSource() throws IOException {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        var resources = resolver.getResources("classpath:i18n/**/*.properties");

        Set<String> basenames = new HashSet<>();
        for (var resource : resources) {
            try {
                String path = resource.getURI().toString();

                int i18nIndex = path.indexOf("i18n/");
                if (i18nIndex == -1) continue;

                String relativePath = path.substring(i18nIndex);

                // Remove .properties and locale suffix (_en, _en_US, etc.)
                relativePath = relativePath.replaceAll("(_[a-z]{2}(_[A-Z]{2})?)?.properties$", "");

                basenames.add("classpath:" + relativePath);
            } catch (Exception e) {
                throw new RuntimeException("Failed to process resource: " + resource, e);
            }
        }

        messageSource.setBasenames(basenames.toArray(new String[0]));
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }
}
