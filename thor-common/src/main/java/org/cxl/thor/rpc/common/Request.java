package org.cxl.thor.rpc.common;

import java.io.Serializable;

public class Request implements Serializable {

    private static final long serialVersionUID = -4761277732146887645L;

    private String requestId;

    private String serviceName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameterValues;

    public Request(){
    }

    private Request(Builder builder) {
        setRequestId(builder.requestId);
        setServiceName(builder.serviceName);
        setMethodName(builder.methodName);
        setParameterTypes(builder.parameterTypes);
        setParameterValues(builder.parameterValues);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(Object[] parameterValues) {
        this.parameterValues = parameterValues;
    }

    public static final class Builder {
        private String requestId;
        private String serviceName;
        private String methodName;
        private Class<?>[] parameterTypes;
        private Object[] parameterValues;

        private Builder() {
        }

        public Builder requestId(String val) {
            requestId = val;
            return this;
        }

        public Builder serviceName(String val) {
            serviceName = val;
            return this;
        }

        public Builder methodName(String val) {
            methodName = val;
            return this;
        }

        public Builder parameterTypes(Class<?>[] val) {
            parameterTypes = val;
            return this;
        }

        public Builder parameterValues(Object[] val) {
            parameterValues = val;
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }

}
