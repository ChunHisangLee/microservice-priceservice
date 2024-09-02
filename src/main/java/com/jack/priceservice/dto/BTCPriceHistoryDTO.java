package com.jack.priceservice.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BTCPriceHistoryDTO {
    private Long id;
    private double price;
    private LocalDateTime timestamp;
}
