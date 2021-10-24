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

import java.io.File;
import java.util.Collection;
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
        String name = param.get("name").get(0);
        log.info("删除用户,uuid: {}, name:{}" ,uuid,name);
        delUser(uuid,name);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("res","success");
        jsonObject.addProperty("desc","删除成功");
        DefaultFullHttpResponse defaultFullHttpResponse =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(jsonObject.toString(), CharsetUtil.UTF_8));
        return defaultFullHttpResponse;
    }

    /**
     * 删除用户所有数据
     * @param uuid
     */
    public void delUser(String... uuid){
        //主世界文件夹名称
        String worldName = Application.getApplication().getServer().getWorlds().get(0).getName();
        File file = new File(worldName+"/playerdata");
        if(null != file){
            File[] files = file.listFiles();
            for(File f:files){
                if(null != f && f.getName().indexOf(uuid[0])> -1){
                    //删除用户数据
                    f.delete();
                }
            }
        }
        file = new File(worldName+"/advancements");
        if(null != file){
            File[] files = file.listFiles();
            for(File f:files){
                if(null != f && f.getName().indexOf(uuid[0])> -1){
                    //删除用户数据
                    f.delete();
                }
            }
        }
    }


}
