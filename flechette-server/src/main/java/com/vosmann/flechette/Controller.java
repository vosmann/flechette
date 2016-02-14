package com.vosmann.flechette;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@org.springframework.stereotype.Controller
@EnableAutoConfiguration
public class Controller {

    @RequestMapping("/")
    @ResponseBody
    String root() {
        return "NOP service is running.";
    }

    @RequestMapping("/metrics")
    @ResponseBody
    Metrics metrics() {
        return new Metrics();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Controller.class, args);
    }


    public static class Metrics {
        private String system;
        private String app;
    }
// TODO Measure:
// https://support.hyperic.com/display/SIGAR/Home
/*
{
    "system": {
        "cpu": 0.1,
        "memory/heap": 0.1,
        "uptime": 20,
        "tcpConnections": 23,
        "udpConnections": 23
    },
    "app": {
        "requestRate": 500,
        "requestCount": 1234,
        "responseTime": 0.1
    }
}
*/

}
