package luhua.site.protocol;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 指令处理
 * @author: lhDream
 * @create: 2021-09-08 15:04
 **/
public class Action implements CommandExecutor {

    private Logger log = LoggerFactory.getLogger(Action.class);
    private final String COMM = "hello";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!COMM.equals(label)){
            return false;
        }
        log.error("hello world");
        return true;
    }
}
