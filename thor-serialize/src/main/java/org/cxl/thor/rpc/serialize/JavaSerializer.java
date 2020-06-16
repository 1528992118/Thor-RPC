package org.cxl.thor.rpc.serialize;

import org.cxl.thor.rpc.common.constant.CommonConstants;

import java.io.*;

public class JavaSerializer implements Serializer {

    public String protocol() {
        return CommonConstants.JAVA_SERIALIZATION;
    }

    public Object serialize(Object object) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(output);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
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
            ObjectInputStream objectInputStream = new ObjectInputStream(input);
            result = objectInputStream.readObject();
            objectInputStream.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



}
