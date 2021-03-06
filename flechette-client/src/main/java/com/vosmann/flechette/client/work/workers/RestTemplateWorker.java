package com.vosmann.flechette.client.work.workers;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RestTemplateWorker implements Worker {

    private static final Logger LOG = LoggerFactory.getLogger(RestTemplateWorker.class);

    private final RestTemplate restTemplate;
    private final String url;
    private final MetricRegistry registry;
    private final String name;
    private final int connectTimeout;
    private final int readTimeout;

    public RestTemplateWorker(final String url, final MetricRegistry registry, final String name,
                              final int connectTimeout, final int readTimeout) {

        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(connectTimeout); // Get from manager.
        factory.setConnectTimeout(connectTimeout); // Create connection.
        factory.setReadTimeout(readTimeout); // Socket.

        this.restTemplate = new RestTemplate(factory);
        this.url = url;
        this.registry = registry;
        this.name = name;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public void run() {
        LOG.debug("Making a call to {}.", url);

        final Timer timer = registry.timer(MetricRegistry.name(RestTemplateWorker.class, name));
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
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "RestTemplateWorker{" + "url='" + url + '\'' + ", name='" + name + '\'' +
                ", connectTimeout=" + connectTimeout + ", readTimeout=" + readTimeout + '}';
    }

}
