package luhua.site.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @description: 读取配置文件
 * @author: lhDream
 * @create: 2021-09-09 13:40
 **/
public class ConfigReader extends YamlConfiguration {

    private YamlConfiguration config;

    public YamlConfiguration getConfig(){
        return this.config;
    }

    /**
     *
     * @param plugin
     * @param fileName
     * @throws Exception
     */
    public ConfigReader(Plugin plugin, String fileName) throws Exception {
        this(new File(plugin.getDataFolder(), fileName));
    }

    /**
     *
     * @param file 配置文件
     * @throws Exception
     */
    public ConfigReader(File file) throws Exception {
        this.config = this.getYml(file);
    }

    /**
     * 获取配置
     * @param file 配置文件
     * @return
     * @throws Exception
     */
    private YamlConfiguration getYml(File file) throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        FileInputStream fileinputstream = null;
        try {
            fileinputstream = new FileInputStream(file);
            InputStreamReader str = new InputStreamReader(fileinputstream, Charset.forName("UTF-8"));
            config.load(str);
            str.close();
        } catch (FileNotFoundException var14) {
        } catch (IOException | InvalidConfigurationException var15) {
            var15.printStackTrace();
            throw var15;
        } finally {
            if (fileinputstream != null) {
                try {
                    fileinputstream.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

        return config;
    }

}
