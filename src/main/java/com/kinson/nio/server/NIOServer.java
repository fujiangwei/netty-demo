package com.kinson.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * descripiton:
 *
 * @author: www.iknowba.cn
 * @date: 2018/3/23
 * @time: 11:08
 * @modifier:
 * @since:
 */
public class NIOServer {

    private int num;

    private static final int BLOCK = 2048;

    private static final ByteBuffer sendB = ByteBuffer.allocate(BLOCK);

    private static final ByteBuffer receB = ByteBuffer.allocate(BLOCK);

    private Selector selector;

    public NIOServer(int port) throws IOException {
        //开启ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //获取ServerSocket
        ServerSocket serverSocket = serverSocketChannel.socket();
        //绑定ServerSocket提供服务的端口
        serverSocket.bind(new InetSocketAddress(port));
        //开启选择器
        selector = Selector.open();
        //将ServerSocketChannel注册到选择器上
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("NOIServer start run in port " + port);
    }

    /**
     * 监听选择器的数据
     *
     * @throws IOException
     */
    private void listen() throws IOException {
        //循环监听，事件驱动模式
        while (true) {
            //select()阻塞，等待有事件发生时唤醒
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                //处理完后移除该事件
                iterator.remove();
                //处理该事件
                handleKey(selectionKey);

            }
        }
    }

    /**
     * 处理选择器的监听事件
     *
     * @param selectionKey 选择器的监听事件key
     * @throws IOException
     */
    private void handleKey(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        int count = 0;

        //客户端新连接
        if (selectionKey.isAcceptable()) {
            //开启通道连接
            serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            socketChannel = serverSocketChannel.accept();
            //设置为非阻塞
            socketChannel.configureBlocking(false);
            //将通道注册到选择器
            socketChannel.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            //获取读事件通道
            socketChannel = (SocketChannel) selectionKey.channel();
            //清除原先读缓存
            receB.clear();
            //读取通道缓存
            count = socketChannel.read(receB);
            if (count > 0) {
                //解析通道缓存数据
                String receMsg = new String(receB.array(), 0, count);
                System.out.println("receive from client " + receMsg);
                //注册切到写事件
                socketChannel.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (selectionKey.isWritable()) {
            //获取写事件通道
            socketChannel = (SocketChannel) selectionKey.channel();
            //清除发送缓存数据
            sendB.clear();
            String sendMsg = "num " + num++;
            //设置待发送的数据
            sendB.put(sendMsg.getBytes());
            //准备写
            sendB.flip();
            int write = socketChannel.write(sendB);
            System.out.println("send to client " + sendMsg);
            //注册切到读事件
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws Exception {
        new NIOServer(9999).listen();
    }
}
