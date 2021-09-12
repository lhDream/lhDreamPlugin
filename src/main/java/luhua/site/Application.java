package luhua.site;


import luhua.site.controller.LoginController;
import luhua.site.controller.PluginsController;
import luhua.site.controller.UserListController;
import luhua.site.httpServer.HttpLhDreamRequestMap;
import luhua.site.httpServer.NettyServerHttp;
import luhua.site.protocol.Action;
import luhua.site.util.ConfigReader;
import luhua.site.util.Pool;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

/**
 *
 * @description: 插件入口
 * @author: lhDream
 * @create: 2021-9-8 14:27:04
 *
 */
public final class Application extends JavaPlugin {

    private static Logger log;
    private int HTTP_PORT = 80;
    private ConfigReader configReader;
    private YamlConfiguration config;
    private static Application plugn;

    private static boolean HTTP_STATE = true;

    public Application(){
        plugn = this;
        log = getSLF4JLogger();
    }

    public static Application getApplication(){
        return plugn;
    }

    /**
     *
     * @description: 插件启动(初始化)
     * @author: lhDream
     * @create: 2021-9-8 14:27:04
     *
     */
    @Override
    public void onEnable() {
        log.info("lhdream Plugin startup logic");
        log.info("初始化配置");
        FileConfiguration config = this.getConfig();
        this.saveDefaultConfig();
        this.initHttp();

        PluginCommand command = this.getCommand("hello");
        command.setExecutor(new Action());
    }

    /**
     *
     * @description: 插件停止
     * @author: lhDream
     * @create: 2021-9-8 14:27:04
     *
     */
    @Override
    public void onDisable() {
        log.info("lhDream Plugin shutdown logic");
    }

    /**
     *
     * @description: 获取全局日志对象
     * @author: lhDream
     * @create: 2021-9-8 14:27:04
     *
     */
    public static Logger getLog(){
        return log;
    }

    /**
     * 初始化http相关服务
     */
    private void initHttp(){
        if(HTTP_STATE){
            HTTP_STATE = false;
            FileConfiguration config = this.getConfig();
            try {
                HTTP_PORT = config.getInt("lhDream.httpServer.port");
                log.info("start http server , port:{}",HTTP_PORT);
                Pool.getThreadPool().execute(new NettyServerHttp(HTTP_PORT));
                HttpLhDreamRequestMap.put("/plugins",new PluginsController());
                HttpLhDreamRequestMap.put("/login",new LoginController());
                HttpLhDreamRequestMap.put("/userList",new UserListController());
            } catch (Exception e) {
                log.error("获取http port 失败");
                log.error(e.getLocalizedMessage());
            }
        }
    }


}
