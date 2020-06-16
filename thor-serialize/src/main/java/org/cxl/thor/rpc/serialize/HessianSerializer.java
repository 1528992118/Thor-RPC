package org.cxl.thor.rpc.serialize;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.cxl.thor.rpc.common.constant.CommonConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements Serializer {

    public String protocol() {
        return CommonConstants.HESSIAN_SERIALIZATION;
    }

    public Object serialize(Object object) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(output);
        try {
            ho.startMessage();
            ho.writeObject(object);
            ho.completeMessage();
            ho.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }

    public Object deserialize(byte[] data) {
        Object result = null;
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(data);
            Hessian2Input hi = new Hessian2Input(input);
            hi.startMessage();
            result = hi.readObject();
            hi.completeMessage();
            hi.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
