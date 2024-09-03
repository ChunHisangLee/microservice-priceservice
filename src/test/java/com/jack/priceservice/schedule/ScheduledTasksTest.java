package com.jack.priceservice.schedule;

import com.jack.priceservice.entity.BTCPriceHistory;
import com.jack.priceservice.repository.BTCPriceHistoryRepository;
import com.jack.priceservice.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledTasksTest {

    @Mock
    private PriceService priceService;

    @Mock
    private BTCPriceHistoryRepository btcPriceHistoryRepository;

    @InjectMocks
    private ScheduledTasks scheduledTasks;

    @Captor
    private ArgumentCaptor<BTCPriceHistory> priceHistoryCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scheduledTasks, "currentPrice", 100);
        ReflectionTestUtils.setField(scheduledTasks, "isIncreasing", true);

        // Reset mocks to avoid unwanted interactions
        reset(priceService, btcPriceHistoryRepository);
    }

    @Test
    void shouldIncreasePriceUpToMaxAndThenDecrease() {
        // Simulate faster invocations of the scheduled task
        for (int i = 0; i < 36; i++) {
            scheduledTasks.updateCurrentPrice();
        }

        // Verify that the repository was called 36 times
        verify(btcPriceHistoryRepository, times(36)).save(priceHistoryCaptor.capture());
        verify(priceService, times(36)).setPrice(anyInt());

        // Verify the last call to the repository and service
        BTCPriceHistory lastInvocation = priceHistoryCaptor.getAllValues().get(35);
        assertThat(lastInvocation.getPrice()).isEqualTo(460);

        // Simulate the next step where the price decreases from the max value
        scheduledTasks.updateCurrentPrice();
        verify(btcPriceHistoryRepository, times(37)).save(priceHistoryCaptor.capture());

        BTCPriceHistory decreaseInvocation = priceHistoryCaptor.getValue();
        assertThat(decreaseInvocation.getPrice()).isEqualTo(450);
    }

    @Test
    void shouldDecreasePriceDownToMinAndThenIncrease() {
        // Set the initial conditions to start decreasing
        ReflectionTestUtils.setField(scheduledTasks, "currentPrice", 460);
        ReflectionTestUtils.setField(scheduledTasks, "isIncreasing", false);

        // Simulate faster invocations of the scheduled task
        for (int i = 0; i < 36; i++) {
            scheduledTasks.updateCurrentPrice();
        }

        // Verify that the repository was called 36 times
        verify(btcPriceHistoryRepository, times(36)).save(priceHistoryCaptor.capture());
        verify(priceService, times(36)).setPrice(anyInt());

        // Verify the last call to the repository and service (when price reaches minimum)
        BTCPriceHistory lastInvocation = priceHistoryCaptor.getAllValues().get(35);
        assertThat(lastInvocation.getPrice()).isEqualTo(100);

        // Simulate the next step where the price increases from the min value
        scheduledTasks.updateCurrentPrice();
        verify(btcPriceHistoryRepository, times(37)).save(priceHistoryCaptor.capture());

        BTCPriceHistory increaseInvocation = priceHistoryCaptor.getValue();
        assertThat(increaseInvocation.getPrice()).isEqualTo(110);
    }
}
