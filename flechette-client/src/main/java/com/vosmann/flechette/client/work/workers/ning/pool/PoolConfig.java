package com.vosmann.flechette.client.work.workers.ning.pool;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Contains settings for configuring the AsyncHttpClient's connection pool. The static defaultConfig() factory method
 * should be provide sane defaults. More specific parameters can be specified via the builder.
 */
public class PoolConfig {

    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;
    public static final int DEFAULT_MAX_NR_CONNECTIONS_PER_HOST = 100;
    public static final int DEFAULT_MAX_IDLE_TIME = 1000;
    public static final int MAX_CONNECTION_LIFE_TIME = 1000;

    private final int maxTotalConnections;
    private final int maxNrConnectionsPerHost;
    private final int maxIdleTime;
    private final int maxConnectionLifeTime;

    public static final class Builder {

        private int maxTotalConnections = DEFAULT_MAX_TOTAL_CONNECTIONS;
        private int maxNrConnectionsPerHost = DEFAULT_MAX_NR_CONNECTIONS_PER_HOST;
        private int maxIdleTime = DEFAULT_MAX_IDLE_TIME;
        private int maxConnectionLifeTime = MAX_CONNECTION_LIFE_TIME;

        public Builder maxTotalConnections(final int maxTotalConnections) {
            this.maxTotalConnections = maxTotalConnections;
            return this;
        }

        public Builder maxNrConnectionsPerHost(final int maxNrConnectionsPerHost) {
            this.maxNrConnectionsPerHost = maxNrConnectionsPerHost;
            return this;
        }

        public Builder maxIdleTime(final int maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
            return this;
        }

        public Builder maxConnectionLifeTime(final int maxConnectionLifeTime) {
            this.maxConnectionLifeTime = maxConnectionLifeTime;
            return this;
        }

        public PoolConfig build() {
            checkArgument(maxTotalConnections > 0);
            checkArgument(maxNrConnectionsPerHost > 0);
            checkArgument(maxIdleTime >= 0);
            checkArgument(maxConnectionLifeTime > 0);
            return new PoolConfig(this);
        }
    }

    public static PoolConfig defaultConfig() {
        return new Builder().build();
    }

    public static PoolConfig oldConfig() {
        return new Builder().maxTotalConnections(100)
                .maxNrConnectionsPerHost(50)
                .maxIdleTime(0)
                .maxConnectionLifeTime(1000)
                .build();
    }

    private PoolConfig(final Builder builder) {
        maxTotalConnections = builder.maxTotalConnections;
        maxNrConnectionsPerHost = builder.maxNrConnectionsPerHost;
        maxIdleTime = builder.maxIdleTime;
        maxConnectionLifeTime = builder.maxConnectionLifeTime;
    }

    public int getMaxTotalConnections() {
        return maxTotalConnections;
    }

    public int getMaxNrConnectionsPerHost() {
        return maxNrConnectionsPerHost;
    }

    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    public int getMaxConnectionLifetime() {
        return maxConnectionLifeTime;
    }

    @Override
    public String toString() { return "PoolConfig{" + "maxTotalConnections=" + maxTotalConnections
                + ", maxNrConnectionsPerHost=" + maxNrConnectionsPerHost + ", maxIdleTime=" + maxIdleTime
                + ", maxConnectionLifeTime=" + maxConnectionLifeTime + '}';
    }

}
