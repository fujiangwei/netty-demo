package com.kinson.io.client;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

/**
 * descripiton:
 *
 * @author: www.iknowba.cn
 * @date: 2018/3/22
 * @time: 23:00
 * @modifier:
 * @since:
 */
public class IOClient {

    /**
     * 日志
     */
    private static final Logger LOGGER = Logger.getLogger(IOClient.class);

    /**
     * 连接服务端ip
     */
    private String ip;

    /**
     * 连接服务端端口
     */
    private int port;

    public IOClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new IOClient("127.0.0.1", 8888).connect();
    }

    public void connect() throws IOException {
        //与服务端连接
        Socket socket = new Socket(ip, port);
        System.out.println("[" + this.getClass().getName() + "] connect with " + socket.getLocalAddress());
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

            //控制台输入发送给服务端数据流
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                //发送给服务端数据
                String content = in.readLine();
                System.out.println("Client socket msg: " + content);
                if (StringUtils.equalsIgnoreCase(content, "q")) {
                    in.close();
                    break;
                }
                //将输入流写入
                writer.println(content);
                writer.flush();
                //读服务端的数据
                String msg = reader.readLine();
                System.out.println("form server socket msg:" + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reader.close();
            writer.close();
            socket.close();
        }
    }
}
