package com.go.diytomcat.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import com.go.diytomcat.constant.Constant;
import com.go.diytomcat.http.Request;
import com.go.diytomcat.http.Response;
import com.go.diytomcat.util.ThreadPoolUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Server {
    private Service service;
    public Server(){
        this.service = new Service(this);
    }

    public void start(){
        logJVM();
        init();
    }

    private void init() {
        try {
            int port = 18080;
            ServerSocket ss = new ServerSocket(port);

            while(true) {
                Socket s =  ss.accept();
                Runnable r = new Runnable(){
                    @Override
                    public void run() {
                        try {
                            Request request = new Request(s,service);
                            Response response = new Response();
                            String uri = request.getUri();
                            if(null==uri)
                                return;
                            System.out.println("uri:"+uri);

                            Context context = request.getContext();

                            if("/500.html".equals(uri)){
                                throw new Exception("this is a deliberately created exception");
                            }

                            if("/".equals(uri)){
                                String html = "Hello DIY Tomcat from how2j.cn";
                                response.getWriter().println(html);
                            }
                            else{
                                String fileName = StrUtil.removePrefix(uri, "/");
                                File file = FileUtil.file(context.getDocBase(),fileName);
                                if(file.exists()){
                                    String fileContent = FileUtil.readUtf8String(file);
                                    response.getWriter().println(fileContent);

                                    if(fileName.equals("timeConsume.html")){
                                        ThreadUtil.sleep(1000);
                                    }

                                }
                                else{
                                    handle404(s, uri);
                                    return;
                                }
                            }
                            handle200(s, response);
                        } catch (Exception e) {
                            LogFactory.get().error(e);
                            handle500(s,e);
                        }
                        finally{
                            try {
                                if(!s.isClosed())
                                    s.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };

                ThreadPoolUtil.run(r);
            }
        } catch (IOException e) {
            LogFactory.get().error(e);
            e.printStackTrace();
        }
    }

    private static void logJVM() {
        Map<String,String> infos = new LinkedHashMap<>();
        infos.put("Server version", "How2J DiyTomcat/1.0.1");
        infos.put("Server built", "2020-04-08 10:20:22");
        infos.put("Server number", "1.0.1");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version", SystemUtil.get("os.version"));
        infos.put("Architecture", SystemUtil.get("os.arch"));
        infos.put("Java Home", SystemUtil.get("java.home"));
        infos.put("JVM Version", SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));

        Set<String> keys = infos.keySet();
        for (String key : keys) {
            LogFactory.get().info(key+":\t\t" + infos.get(key));
        }
    }

    private static void handle200(Socket s, Response response) throws IOException {
        String contentType = response.getContentType();
        String headText = Constant.response_head_202;
        headText = StrUtil.format(headText, contentType);
        byte[] head = headText.getBytes();

        byte[] body = response.getBody();

        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

        OutputStream os = s.getOutputStream();
        os.write(responseBytes);
    }

    protected void handle404(Socket s, String uri) throws IOException {
        OutputStream os = s.getOutputStream();
        String responseText = StrUtil.format(Constant.textFormat_404, uri, uri);
        responseText = Constant.response_head_404 + responseText;
        byte[] responseByte = responseText.getBytes("utf-8");
        os.write(responseByte);
    }

    protected void handle500(Socket s, Exception e) {
        try {
            OutputStream os = s.getOutputStream();
            StackTraceElement stes[] = e.getStackTrace();
            StringBuffer sb = new StringBuffer();
            sb.append(e.toString());
            sb.append("\r\n");
            for (StackTraceElement ste : stes) {
                sb.append("\t");
                sb.append(ste.toString());
                sb.append("\r\n");
            }

            String msg = e.getMessage();

            if (null != msg && msg.length() > 20)
                msg = msg.substring(0, 19);

            String text = StrUtil.format(Constant.textFormat_500, msg, e.toString(), sb.toString());
            text = Constant.response_head_500 + text;
            byte[] responseBytes = text.getBytes("utf-8");
            os.write(responseBytes);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
