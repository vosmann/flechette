package com.vosmann.flechette.client.work.workers.resttemplate;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.vosmann.flechette.client.work.workers.Worker;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class BigPoolRestTemplateWorker implements Worker {

    private static final Logger LOG = LoggerFactory.getLogger(BigPoolRestTemplateWorker.class);

    private static final int MAX_CONN_TOTAL = 100;

    private final RestTemplate restTemplate;
    private final String url;
    private final MetricRegistry registry;
    private final String name;
    private final int connectTimeout;
    private final int readTimeout;

    public BigPoolRestTemplateWorker(final String url, final MetricRegistry registry, final String name,
                                     final int connectTimeout, final int readTimeout) {

        final HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(MAX_CONN_TOTAL)
                // .setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
                .build();

        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
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

        final Timer timer = registry.timer(MetricRegistry.name(BigPoolRestTemplateWorker.class, name));
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
        return "BigPoolRestTemplateWorker{" + "url='" + url + '\'' + ", name='" + name + '\''
                + ", connectTimeout=" + connectTimeout + ", readTimeout=" + readTimeout
                + ", MAX_CONN_TOTAL=" + MAX_CONN_TOTAL + '}';
    }

}
