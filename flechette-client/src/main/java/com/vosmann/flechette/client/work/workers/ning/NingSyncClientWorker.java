package com.vosmann.flechette.client.work.workers.ning;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.vosmann.flechette.client.App;
import com.vosmann.flechette.client.work.workers.ning.pool.PoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

public class NingSyncClientWorker implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NingSyncClientWorker.class);

    private final NingSyncClient client;
    private final String url;
    private final MetricRegistry registry;
    private final String metricsPrefix;

    public NingSyncClientWorker(final String url, final MetricRegistry registry, final PoolConfig poolConfig,
                                final String metricsPrefix) {
        this.client = new NingSyncClient(poolConfig, App.REQUEST_TIMEOUT_IN_MS);
        this.url = url;
        this.registry = registry;
        this.metricsPrefix = metricsPrefix;
    }

    @Override
    public void run() {
        LOG.debug("Making a call to {}.", url);

        final Timer timer = registry.timer(MetricRegistry.name(NingSyncClientWorker.class, metricsPrefix));
        final Timer.Context context = timer.time();
        try {
            final Optional<String> response = client.get(url);
            LOG.debug("Call returned: {}", response);
        } catch (final RestClientException e) {
            LOG.error("HTTP call failed.", e);
        } finally {
            context.stop();
        }
    }

    @Override
    public String toString() {
        return "NingSyncClientWorker{" + "url='" + url + '\'' + ", metricsPrefix='" + metricsPrefix + '\'' + '}';
    }

}
