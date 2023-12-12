package RateControl.Services;

import RateControl.Models.Metrics.RequestMetric;
import RateControl.Repositories.MetricsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MetricsService {
    private MetricsRepository metricsRepository;

    public MetricsService(MetricsRepository metricsRepository) {
        this.metricsRepository = metricsRepository;
    }

    public void saveMetric(RequestMetric metric) {
        metricsRepository.save(metric);
    }
}
