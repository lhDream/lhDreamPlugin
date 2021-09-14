package luhua.site.controller;

import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import luhua.site.Application;
import luhua.site.httpServer.HttpControllerBase;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * @description: 玩家相关控制类
 * @author: lhDream
 * @create: 2021-09-13 19:14
 **/
public class PlayerController implements HttpControllerBase {

    private Logger log ;

    public PlayerController(){
        this.log = Application.getLog();
    }


    @Override
    public DefaultFullHttpResponse httpRequest(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse resp, Map<String, List<String>> param) {

        DefaultFullHttpResponse response = LoginController.tokenVify(param);
        if( null != response){
            //token校验失败
            return response;
        }
        String uuid = param.get("uuid").get(0);
        log.info("删除用户:{}" ,uuid);

        Player player = Application.getApplication().getServer().getPlayer(uuid);
        if(null != player){
            player.remove();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("res","error");
        jsonObject.addProperty("error","NOT_FIND_TOKEN");
        DefaultFullHttpResponse defaultFullHttpResponse =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(jsonObject.toString(), CharsetUtil.UTF_8));
        return defaultFullHttpResponse;
    }
}
