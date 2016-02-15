package com.vosmann.flechette;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class App {

    @Bean
    public StringService stringService() {
        return new NopService();
    }

    @Bean
    public MetricRegistry metricRegistry() {
        final MetricRegistry registry = new MetricRegistry();
        registry.registerAll(new MemoryUsageGaugeSet());
        registry.registerAll(new ThreadStatesGaugeSet());
        // registry.registerAll(new BufferPoolMetricSet());
        return registry;
    }

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new MetricsModule(TimeUnit.SECONDS, TimeUnit.MILLISECONDS, false));
        return mapper;
    }

    @Bean
    public SystemMetricsService systemMetricsService() {
        return new SystemMetricsService();
    }

    public static void main(final String[] args) throws Exception {
        addSigarNativeLib();
        SpringApplication.run(App.class, args);
    }

    private static void addSigarNativeLib() {
        final String javaLibraryPath = System.getProperty("java.library.path");
        System.setProperty("java.library.path", "lib:" + javaLibraryPath); // Relative path for Sigar lib.
    }

}
