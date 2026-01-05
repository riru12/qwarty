package com.qwarty.i18n.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.qwarty.i18n.service.I18nService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class I18nControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private I18nService i18nService;

    @Test
    void getGlobalTranslations_ShouldReturnOk() throws Exception {
        Map<String, String> dummyTranslations = new HashMap<>();
        dummyTranslations.put("qwarty", "qwarty");

        when(i18nService.getGlobalI18n()).thenReturn(dummyTranslations);

        mockMvc.perform(get("/i18n/global")).andExpect(status().isOk());

        verify(i18nService, times(1)).getGlobalI18n();
    }

    @Test
    void getTranslations_ShouldReturnOk_ForValidScreen() throws Exception {
        String screen = "login";
        Map<String, String> dummyTranslations = new HashMap<>();
        dummyTranslations.put("login", "log in");

        when(i18nService.getScreenI18n(screen)).thenReturn(dummyTranslations);

        mockMvc.perform(get("/i18n/{screen}", screen)).andExpect(status().isOk());

        verify(i18nService, times(1)).getScreenI18n(screen);
    }
}
