package com.jack.priceservice.controller;

import com.jack.priceservice.service.PriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceService priceService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetCurrentPrice() throws Exception {
        // Mock the behavior of PriceService
        when(priceService.getPrice()).thenReturn(300);

        // Perform GET request to /price/current with an authenticated user
        mockMvc.perform(get("/price/current"))
                .andExpect(status().isOk())
                .andExpect(content().string("300"));
    }
}
