package luhua.site.controller;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import luhua.site.Application;
import luhua.site.httpServer.HttpControllerBase;
import luhua.site.util.Config;
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @description: 用户登录
 * @author: luhua
 * @create: 2021-09-11 16:23
 **/
public class LoginController implements HttpControllerBase {

    private Logger log = Application.getLog();
    private final String username;
    private final String password;
    public static final Map<String,String> tokens = new HashMap<>();
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk
    private HttpPostRequestDecoder decoder;

    /**
     * 初始化
     */
    public LoginController(){
        FileConfiguration config = Application.getApplication().getConfig();
        this.username = config.getString(Config.USERNAME);
        this.password = config.getString(Config.PASSWORD);
    }

    /**
     * 用户登录逻辑处理
     * @param ctx
     * @param req
     * @return
     */
    @Override
    public DefaultFullHttpResponse httpRequest(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse resp, Map<String, List<String>> param) {
        final ByteBuf content = req.content();
        try{
            if(req.method() == HttpMethod.POST){
                //处理http port请求
                String username = param.get("username").get(0);
                String password = param.get("password").get(0);
                QueryStringDecoder q = new QueryStringDecoder(content.toString(CharsetUtil.UTF_8));

                Map<String, List<String>> parameters = q.parameters();

                JsonObject json = new JsonObject();
                if(this.username.equals(username) && this.password.equals(password)){
                    json.addProperty("res","success");
                    String token = String.valueOf(System.currentTimeMillis());
                    json.addProperty("token",token);
                    tokens.put(this.username,token);
                }else{
                    json.addProperty("res","error");
                }
                DefaultFullHttpResponse defaultFullHttpResponse =
                        new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                HttpResponseStatus.OK,
                                Unpooled.copiedBuffer(json.toString(), CharsetUtil.UTF_8));
                return defaultFullHttpResponse;
            }else{
                //http get请求不处理
                return null;
            }
        }catch(Exception e){
            log.error("login error,{}",e.toString());
            DefaultFullHttpResponse defaultFullHttpResponse =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.INTERNAL_SERVER_ERROR,
                            Unpooled.copiedBuffer(e.getLocalizedMessage(), CharsetUtil.UTF_8));
            return defaultFullHttpResponse;
        }
    }

    /**
     * token校验，
     * @return null说明校验成功
     */
    public static DefaultFullHttpResponse tokenVify(Map<String, List<String>> param){
        String token = param.get("token").get(0);
        String username = param.get("username").get(0);
        String localToken = tokens.get(username);
        if(localToken != null && localToken.equals(token)){
            //校验成功
            return null;
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
