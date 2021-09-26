package com.kong.rpc.service;

import com.kong.rpc.annotation.RpcService;

@RpcService
public class DemoService {
    public String testAnnotion(){
        return "this is a rpc provider service";
    }
}
