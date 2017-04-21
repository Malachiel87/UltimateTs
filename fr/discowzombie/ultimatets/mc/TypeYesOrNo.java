package fr.discowzombie.ultimatets.mc;

import fr.discowzombie.ultimatets.*;
import fr.discowzombie.ultimatets.functions.UtilsFunctions;
import fr.discowzombie.ultimatets.sql.SqlRequest;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TypeYesOrNo implements Listener {
	
	private SqlRequest sql;
	   
	public TypeYesOrNo(SqlRequest sql) {
        this.sql = sql;
    }
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if(UltimateTSCmd.confirmationReady.contains(p)){
			String message = e.getMessage();
			if(message.equalsIgnoreCase("YES")){
				//UNLINK
				int dbclientId = 0;
				if(sql.useDataBase()){
					dbclientId = sql.getLinkedId(p.getUniqueId().toString());
				}else{
					dbclientId = UltimateTS.g().getConfig().getInt("linked."+p.getUniqueId().toString());
				}
				UtilsFunctions.unlink(p, dbclientId);
				p.sendMessage(UltimateTS.g().getConfig().getString("messages.unlinked.confirmation.yes").replace('&', '§'));
			}else{
				p.sendMessage(UltimateTS.g().getConfig().getString("messages.unlinked.confirmation.no").replace('&', '§')); 
			} 
			UltimateTSCmd.confirmationReady.remove(p);
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(UltimateTSCmd.confirmationReady.contains(e.getPlayer())) UltimateTSCmd.confirmationReady.remove(e.getPlayer()); 
	}

}
