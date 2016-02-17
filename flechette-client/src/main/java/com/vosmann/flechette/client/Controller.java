package com.vosmann.flechette.client;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private MetricRegistry registry;
    @Autowired
    private SystemMetricsService systemMetricsService;

    @RequestMapping("/health")
    @ResponseBody
    String health() {
        return "Running.";
    }

    @RequestMapping("/metrics")
    @ResponseBody
    Map<String, Metric> metrics() {
        return registry.getMetrics();
    }

    @RequestMapping("/system-metrics")
    @ResponseBody
    SystemMetrics systemMetrics() {
        return systemMetricsService.get();

    }

}
