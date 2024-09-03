package com.jack.priceservice.repository;

import com.jack.priceservice.entity.BTCPriceHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BTCPriceHistoryRepositoryTest {

    @Container
    @SuppressWarnings("resource")
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private BTCPriceHistoryRepository btcPriceHistoryRepository;

    private BTCPriceHistory btcPriceHistory;

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        btcPriceHistory = new BTCPriceHistory();
        btcPriceHistory.setPrice(450.0);
        btcPriceHistory.setTimestamp(LocalDateTime.now());
    }

    @Test
    void testSaveAndFindTopByOrderByTimestampDesc() {
        BTCPriceHistory savedRecord = btcPriceHistoryRepository.save(btcPriceHistory);

        Optional<BTCPriceHistory> latestRecord = btcPriceHistoryRepository.findTopByOrderByTimestampDesc();
        assertThat(latestRecord).isPresent();
        assertThat(latestRecord.get().getPrice()).isEqualTo(450.0);
        assertThat(latestRecord.get().getTimestamp()).isEqualTo(savedRecord.getTimestamp());
    }

    @Test
    void testFindTopByOrderByTimestampDesc_NoRecords() {
        Optional<BTCPriceHistory> latestRecord = btcPriceHistoryRepository.findTopByOrderByTimestampDesc();
        assertThat(latestRecord).isEmpty();
    }
}
