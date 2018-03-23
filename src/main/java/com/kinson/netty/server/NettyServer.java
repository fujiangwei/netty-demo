package com.kinson.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * descripiton:服务端
 *
 * @author: www.iknowba.cn
 * @date: 2018/3/23
 * @time: 15:37
 * @modifier:
 * @since:
 */
public class NettyServer {
    /**
     * 端口
     */
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void run() {
        //EventLoopGroup是用来处理IO操作的多线程事件循环器
        //负责接收客户端连接线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //负责处理客户端i/o事件、task任务、监听任务组
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //启动 NIO 服务的辅助启动类
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        //配置 Channel
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ServerIniterHandler());
        //BACKLOG用于构造服务端套接字ServerSocket对象，
        // 标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        //是否启用心跳保活机制
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            //绑定服务端口监听
            Channel channel = bootstrap.bind(port).sync().channel();
            System.out.println("server run in port " + port);
            //服务器关闭监听
            /*channel.closeFuture().sync()实际是如何工作:
            channel.closeFuture()不做任何操作，只是简单的返回channel对象中的closeFuture对象，对于每个Channel对象，都会有唯一的一个CloseFuture，用来表示关闭的Future，
            所有执行channel.closeFuture().sync()就是执行的CloseFuturn的sync方法，从上面的解释可以知道，这步是会将当前线程阻塞在CloseFuture上*/
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //关闭事件流组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyServer(8899).run();
    }
}
