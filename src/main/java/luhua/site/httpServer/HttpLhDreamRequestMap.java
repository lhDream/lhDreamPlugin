package luhua.site.httpServer;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: http 接口库
 * @author: luhua
 * @create: 2021-09-10 17:27
 **/
public class HttpLhDreamRequestMap {

    private static final Map<String,HttpControllerBase> map = new HashMap<>();

    /**
     * 注册一个新的http服务
     * @param path
     * @param httpController
     * @return
     */
    public HttpControllerBase put(String path,HttpControllerBase httpController){
        return map.put(path,httpController);
    }

    /**
     * 获取http请求列表
     * @return
     */
    static Map<String, HttpControllerBase> getMap() {
        return map;
    }

    /**
     * 获取http服务
     * @param path
     * @return
     */
    static HttpControllerBase getHttpController(String path){
        return map.get(path);
    }

}
