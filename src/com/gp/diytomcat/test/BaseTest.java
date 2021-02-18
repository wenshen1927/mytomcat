package com.gp.diytomcat.test;

import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.go.diytomcat.util.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;

public class BaseTest {
    protected static int port = 18080;
    protected static String ip = "127.0.0.1";
    @BeforeClass
    public static void beforeClass() {
        //所有测试开始前看diy tomcat 是否已经启动了
        if(NetUtil.isUsableLocalPort(port)) {
            System.err.println("请先启动 位于端口: " +port+ " 的diy tomcat，否则无法进行单元测试");
            System.exit(1);
        }
        else {
            System.out.println("检测到 diy tomcat已经启动，开始进行单元测试");
        }
    }

    protected String getContentString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip,port,uri);
        System.out.println("request url:"+url);
        String content = MiniBrowser.getContentString(url);
        return content;
    }

    protected String getHttpString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip,port,uri);
        String http = MiniBrowser.getHttpString(url);
        return http;
    }

    protected void containAssert(String html, String string) {
        boolean match = StrUtil.containsAny(html, string);
        Assert.assertTrue(match);
    }
}
