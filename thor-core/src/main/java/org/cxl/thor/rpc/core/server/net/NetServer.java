package org.cxl.thor.rpc.core.server.net;

import org.cxl.thor.rpc.core.server.AbstractRequestHandler;

public abstract class NetServer {

    protected final String address;
    protected final AbstractRequestHandler handler;

    public NetServer(String address, AbstractRequestHandler handler) {
        this.address = address;
        this.handler = handler;
    }

    /**
     * 开启服务
     */
    public abstract void start();

    /**
     * 停止服务
     */
    public abstract void stop();

    public String getAddress() {
        return address;
    }

    public AbstractRequestHandler getHandler() {
        return handler;
    }
}
