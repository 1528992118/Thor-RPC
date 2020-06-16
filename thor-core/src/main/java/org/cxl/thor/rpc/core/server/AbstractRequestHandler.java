package org.cxl.thor.rpc.core.server;

import org.cxl.thor.rpc.common.Request;
import org.cxl.thor.rpc.common.Response;
import org.cxl.thor.rpc.register.Registry;
import org.cxl.thor.rpc.serialize.Serializer;

public abstract class AbstractRequestHandler {

    private final Registry registry;

    private final Serializer serializer;

    public AbstractRequestHandler(Registry registry, Serializer serializer) {
        this.registry = registry;
        this.serializer = serializer;
    }

    public abstract Response handlerRequest(Request request);

    public Registry getRegistry() {
        return registry;
    }

    public Serializer getSerializer() {
        return serializer;
    }
}
