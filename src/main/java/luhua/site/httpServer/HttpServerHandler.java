package luhua.site.httpServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @description: http请求处理
 * @author: lhDream
 * @create: 2021-09-09 09:49
 **/
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String INDEX = "/";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String uri = msg.method().name();

        if("".equals(uri) || INDEX.equals(uri)){
            //首页

        }


    }

}
