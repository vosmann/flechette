package com.vosmann.flechette.server;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

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

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        final TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setPort(11000);
        return factory;
    }

    public static void main(final String[] args) throws Exception {
        addSigarNativeLib();
        SpringApplication.run(App.class, args);
    }

    private static void addSigarNativeLib() {

        final String javaLibraryPath = "lib:" + System.getProperty("java.library.path");
        System.setProperty("java.library.path", javaLibraryPath); // Relative path for Sigar lib.

        LOG.info("Updated with path to Sigar native binaries: java.library.path={}", javaLibraryPath);
    }

}
