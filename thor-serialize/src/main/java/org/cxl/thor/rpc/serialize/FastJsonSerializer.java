package org.cxl.thor.rpc.serialize;

import org.cxl.thor.rpc.common.constant.CommonConstants;


public class FastJsonSerializer implements Serializer {

    public String protocol() {
        return CommonConstants.F_JSON_SERIALIZATION;
    }

    public Object serialize(Object object)  {
        return null;
    }
    
    public Object deserialize(byte[] data) {
        return null;
    }

}
