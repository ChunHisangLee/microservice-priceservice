package com.jack.priceservice.mapper;

import com.jack.priceservice.dto.BTCPriceHistoryDTO;
import com.jack.priceservice.entity.BTCPriceHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BTCPriceHistoryMapperTest {

    private BTCPriceHistoryMapper btcPriceHistoryMapper;

    @BeforeEach
    void setUp() {
        btcPriceHistoryMapper = new BTCPriceHistoryMapper();
    }

    @Test
    void toDto_ShouldMapEntityToDto() {
        // Given
        BTCPriceHistory btcPriceHistory = BTCPriceHistory.builder()
                .id(1L)
                .price(450.0)
                .timestamp(LocalDateTime.of(2023, 1, 1, 12, 0))
                .build();

        // When
        BTCPriceHistoryDTO btcPriceHistoryDTO = btcPriceHistoryMapper.toDto(btcPriceHistory);

        // Then
        assertThat(btcPriceHistoryDTO).isNotNull();
        assertThat(btcPriceHistoryDTO.getId()).isEqualTo(1L);
        assertThat(btcPriceHistoryDTO.getPrice()).isEqualTo(450.0);
        assertThat(btcPriceHistoryDTO.getTimestamp()).isEqualTo(LocalDateTime.of(2023, 1, 1, 12, 0));
    }

    @Test
    void toEntity_ShouldMapDtoToEntity() {
        // Given
        BTCPriceHistoryDTO btcPriceHistoryDTO = BTCPriceHistoryDTO.builder()
                .id(1L)
                .price(450.0)
                .timestamp(LocalDateTime.of(2023, 1, 1, 12, 0))
                .build();

        // When
        BTCPriceHistory btcPriceHistory = btcPriceHistoryMapper.toEntity(btcPriceHistoryDTO);

        // Then
        assertThat(btcPriceHistory).isNotNull();
        assertThat(btcPriceHistory.getPrice()).isEqualTo(450.0);
        assertThat(btcPriceHistory.getTimestamp()).isEqualTo(LocalDateTime.of(2023, 1, 1, 12, 0));
    }

    @Test
    void toEntity_ShouldUseCurrentTimestampWhenTimestampIsNull() {
        // Given
        BTCPriceHistoryDTO btcPriceHistoryDTO = BTCPriceHistoryDTO.builder()
                .id(1L)
                .price(450.0)
                .timestamp(null)
                .build();

        // When
        BTCPriceHistory btcPriceHistory = btcPriceHistoryMapper.toEntity(btcPriceHistoryDTO);

        // Then
        assertThat(btcPriceHistory).isNotNull();
        assertThat(btcPriceHistory.getPrice()).isEqualTo(450.0);
        assertThat(btcPriceHistory.getTimestamp()).isNotNull();
        assertThat(btcPriceHistory.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}
