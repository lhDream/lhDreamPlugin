package luhua.site.controller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import luhua.site.httpServer.HttpControllerBase;

/**
 * @description: 用户登录
 * @author: luhua
 * @create: 2021-09-11 16:23
 **/
public class LoginController implements HttpControllerBase {

    @Override
    public DefaultFullHttpResponse httpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {

        final ByteBuf content = msg.content();


        return null;
    }

}
