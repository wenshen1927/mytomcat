package com.go.diytomcat;

import com.go.diytomcat.catalina.Server;

public class Bootstrap {
    public static void main(String[] args) {
        // 启动服务，加载应用
        Server server = new Server();
        server.start();
    }
}
