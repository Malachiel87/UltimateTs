package ultimatets.teamspeak;

import java.util.Collections;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import ultimatets.UltimateTs;
import ultimatets.minecraft.PlayerManager;
import ultimatets.utils.enums.LogEnum;

public class BotManager {
	
	public static boolean online = false;
	public static TS3Api api;
	final TS3Config config = new TS3Config();
	final TS3Query query = new TS3Query(config);
	
	public UltimateTs m(){
		return UltimateTs.main();
	}
	
	public void runBot(String ip, String user, String mdp) {
		config.setHost(ip);
		config.setDebugLevel(Level.ALL);
		try{
			query.connect();
		}catch(Exception e){
			m().log(LogEnum.TeamSpeak, Level.SEVERE, "Error when trying to connecting to TeamSpeak !\n"+e);
			Bukkit.getServer().getPluginManager().disablePlugin(m());
			return;
		}
		m().log(LogEnum.TeamSpeak, Level.INFO, UltimateTs.messages.getString("messages.bot.online"));
		online = true;

		api = query.getApi();
		try{
			api.login(user, mdp);
		}catch(Exception e){
			m().log(LogEnum.TeamSpeak, Level.SEVERE, "Error when trying to login to TeamSpeak !\n"+e);
			Bukkit.getServer().getPluginManager().disablePlugin(m());
			return;
		}
		try{
			api.selectVirtualServerByPort(m().getConfig().getInt("login.port"));
		}catch(Exception e){
			m().log(LogEnum.TeamSpeak, Level.SEVERE, "Error when trying to selecting TeamSpeak virtual server !\n"+e);
			Bukkit.getServer().getPluginManager().disablePlugin(m());
			return;
		}
		api.setNickname(m().getConfig().getString("bot.name"));

		final int clientId = api.whoAmI().getId();
		
		api.registerAllEvents();
		api.addTS3Listeners(new TS3EventAdapter() {

			@Override
			public void onTextMessage(TextMessageEvent e) {
				if((e.getInvokerId() != clientId) && (e.getTargetMode() == TextMessageTargetMode.CHANNEL)) {
					String message = e.getMessage().toLowerCase();
					if(message.equalsIgnoreCase("$ping")){
						api.sendChannelMessage("pong!");
					}else if(message.equalsIgnoreCase("$info")){
						api.sendChannelMessage("This server use "+m().n+" v"+m().v+" created by "+m().a+".");
					}
				}
			}
			
			@Override
			public void onClientJoin(ClientJoinEvent e) {
				int clientId = e.getClientId();
				ClientInfo client = api.getClientInfo(clientId);
				int dbId = e.getClientDatabaseId();
				String ip = client.getIp();
				
				if(PlayerManager.linkedWaiting.containsKey(ip)){
					Player whoLink = PlayerManager.linkedWaiting.get(ip);
					api.editClient(clientId, Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, UltimateTs.messages.getString("messages.linked.desc").replace("%mcname%", whoLink.getName())));
					api.pokeClient(clientId, UltimateTs.messages.getString("messages.linked.sucess").replace("%mcname%", whoLink.getName()));
					PlayerManager.link(whoLink, dbId);
					PlayerManager.linkedWaiting.remove(ip, whoLink);
				}else if(UltimateTs.main().getConfig().getBoolean("config.kickNotRegister") == true){
					PlayerManager.kickNotRegister(client, clientId);
				}
			}
				
		});
	}
	
	public void stopBot(){
		if(!online) return;
		try{
			query.exit();
			UltimateTs.main().log(LogEnum.TeamSpeak, Level.INFO, UltimateTs.messages.getString("messages.bot.offline"));
		}catch(Exception e){}
		online = false;
	}
	
	public static TS3Api getBot(){
		return api;
	}

}
