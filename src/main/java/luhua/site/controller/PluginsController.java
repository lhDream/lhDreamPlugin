package luhua.site.controller;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import luhua.site.Application;
import luhua.site.httpServer.HttpControllerBase;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @description: 插件控制
 * @author: luhua
 * @create: 2021-09-10 17:40
 **/
public class PluginsController implements HttpControllerBase {

    private Logger log = Application.getLog();

    @Override
    public DefaultFullHttpResponse httpRequest(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse resp, Map<String, List<String>> param) {
        byte[] b;
        StringBuilder sb = new StringBuilder();
        @NotNull Plugin[] plugins = Application.getApplication().getServer().getPluginManager().getPlugins();
        sb.append("<div>服务器插件列表：</div>");
        for(Plugin p:plugins){
            sb.append("<div>").append(p.getName()).append("</div>");
            log.info("配置:{}",p.getConfig().saveToString());
        }


        b = sb.toString().getBytes(StandardCharsets.UTF_8);
        DefaultFullHttpResponse defaultFullHttpResponse =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(b,0,b.length));
        return defaultFullHttpResponse;
    }
}
