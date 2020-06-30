package org.cxl.thor.rpc.core.client;

import org.cxl.thor.rpc.common.Request;
import org.cxl.thor.rpc.common.Response;
import org.cxl.thor.rpc.common.URL;

/**
 * @author cxl
 * @date 2020/6/8 20:00
 */
public interface NetClient {

    Response send(Request request, URL url) throws Throwable;

}
