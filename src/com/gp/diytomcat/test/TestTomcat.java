package com.gp.diytomcat.test;

import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.go.diytomcat.util.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTomcat extends BaseTest {
    @Test
    public void testHelloTomcat() {
        String html = getContentString("/");
        System.out.println(html);
        Assert.assertEquals(html,"Hello DIY Tomcat from how2j.cn");
    }

    @Test
    public void testaHtml() {
        String html = getContentString("/a.html");
        Assert.assertEquals(html,"Hello DIY Tomcat from a.html");
    }


}
