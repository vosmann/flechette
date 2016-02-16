package com.vosmann.flechette.server;

public class NopService implements StringService {

    @Override
    public String get() {
        return "NOP service is running.";
    }

}
