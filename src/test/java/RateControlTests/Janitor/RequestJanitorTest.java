package RateControlTests.Janitor;

import RateControl.Janitor.RequestJanitor;
import RateControl.Repositories.ApiProcessingRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RequestJanitorTest {
    @InjectMocks
    private RequestJanitor requestJanitor;
    @Mock
    private ApiProcessingRepository apiProcessingRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRemoveOldRequests() {
        List<String> requestSets = List.of("test", "api", "keys");
        final Double timestampDayAgo = Double.valueOf((Instant.now().minusSeconds(86400)).getEpochSecond());

        when(apiProcessingRepository.getAllApiRequestSets()).thenReturn(requestSets);

        requestJanitor.removeOldRequests();

        for (var i : requestSets) {
            verify(apiProcessingRepository, times(1)).removeRequests(eq(i), eq(timestampDayAgo));
        }
        verify(apiProcessingRepository, times(1)).getAllApiRequestSets();
    }

    @Test
    public void testRemoveOldRequestsNoKeys() {
        List<String> requestSets = Collections.emptyList();

        when(apiProcessingRepository.getAllApiRequestSets()).thenReturn(requestSets);

        requestJanitor.removeOldRequests();

        verify(apiProcessingRepository, times(0)).removeRequests(any(), any());
        verify(apiProcessingRepository, times(1)).getAllApiRequestSets();
    }

    @Test
    public void testAggregateRequestData() {
        List<String> requestSets = List.of("test", "api", "keys");
        final Instant timestamp = Instant.now();
        final Double timestampMinuteAgo = Double.valueOf((timestamp.minusSeconds(60)).getEpochSecond());

        when(apiProcessingRepository.getAllApiRequestSets()).thenReturn(requestSets);
        when(apiProcessingRepository.getNumberOfRequests(any(), any(), any())).thenReturn(150);

        requestJanitor.aggregateRequestData();

        for (var i : requestSets) {
            verify(apiProcessingRepository, times(1)).getNumberOfRequests(eq(i), eq(timestampMinuteAgo), eq(Double.valueOf(timestamp.getEpochSecond())));
        }
        verify(apiProcessingRepository, times(1)).getAllApiRequestSets();
        verify(apiProcessingRepository, times(requestSets.size())).saveMinuteRequestsValue(anyString(), anyInt());
    }

    @Test
    public void testAggregateRequestDataNoKeys() {
        List<String> requestSets = Collections.emptyList();

        when(apiProcessingRepository.getAllApiRequestSets()).thenReturn(requestSets);
        when(apiProcessingRepository.getNumberOfRequests(any(), any(), any())).thenReturn(150);

        requestJanitor.aggregateRequestData();

        verify(apiProcessingRepository, times(0)).getNumberOfRequests(any(), any(), any());
        verify(apiProcessingRepository, times(requestSets.size())).saveMinuteRequestsValue(anyString(), anyInt());
    }
}
