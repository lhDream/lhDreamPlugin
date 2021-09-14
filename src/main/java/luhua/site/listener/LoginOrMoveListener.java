package luhua.site.listener;

import luhua.site.Application;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.slf4j.Logger;

/**
 * 用户登录、离线事件监听
 */
public class LoginOrMoveListener implements Listener {

    private Logger log ;

    public LoginOrMoveListener(){
        Application.getLog();
    }

    /**
     *  玩家上线
     * @param event
     */
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if(null != player){
            log.info("玩家 {} 上线了",player.getName());
        }

    }

    /**
     * 玩家离线
     * @param event
     */
    @EventHandler
    public void onLogin(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(null != player){
            log.info("玩家 {} 离线了",player.getName());
        }
    }


}
