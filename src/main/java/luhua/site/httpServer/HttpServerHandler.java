package luhua.site.httpServer;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import luhua.site.Application;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @description: http请求处理
 * @author: lhDream
 * @create: 2021-09-09 09:49
 **/
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk
    private HttpPostRequestDecoder decoder;

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
     * logo后缀
     */
    private static final String ICO = ".ico";

    public static final String CONNECTION = "Connection";

    public static String KEEP_LIVE = "keeplive";
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
        try{
            String uri = msg.uri();
            DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);

            if(HttpMethod.GET == msg.method()){
                //参数标识
                QueryStringDecoder qsd = new QueryStringDecoder(msg.uri());
                uri = qsd.path();

                //GET请求
                if("".equals(uri) || INDEX.equals(uri)){
                    //首页
                    httpResponse(ctx,"/login.html");
                }else if(null != HttpLhDreamRequestMap.getHttpController(uri)){
                    //获取get请求参数
                    Map<String, List<String>> param = qsd.parameters();
                    defaultResponse(ctx, msg, uri, resp, param);
                }else{
                    //资源文件请求
                    httpResponse(ctx,uri);
                }
            }else if(HttpMethod.POST == msg.method()){
                if(null != HttpLhDreamRequestMap.getHttpController(uri)){
                    //获取post请求参数
                    decoder = new HttpPostRequestDecoder(factory, msg);
                    Map<String,List<String>> map = new HashMap<>();
                    List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();
                    if(null != bodyHttpDatas){
                        final String url = uri;
                        bodyHttpDatas.forEach(e->{
                            if(null == e){
                                return;
                            }
                            if (e.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                                Attribute attribute = (Attribute) e;
                                try {
                                    List<String> strings = map.get(e.getName());
                                    if(strings == null){
                                        strings = new ArrayList<>();
                                        map.put(e.getName(),strings);
                                    }
                                    strings.add(attribute.getValue());
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                    log.error("ip:{},请求时传递了非法参数:{},请求地址:{}",
                                            ctx.channel().remoteAddress().toString(),
                                            bodyHttpDatas.toString(),
                                            url);
                                    log.error(ioException.toString());
                                }
                            }
                        });
                    }
                    defaultResponse(ctx, msg, uri, resp, map);
                }else{
                    DefaultFullHttpResponse defaultFullHttpResponse =
                            new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                    HttpResponseStatus.NOT_FOUND);
                    ctx.writeAndFlush(defaultFullHttpResponse).addListener(ChannelFutureListener.CLOSE);
                    ctx.close();
                }
            }else{
                log.error("ip:{},进行了不支持的请求方式:{},请求地址:{}",
                        ctx.channel().remoteAddress().toString(),
                        msg.method().toString(),
                        uri);
                DefaultFullHttpResponse defaultFullHttpResponse =
                        new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                HttpResponseStatus.INTERNAL_SERVER_ERROR);
                ctx.writeAndFlush(defaultFullHttpResponse).addListener(ChannelFutureListener.CLOSE);
            }

        }catch(Exception e){
            DefaultFullHttpResponse defaultFullHttpResponse =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.INTERNAL_SERVER_ERROR);
            ctx.writeAndFlush(defaultFullHttpResponse).addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 默认http响应
     * @param ctx
     * @param msg
     * @param uri
     * @param resp
     * @param param
     */
    private void defaultResponse(ChannelHandlerContext ctx, FullHttpRequest msg, String uri, DefaultFullHttpResponse resp, Map<String, List<String>> param) {
        //debug日志
//        log.info("param: {}",param);
        DefaultFullHttpResponse defaultFullHttpResponse = HttpLhDreamRequestMap.getHttpController(uri).httpRequest(ctx, msg,resp,param);
        if(null != defaultFullHttpResponse){
            ChannelFuture channelFuture = ctx.writeAndFlush(defaultFullHttpResponse);
            if(!KEEP_LIVE.equals(defaultFullHttpResponse.headers().get(CONNECTION))){
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }else{
            defaultFullHttpResponse =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            ctx.writeAndFlush(defaultFullHttpResponse).addListener(ChannelFutureListener.CLOSE);
            ctx.close();
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
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK);;
            if(filename.indexOf(HTML) > -1){
                Document doc = Jsoup.parse(new String(jarFile), "UTF-8");
                Elements head = doc.getElementsByTag("head");
                head.append(String.format("<base href=\"http://%s\">",serverAddress));
                jarFile = doc.html().getBytes(StandardCharsets.UTF_8);
            }else if(filename.indexOf(ICO) > -1){
                response.headers().set("content-type","image/vnd.microsoft.icon");
            }

            response.content().writeBytes(jarFile);
            response.headers().set("Connection","Close");
            response.headers().set("Content_Length", response.content().readableBytes());
            //允许跨域访问
            response.headers().set( ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            response.headers().set( ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept");
            response.headers().set( ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT,DELETE");

            ChannelFuture channelFuture = ctx.writeAndFlush(response);
            channelFuture.addListener((e)->{
                if(e.isSuccess()){
                    ctx.close();
                }
            });
            //http响应延时处理最小长度 10000字节
            final int HTTP_MIN_SIZE = 10000;
            if(channelFuture.isSuccess() || response.content().readableBytes() < HTTP_MIN_SIZE){
                ctx.close();
            }
        }catch(Exception e){
            log.error("http req err,path: {},ip: {}, error msg: {},",filename,ctx.channel().remoteAddress().toString(),e.getLocalizedMessage());
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
        ins.close();
        return outbursts.toByteArray();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        ctx.close();
    }

}
