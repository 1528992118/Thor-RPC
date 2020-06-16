package org.cxl.thor.rpc.config.spring;

import org.cxl.thor.rpc.common.constant.CommonConstants;
import org.cxl.thor.rpc.serialize.HessianSerializer;
import org.cxl.thor.rpc.serialize.JavaSerializer;
import org.cxl.thor.rpc.serialize.Serializer;

import static org.cxl.thor.rpc.common.constant.CommonConstants.HESSIAN_SERIALIZATION;

/**
 * @author cxl
 * @date 2020/6/11 22:07
 */
public class SerializerUtil {

    public static Serializer getSerializer(String protocol) {
        Serializer serializer;
        switch (protocol) {
            case CommonConstants.JAVA_SERIALIZATION:
                serializer = new JavaSerializer();
                break;
            case HESSIAN_SERIALIZATION:
                serializer = new HessianSerializer();
                break;
            default:
                serializer = new JavaSerializer();
                break;
        }
        return serializer;
    }

}
