package org.cxl.thor.rpc.serialize;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 序列化接口
 */
public interface Serializer {

    String protocol();

    Object serialize(Object object);

    Object deserialize(byte[] data);

}
