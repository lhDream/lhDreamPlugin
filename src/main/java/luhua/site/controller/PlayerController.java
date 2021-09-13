package luhua.site.controller;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import luhua.site.httpServer.HttpControllerBase;

import java.util.List;
import java.util.Map;

/**
 * @description: 玩家相关控制类
 * @author: lhDream
 * @create: 2021-09-13 19:14
 **/
public class PlayerController implements HttpControllerBase {


    @Override
    public DefaultFullHttpResponse httpRequest(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse resp, Map<String, List<String>> param) {




        return null;
    }
}
