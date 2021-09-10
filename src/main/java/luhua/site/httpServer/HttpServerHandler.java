package luhua.site.httpServer;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import luhua.site.Application;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;


/**
 * @description: http请求处理
 * @author: lhDream
 * @create: 2021-09-09 09:49
 **/
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * http根目录、首页
     */
    private static final String INDEX = "/";
    /**
     * 浏览器请求logo
     */
    private static final String favicon = "/favicon.ico";
    /**
     * html后缀
     */
    private static final String HTML = ".html";
    /**
     * 服务器ip
     */
    private final String serverAddress;
    /**
     * http请求根路径
     */
    public  final String HTTP_ROOT_PATH;
    /**
     * 插件对象
     */
    private final Application plugin;
    /**
     * 日志对象
     */
    private Logger log;

    public HttpServerHandler(){
        plugin = Application.getApplication();
        log = this.plugin.getSLF4JLogger();
        HTTP_ROOT_PATH = plugin.getDataFolder().getPath()+"/html/";
        serverAddress = this.plugin.getConfig().getString("lhDream.httpServer.ip");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String method = msg.method().name();
        String uri = msg.uri();
        if (favicon.equals(uri)) {
            return;
        }

        if(msg.method() == HttpMethod.GET){
            //GET请求
            if("".equals(uri) || INDEX.equals(uri)){
                //首页
                httpResponse(ctx,"/index.html");
            }else if(null != HttpLhDreamRequestMap.getHttpController(uri)){
                HttpLhDreamRequestMap.getHttpController(uri).httpRequest(ctx,msg);
            }else if("/plugins".equals(uri)){

            }else{
                //资源文件请求
                httpResponse(ctx,uri);
            }
        }else if(msg.method() == HttpMethod.POST){
            //POST请求

        }
    }

    /**
     *
     * 获取资源文件并响应
     *
     * @param ctx
     * @param filename
     */
    private void httpResponse(ChannelHandlerContext ctx,String filename){
        httpResponse(ctx,serverAddress,filename);
    }
    /**
     *
     * 获取资源文件并响应
     *
     * @param ctx
     * @param filename
     */
    private void httpResponse(ChannelHandlerContext ctx,String serverAddress,String filename){
        try{
            String url = "html"+filename;
            byte[] jarFile = this.getJarFile(url);
            if(filename.indexOf(HTML) > -1){
                Document doc = Jsoup.parse(new String(jarFile), "UTF-8");
                Elements head = doc.getElementsByTag("head");
                head.append(String.format("<base href=\"http://%s\">",serverAddress));
                jarFile = doc.html().getBytes(StandardCharsets.UTF_8);
            }
            DefaultFullHttpResponse defaultFullHttpResponse =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.OK,
                            Unpooled.copiedBuffer(jarFile,0,jarFile.length));
            ctx.writeAndFlush(defaultFullHttpResponse);
            ctx.close();
        }catch(Exception e){
            log.error(e.getLocalizedMessage());
            DefaultFullHttpResponse defaultFullHttpResponse =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.NOT_FOUND);
            ctx.writeAndFlush(defaultFullHttpResponse);
            ctx.close();
        }
    }

    /**
     * 获取jar包中的文件数据
     * @param url
     * @return
     * @throws Exception
     */
    private byte[] getJarFile(@NotNull String url) throws Exception{
        final InputStream ins = this.plugin.getResource(url);
        ByteArrayOutputStream outbursts = new ByteArrayOutputStream();
        byte[] str_b = new byte[1024];
        int len = -1;
        while ((len=ins.read(str_b)) > 0) {
            outbursts.write(str_b,0,len);
        }
        return outbursts.toByteArray();
    }

}
