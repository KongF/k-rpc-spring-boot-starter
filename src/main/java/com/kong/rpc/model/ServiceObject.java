package com.kong.rpc.model;

public class ServiceObject {

    /**
     * service name
     */
    private String serviceName;

    /**
     * Service Class
     */
    private Class<?> clazz;
    /**
     * Service object
     */
    private Object object;

    public ServiceObject(String serviceName, Class<?> clazz, Object object) {
        this.serviceName = serviceName;
        this.clazz = clazz;
        this.object = object;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
