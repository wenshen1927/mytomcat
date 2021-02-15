package com.gp.diytomcat.test;

import org.junit.Assert;
import org.junit.Test;


public class TestMultiApp extends BaseTest{
    @Test
    public void testNewAppIndex() {
        String html = getContentString("/NEW_APP/index.html");
        System.out.println("html:"+html);
        Assert.assertEquals(html,"Hello DIY Tomcat from index.html@new_app");
    }
    @Test
    public void testbIndex() {
        String html = getContentString("/b/index.html");
        System.out.println("html:"+html);
        Assert.assertEquals(html,"Hello DIY Tomcat from index.html@b");
    }
}
