package luhua.site.httpServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 *
 * @description: http控制入口
 * @author: lhDream
 *
 */
public interface HttpControllerBase {

    DefaultFullHttpResponse httpRequest(ChannelHandlerContext ctx, FullHttpRequest msg);

}
