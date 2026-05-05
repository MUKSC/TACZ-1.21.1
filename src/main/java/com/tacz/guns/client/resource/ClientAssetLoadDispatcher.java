package com.tacz.guns.client.resource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 资源后台预热器
public final class ClientAssetLoadDispatcher {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "tacz-client-asset-preload");
        thread.setDaemon(true);
        return thread;
    });

    private ClientAssetLoadDispatcher() {
    }

    public static ExecutorService executor() {
        return EXECUTOR;
    }
}
