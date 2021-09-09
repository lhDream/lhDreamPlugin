package luhua.site;

import luhua.site.protocol.Action;
import org.bukkit.command.PluginCommand;
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

    public Application(){
        log = getSLF4JLogger();
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
}
