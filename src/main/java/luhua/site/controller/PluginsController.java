package luhua.site.controller;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import luhua.site.Application;
import luhua.site.httpServer.HttpControllerBase;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

/**
 * @description: 插件控制
 * @author: luhua
 * @create: 2021-09-10 17:40
 **/
public class PluginsController implements HttpControllerBase {

    @Override
    public void httpRequest(ChannelHandlerContext ctx, FullHttpRequest msg) {
        byte[] b;
        StringBuilder sb = new StringBuilder();
        @NotNull Plugin[] plugins = Application.getApplication().getServer().getPluginManager().getPlugins();
        for(Plugin p:plugins){
            sb.append(p.getName()).append("\n");
        }
        b = sb.toString().getBytes(StandardCharsets.UTF_8);
        DefaultFullHttpResponse defaultFullHttpResponse =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(b,0,b.length));
        ctx.writeAndFlush(defaultFullHttpResponse);
        ctx.close();
    }

}
