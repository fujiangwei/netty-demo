package com.kinson.io.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * descripiton: IO服务端
 *
 * @author: www.iknowba.cn
 * @date: 2018/3/22
 * @time: 22:08
 * @modifier:
 * @since:
 */
public class IOServer {

    /**
     * 日志
     */
    private static final Logger LOGGER = Logger.getLogger(IOServer.class);

    /**
     * 端口号
     */
    private int port;

    public IOServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new IOServer(8888).start();
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[" + this.getClass() + "] start run in port " + port);
        try {
            final Socket socket = serverSocket.accept();
            //获取新客户端连接,阻塞直到连上客户端
            System.out.println("[" + this.getClass().getName() + "] start connect with " + socket.getLocalAddress());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader reader = null;
                    PrintWriter writer = null;
                    try {
                        //用于接收客户端发来的请求
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        //用于发送返回信息
                        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

                        //控制台输入
                        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                        while (true) {
                            //读客户端数据
                            String content = reader.readLine();
                            System.out.println("from client socket msg: " + content);
                            //往客户端发送数据
                            String msg = in.readLine();
                            System.out.println("Server Socket Message:" + msg);
                            if (StringUtils.equalsIgnoreCase(msg, "q")) {
                                break;
                            } else {
                                writer.println(msg);
                                writer.flush();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            reader.close();
                            writer.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            serverSocket.close();
        }
    }
}
