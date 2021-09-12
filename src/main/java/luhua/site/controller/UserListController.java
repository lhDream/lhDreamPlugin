package luhua.site.controller;

import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import luhua.site.Application;
import luhua.site.httpServer.HttpControllerBase;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 获取用户列表
 */
public class UserListController implements HttpControllerBase {

    @Override
    public DefaultFullHttpResponse httpRequest(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse resp, Map<String, List<String>> param) {

        if(req.method() == HttpMethod.POST){
            String token = param.get("token").get(0);
            String username = param.get("username").get(0);
            String localToken = LoginController.tokens.get(username);
            if(localToken != null && localToken.equals(token)){
                JSONArray jsonArray = new JSONArray();
                @NotNull Collection<? extends Player> players = Application.getApplication().getServer().getOnlinePlayers();
                players.forEach(e->{
                    JsonObject player = new JsonObject();
                    player.addProperty("name",e.getName());
                    player.addProperty("state",1);
                    player.addProperty("uuid",e.getUniqueId().toString());

                    jsonArray.add(player);
                });

                @NotNull OfflinePlayer[] offlinePlayers = Application.getApplication().getServer().getOfflinePlayers();
                for(OfflinePlayer e:offlinePlayers){
                    JsonObject player = new JsonObject();
                    player.addProperty("name",e.getName());
                    player.addProperty("state",0);
                    player.addProperty("uuid",e.getUniqueId().toString());
                    jsonArray.add(player);
                }
                JsonObject json = new JsonObject();
                json.addProperty("res","success");
                json.addProperty("data",jsonArray.toJSONString());

                DefaultFullHttpResponse defaultFullHttpResponse =
                        new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                HttpResponseStatus.OK,
                                Unpooled.copiedBuffer(json.toString(), CharsetUtil.UTF_8));
                return defaultFullHttpResponse;
            }
        }
        return null;
    }


}
