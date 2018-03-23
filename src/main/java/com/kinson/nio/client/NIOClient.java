package com.kinson.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * descripiton:
 *
 * @author: www.iknowba.cn
 * @date: 2018/3/23
 * @time: 11:53
 * @modifier:
 * @since:
 */
public class NIOClient {

    private static final int BLOCK = 4096;

    private static final ByteBuffer sendB = ByteBuffer.allocate(BLOCK);

    private static final ByteBuffer receB = ByteBuffer.allocate(BLOCK);

    private SocketChannel socketChannel;

    private Selector selector;

    public NIOClient(String ip, int port) throws IOException {
        //开启通道
        socketChannel = SocketChannel.open();
        //设置为非阻塞
        socketChannel.configureBlocking(false);
        //开启选择器
        selector = Selector.open();
        //将通道注册到选择器
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        //连接服务端
        socketChannel.connect(new InetSocketAddress(ip, port));

    }

    /**
     * 连接服务器
     */
    public void connect() throws IOException {
        Set<SelectionKey> selectionKeys;
        Iterator<SelectionKey> iterator;
        SelectionKey selectionKey;
        int index = 0;

        while (true && index < 10) {
            index++;
            selector.select();
            selectionKeys = selector.selectedKeys();
            iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                selectionKey = iterator.next();
                handleKey(selectionKey, index);
            }
            selectionKeys.clear();
        }
        //交互10次后关闭连接
        socketChannel.close();
    }

    /**
     * 处理选择器监听事件
     *
     * @param selectionKey
     */
    public void handleKey(SelectionKey selectionKey, int index) throws IOException {
        SocketChannel client;
        int count = 0;
        // 连接事件
        if (selectionKey.isConnectable()) {
            System.out.println("client connect......");
            client = (SocketChannel) selectionKey.channel();
            if (client.isConnectionPending()) {
                client.finishConnect();
                sendB.clear();
                sendB.put("Hello, Server".getBytes());
                sendB.flip();
                client.write(sendB);
            }
            client.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            // 读事件
            client = (SocketChannel) selectionKey.channel();
            receB.clear();
            count = client.read(receB);
            if (count > 0) {
                String receMsg = new String(receB.array(), 0, count);
                System.out.println("receive from server " + receMsg);
                client.register(selector, SelectionKey.OP_WRITE);
            }
        } else if (selectionKey.isWritable()) {
            // 给客户端注册写事件
            client = (SocketChannel) selectionKey.channel();
            sendB.clear();
            String sendMsg = "index " + index;
            sendB.put(sendMsg.getBytes());
            sendB.flip();
            client.write(sendB);
            System.out.println("send to server " + sendMsg);
            client.register(selector, SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws Exception {
        new NIOClient("127.0.0.1", 9999).connect();
    }
}
