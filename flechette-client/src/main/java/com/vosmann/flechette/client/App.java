package com.vosmann.flechette.client;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vosmann.flechette.client.work.Launcher;
import com.vosmann.flechette.client.work.workers.Worker;
import com.vosmann.flechette.client.work.workers.ning.NingSyncClientWorker;
import com.vosmann.flechette.client.work.workers.RestTemplateWorker;
import com.vosmann.flechette.client.work.workers.ning.pool.PoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootApplication
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    // Wait until a connection is established.
    // Not to be confused with waiting for a connection from a connection manager, what can be fast if reused connection.
    public static final int CONNECT_TIMEOUT_IN_MS = 1000;
    public static final int REQUEST_TIMEOUT_IN_MS = 100;

    @Value("${url}")
    private String url;
    @Value("${thread.count}")
    private int threadCount;
    @Value("${rampup.time}")
    private long rampUpTime;
    @Value("${rampup.timeunit}")
    private TimeUnit rampUpTimeUnit;
    @Value("${execution.period}")
    private long executionPeriod;
    @Value("${execution.period.timeunit}")
    private TimeUnit executionPeriodTimeUnit;
    @Value("#{'${allowed.workers}'.split(',')}")
    private Set<String> allowedWorkers;

    @Bean
    public Worker ningSyncClientOldtWorker(final MetricRegistry registry) {
        final PoolConfig poolConfig = PoolConfig.oldConfig();
        return new NingSyncClientWorker(url, registry, poolConfig, "old");
    }

    @Bean
    public Worker ningSyncClientDefaultWorker(final MetricRegistry registry) {
        final PoolConfig poolConfig = PoolConfig.defaultConfig();
        return new NingSyncClientWorker(url, registry, poolConfig, "default");
    }

    @Bean
    public Worker ningSyncClientLongWorker(final MetricRegistry registry) {
        final int tenSeconds = 10000;
        final PoolConfig poolConfig = new PoolConfig.Builder().maxConnectionLifeTime(tenSeconds).build();
        return new NingSyncClientWorker(url, registry, poolConfig, "long");
    }

    @Bean
    public Worker ningSyncClientIdleWorker(final MetricRegistry registry) {
        final int tenSeconds = 10000;
        final PoolConfig poolConfig = new PoolConfig.Builder().maxIdleTime(tenSeconds).build();
        return new NingSyncClientWorker(url, registry, poolConfig, "idle");
    }

    @Bean
    public Worker ningSyncClientLongIdleWorker(final MetricRegistry registry) {
        final int tenSeconds = 10000;
        final PoolConfig poolConfig = new PoolConfig.Builder()
                .maxConnectionLifeTime(tenSeconds)
                .maxIdleTime(tenSeconds)
                .build();
        return new NingSyncClientWorker(url, registry, poolConfig, "longIdle");
    }

    @Bean
    public Worker restTemplateWorker(final MetricRegistry registry, final ClientHttpRequestFactory requestFactory) {
        return new RestTemplateWorker(url, registry, "restTemplateDefault", requestFactory);
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(REQUEST_TIMEOUT_IN_MS); // Socket.
        factory.setConnectionRequestTimeout(REQUEST_TIMEOUT_IN_MS); // Get from manager.
        factory.setConnectTimeout(CONNECT_TIMEOUT_IN_MS); // Create connection.
        return factory;
    }

    @Bean
    public List<Launcher> launchers(final List<Worker> workers) {

        LOG.info("Allowed workers: {}", allowedWorkers);

        final List<Launcher> launchers = workers.stream()
                .filter(worker -> allowedWorkers.contains(worker.getName()))
                .map(worker -> new Launcher.Builder()
                            .threadCount(threadCount)
                            .rampUpTime(rampUpTime)
                            .rampUpTimeUnit(rampUpTimeUnit)
                            .executionPeriod(executionPeriod)
                            .executionPeriodTimeUnit(executionPeriodTimeUnit)
                            .worker(worker)
                            .build())
                .collect(Collectors.toList());

        launchers.forEach(launcher -> LOG.info("Created a launcher: {}", launcher));
        return launchers;
    }

    @Bean
    public MetricRegistry metricRegistry() {
        final MetricRegistry registry = new MetricRegistry();
        // registry.registerAll(new MemoryUsageGaugeSet());
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
        factory.setPort(22000);
        return factory;
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
