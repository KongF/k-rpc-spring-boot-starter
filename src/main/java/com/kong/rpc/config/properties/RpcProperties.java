package com.kong.rpc.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 10285
 */
@ConfigurationProperties(prefix = "k.rpc")
public class RpcProperties {
    /**
     * address:port
     */
    private String registerAddress = "127.0.0.1:2181";
    /**
     * export port
     */
    private Integer serverPort = 9999;
    private String protocol  = "java";

    private Integer weight = 1;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
