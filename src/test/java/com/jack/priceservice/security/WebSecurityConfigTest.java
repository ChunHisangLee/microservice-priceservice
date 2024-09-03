package com.jack.priceservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; // Standard MockMvc GET
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(WebSecurityConfig.class)
class WebSecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private WebSecurityConfig webSecurityConfig;

    @Test
    void testSecurityFilterChainBean() throws Exception {
        // Assert that the SecurityFilterChain bean is not null
        SecurityFilterChain securityFilterChain = webSecurityConfig.securityFilterChain(context.getBean(HttpSecurity.class));
        assertNotNull(securityFilterChain, "SecurityFilterChain bean should not be null");
    }

    @Test
    void testAuthenticationEnabled() throws Exception {
        // Set authenticationEnabled to true
        ReflectionTestUtils.setField(webSecurityConfig, "authenticationEnabled", true);

        mockMvc = webAppContextSetup(context).apply(springSecurity()).build();

        // Perform a request to check that authentication is required
        mockMvc.perform(get("/price/current"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAuthenticationDisabled() throws Exception {
        // Set authenticationEnabled to false
        ReflectionTestUtils.setField(webSecurityConfig, "authenticationEnabled", false);

        mockMvc = webAppContextSetup(context).apply(springSecurity()).build();

        // Perform a request to check that no authentication is required
        mockMvc.perform(get("/price/current"))
                .andExpect(status().isOk());
    }
}
