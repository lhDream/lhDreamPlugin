package luhua.site.httpServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @description: HTTP服务
 * @author: lhDream
 * @create: 2021-09-09 09:44
 **/
public class NettyServerHttp extends Thread{

    /**
     * http服务端口
     */
    private int port = -1;
    /**
     * http缓存最大值
     */
    private static final int MAX_LEN = 20*1024*1024;

    /**
     * @description: HTTP服务初始化
     * @author: lhDream
     * @create: 2021-09-09 09:44
     * @param port http服务端口
     **/
    public NettyServerHttp(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        bind(port);
    }

    /**
     * @description: HTTP服务绑定端口，并启动
     * @author: lhDream
     * @create: 2021-09-09 09:44
     * @param port http服务端口
     **/
    private void bind(int port) {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup();

        ServerBootstrap bootstrap =  new ServerBootstrap();
        try {
            bootstrap.group(boss, work)
                    //注册factory
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new HttpRequestDecoder())
                                    .addLast(new HttpObjectAggregator(MAX_LEN))
                                    .addLast(new HttpServerHandler())
                                    .addLast(new HttpResponseEncoder());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //TCP无掩饰
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //清除死连接，维持活跃的
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture future;
            future = bootstrap.bind("0.0.0.0",port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
