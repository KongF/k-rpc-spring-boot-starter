package com.kong.rpc.model;

import java.util.Objects;

public class ServiceResourse {

    /**
     * service name
     */
    private String name;
    /**
     * service protocol
     */
    private String protocol;
    /**
     *  ip address format:  ip:port
     */
    private String address;
    /**
     * service node weight
     */
    private Integer weight;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    @Override
    public String toString() {
        return "serviceURL{" +
                "name='" + name + '\'' +
                ", protocol='" + protocol + '\'' +
                ", address='" + address + '\'' +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceResourse serviceResourse = (ServiceResourse) o;
        return Objects.equals(name, serviceResourse.name) &&
                Objects.equals(protocol, serviceResourse.protocol) &&
                Objects.equals(address, serviceResourse.address) &&
                Objects.equals(weight, serviceResourse.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, protocol, address, weight);
    }
}
