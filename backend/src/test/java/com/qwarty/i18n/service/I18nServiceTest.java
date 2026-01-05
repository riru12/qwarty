package com.qwarty.i18n.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@ExtendWith(MockitoExtension.class)
class I18nServiceTest {

    @InjectMocks
    private I18nService i18nService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource mockResource;

    @BeforeEach
    void setup() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @Test
    void getGlobalI18n_shouldReturnTranslations() throws Exception {
        String qwartyKey = "qwarty";
        String qwartyProp = "qwarty translation";
        String racerKey = "racer";
        String racerProp = "racer translation";

        when(resourceLoader.getResource("classpath:i18n/messages.properties")).thenReturn(mockResource);
        when(mockResource.exists()).thenReturn(true);

        String propsContent = String.format("""
            %s=%s
            %s=%s
            """, qwartyKey, qwartyProp, racerKey, racerProp);
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream(propsContent.getBytes(StandardCharsets.UTF_8)));

        when(messageSource.getMessage(eq(qwartyKey), any(), any())).thenReturn(qwartyProp);
        when(messageSource.getMessage(eq(racerKey), any(), any())).thenReturn(racerProp);

        Map<String, String> translations = i18nService.getGlobalI18n();

        assertEquals(2, translations.size());
        assertEquals(qwartyProp, translations.get(qwartyKey));
        assertEquals(racerProp, translations.get(racerKey));

        verify(resourceLoader, atLeastOnce()).getResource(anyString());
        verify(messageSource, times(1)).getMessage(qwartyKey, null, Locale.ENGLISH);
        verify(messageSource, times(1)).getMessage(racerKey, null, Locale.ENGLISH);
    }

    @Test
    void getScreenI18n_shouldReturnTranslations() throws Exception {
        String usernameKey = "username";
        String usernameProp = "username translation";
        String passwordKey = "password";
        String passwordProp = "password translation";

        String screen = "login";
        when(resourceLoader.getResource("classpath:i18n/screens/" + screen + ".properties")).thenReturn(mockResource);
        when(mockResource.exists()).thenReturn(true);

        String propsContent = String.format("""
            %s=%s
            %s=%s
            """, usernameKey, usernameProp, passwordKey, passwordProp);
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream(propsContent.getBytes(StandardCharsets.UTF_8)));

        when(messageSource.getMessage(eq(usernameKey), any(), any())).thenReturn(usernameProp);
        when(messageSource.getMessage(eq(passwordKey), any(), any())).thenReturn(passwordProp);

        Map<String, String> translations = i18nService.getScreenI18n(screen);

        assertEquals(2, translations.size());
        assertEquals(usernameProp, translations.get(usernameKey));
        assertEquals(passwordProp, translations.get(passwordKey));

        verify(resourceLoader, atLeastOnce()).getResource(anyString());
        verify(messageSource, times(1)).getMessage(usernameKey, null, Locale.ENGLISH);
        verify(messageSource, times(1)).getMessage(passwordKey, null, Locale.ENGLISH);
    }

    @Test
    void getGlobalI18n_resourceDoesNotExist_shouldReturnEmptyMap() throws Exception {
        when(resourceLoader.getResource("classpath:i18n/messages.properties")).thenReturn(mockResource);
        when(mockResource.exists()).thenReturn(false);

        Map<String, String> translations = i18nService.getGlobalI18n();
        assertTrue(translations.isEmpty());

        verify(resourceLoader, atLeastOnce()).getResource(anyString());
        verify(messageSource, times(0)).getMessage(anyString(), any(), any());
    }

    @Test
    void getScreenI18n_resourceDoesNotExist_shouldReturnEmptyMap() throws Exception {
        String screen = "login";
        when(resourceLoader.getResource("classpath:i18n/screens/" + screen + ".properties")).thenReturn(mockResource);
        when(mockResource.exists()).thenReturn(false);

        Map<String, String> translations = i18nService.getScreenI18n(screen);
        assertTrue(translations.isEmpty());

        verify(resourceLoader, atLeastOnce()).getResource(anyString());
        verify(messageSource, times(0)).getMessage(anyString(), any(), any());
    }
}