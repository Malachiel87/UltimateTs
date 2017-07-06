package ultimatets.minecraft;

import ultimatets.UltimateTs;
import ultimatets.teamspeak.BotManager;

import java.util.Collections;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.theholywaffle.teamspeak3.api.ClientProperty;

public class TypeYesOrNo implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if(PlayerManager.confirmationReady.contains(p)){
			String message = e.getMessage();
			if(message == null) return;
			
			String rep = UltimateTs.main().getConfig().getString("config.yesReponse");
			if(rep == null) rep = "YES";
			if(message.equalsIgnoreCase(rep.toString())){
				BotManager.getBot().editClient(PlayerManager.convertDatabaseIdToClientId(PlayerManager.getLinkedWithDbId(p)), Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, ""));
				p.sendMessage(UltimateTs.messages.getString("messages.unlinked.confirmation.yes").replace('&', '§'));
				PlayerManager.unlink(p);
			}else{
				p.sendMessage(UltimateTs.messages.getString("messages.unlinked.confirmation.no").replace('&', '§')); 
			} 
			PlayerManager.confirmationReady.remove(p);
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(PlayerManager.confirmationReady.contains(p)){
			PlayerManager.confirmationReady.remove(p); 
		}
	}

}
