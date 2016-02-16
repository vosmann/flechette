package com.vosmann.flechette.client.work.workers;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RestTemplateWorker implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RestTemplateWorker.class);

    private final RestTemplate restTemplate;
    private final String url;
    private final MetricRegistry registry;

    public RestTemplateWorker(final String url, final MetricRegistry registry) {
        this.restTemplate = new RestTemplate();
        this.url = url;
        this.registry = registry;
    }

    @Override
    public void run() {
        LOG.debug("Making a call to {}.", url);

        final Timer timer = registry.timer(MetricRegistry.name(RestTemplateWorker.class, "default"));
        final Timer.Context context = timer.time();
        try {
            final String response = restTemplate.getForObject(url, String.class);
            LOG.debug("Call returned: {}", response);
        } catch (final RestClientException e) {
            LOG.error("HTTP call failed.", e);
        } finally {
            context.stop();
        }
    }

    @Override
    public String toString() {
        return "RestTemplateWorker{" + "url='" + url + '\'' + '}';
    }

}
