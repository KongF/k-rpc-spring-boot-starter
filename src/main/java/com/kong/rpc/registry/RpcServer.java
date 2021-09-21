package com.kong.rpc.registry;

import com.kong.rpc.io.handler.RequestHandler;
import org.apache.zookeeper.proto.RequestHeader;

/**
 * server 服务端
 * @author 10285
 */
public abstract class RpcServer {
    protected int port;
    protected String protocol;
    protected RequestHandler requestHandler;

    /**
     * server start
     */
    public abstract void start();

    /**
     * server stop
     */
    public abstract void stop();

    public RpcServer(int port, String protocol, RequestHandler requestHandler) {
        this.port = port;
        this.protocol = protocol;
        this.requestHandler = requestHandler;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }
}
