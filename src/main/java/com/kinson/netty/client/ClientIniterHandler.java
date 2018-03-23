package com.kinson.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * descripiton: 客户端处理初始化
 *
 * @author: www.iknowba.cn
 * @date: 2018/3/23
 * @time: 16:55
 * @modifier:
 * @since:
 */
public class ClientIniterHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //注册管道
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("stringD", new StringDecoder());
        pipeline.addLast("stringC", new StringEncoder());
        pipeline.addLast("http", new HttpClientCodec());
        pipeline.addLast("chat", new ClientHandler());
    }
}
