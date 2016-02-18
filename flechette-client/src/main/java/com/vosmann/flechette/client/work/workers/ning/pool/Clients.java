package com.vosmann.flechette.client.work.workers.ning.pool;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.providers.netty.NettyConnectionsPool;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Clients {

    private static final Logger LOG = LoggerFactory.getLogger(Clients.class);

    private Clients() { }

    public static AsyncHttpClient newNettyAsyncHttpClient(final int timeout) {
        return newNettyAsyncHttpClient(timeout, PoolConfig.defaultConfig());
    }

    public static AsyncHttpClient newNettyAsyncHttpClient(final int timeout, final PoolConfig poolConfig) {

        final NettyConnectionsPool pool = new NettyConnectionsPool( //
                poolConfig.getMaxTotalConnections(),                //
                poolConfig.getMaxNrConnectionsPerHost(),            //
                poolConfig.getMaxIdleTime(),                        //
                poolConfig.getMaxConnectionLifetime(),              //
                false,                                              //
                new HashedWheelTimer());

        final AsyncHttpClientConfig config =
            new AsyncHttpClientConfig.Builder().setConnectionTimeoutInMs(timeout)                                     //
                                               .setRequestTimeoutInMs(timeout)                                        //
                                               .setMaximumConnectionsPerHost(poolConfig.getMaxNrConnectionsPerHost()) //
                                               .setConnectionsPool(pool)                                              //
                                               .build();

        return new AsyncHttpClient(config);
    }

}
