package luhua.site.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;

/**
 * 未知指令监听器
 */
public class UnknownCommandListener implements Listener {

    /**
     *
     * @param UnknownCommandEvent
     */
    @EventHandler
    public void UnknownCommand(UnknownCommandEvent UnknownCommandEvent){
        UnknownCommandEvent.getSender().sendMessage("指令输入错误,不会使用指令的话，尝试输入/help来获取帮助!");
    }

}
