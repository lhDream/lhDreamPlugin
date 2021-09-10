package luhua.site.httpServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 *
 * @description: http控制入口
 * @author: lhDream
 *
 */
public interface HttpControllerBase {

    void httpRequest(ChannelHandlerContext ctx, FullHttpRequest msg);

}
