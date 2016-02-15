package com.vosmann.flechette;

import org.hyperic.sigar.NetConnection;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemMetricsService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemMetricsService.class);

    private Sigar sigar;

    public SystemMetricsService() {
        this.sigar = new Sigar();
    }

    public SystemMetrics get() {
        try {
            final SystemMetrics.Builder metrics = new SystemMetrics.Builder();

            metrics.cpu(sigar.getCpu());
            metrics.mem(sigar.getMem());
            metrics.tcp(sigar.getTcp());
            metrics.tcpEstablishedCount(getCount(NetFlags.TCP_ESTABLISHED));
            metrics.tcpIdleCount(getCount(NetFlags.TCP_IDLE));
            metrics.tcpCloseCount(getCount(NetFlags.TCP_CLOSE));
            metrics.tcpClosingCount(getCount(NetFlags.TCP_CLOSING));
            metrics.tcpCloseWaitCount(getCount(NetFlags.TCP_CLOSE_WAIT));
            metrics.tcpTimeWaitCount(getCount(NetFlags.TCP_TIME_WAIT));
            metrics.tcpBoundCount(getCount(NetFlags.TCP_BOUND));

            return metrics.build();

        } catch (final SigarException e) {
            LOG.error("Could not get system metrics from Sigar.", e);
            return new SystemMetrics.Builder().build();
        }
    }

    private int getCount(final int flags) throws SigarException {
        final NetConnection[] connections = sigar.getNetConnectionList(flags);
        if (connections == null) {
            return 0;
        }

        return connections.length;
    }

}
