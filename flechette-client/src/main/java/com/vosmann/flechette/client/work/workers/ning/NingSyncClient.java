package com.vosmann.flechette.client.work.workers.ning;

import com.google.common.base.Stopwatch;
import com.ning.http.client.*;
import com.vosmann.flechette.client.work.workers.ning.pool.Clients;
import com.vosmann.flechette.client.work.workers.ning.pool.PoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * A Ning AsyncHttpClient-based implementation. Adapted from the early days of reco. Contains a bit of time measurement.
 */
public class NingSyncClient<T> {

    private static final Logger LOG = LoggerFactory.getLogger(NingSyncClient.class);

    private final AsyncHttpClient asyncHttpClient;
    private final PoolConfig poolConfig;

    public NingSyncClient(final PoolConfig poolConfig, final int timeout) {
        this.asyncHttpClient = Clients.newNettyAsyncHttpClient(timeout, poolConfig);
        this.poolConfig = poolConfig;
        LOG.info("Initialized a Client: {}", this);
    }

    public Optional<String> get(final String url) {

        final Stopwatch prepTime = Stopwatch.createUnstarted();
        final Stopwatch netTime = Stopwatch.createUnstarted();

        prepTime.start();

        final AsyncHandler<String> handler = new GetHandler();
        final Future<String> future;

        try {
            future = asyncHttpClient.prepareGet(url).execute(handler);
        } catch (final IOException e) {
            prepTime.stop();
            LOG.error("GET on URL: {} failed on prep (took {} ms).", url, prepTime.elapsed(MILLISECONDS));
            return Optional.empty();
        }

        prepTime.stop();
        netTime.start();

        try {
            final String received = future.get();

            netTime.stop();
            LOG.debug("GET on URL: {} took {} ms (prep took {} ms).", //
                url, netTime.elapsed(MILLISECONDS), prepTime.elapsed(MILLISECONDS));

            return Optional.of(received);

        } catch (final InterruptedException | ExecutionException | RuntimeException e) {

            netTime.stop();
            LOG.error("GET on URL: {} failed after {} ms (prep took {} ms).", //
                url, netTime.elapsed(MILLISECONDS), prepTime.elapsed(MILLISECONDS));

            return Optional.empty();
        }
    }

    // public class GetHandler<T> extends AsyncCompletionHandler<T> {
    public static class GetHandler extends AsyncCompletionHandler<String> {

        private static final Logger LOG = LoggerFactory.getLogger(GetHandler.class);
        // private final Class<T> responseClass;

        @SuppressWarnings("unchecked")
        @Override
        public String onCompleted(final Response response) throws Exception {

            try {
                return response.getResponseBody();
            } catch (final RuntimeException e) {
                LOG.error("Error while getting response body.", e);
                throw e;
            }
        }
        @Override
        public STATE onStatusReceived(final HttpResponseStatus status) throws Exception {
            LOG.debug("GET status received: {}.", status.getStatusCode());
            return super.onStatusReceived(status);
        }
        @Override
        public void onThrowable(final Throwable t) {
            LOG.error("Problem in GET.", t);
        }

    }

    @Override
    public String toString() {
        return "NingSyncClient{" + "poolConfig=" + poolConfig + '}';
    }

}
