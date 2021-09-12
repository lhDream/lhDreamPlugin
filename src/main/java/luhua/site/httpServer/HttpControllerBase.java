package luhua.site.httpServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;
import java.util.Map;

/**
 *
 * @description: http控制入口
 * @author: lhDream
 *
 */
public interface HttpControllerBase {

    /**
     * http 请求
     * @param ctx
     * @param req
     * @param resp
     * @param param
     * @return
     */
    DefaultFullHttpResponse httpRequest(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse resp, Map<String, List<String>> param);

}
