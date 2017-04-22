package fr.discowzombie.ultimatets.ts;

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
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;

import fr.discowzombie.ultimatets.UltimateTS;
import fr.discowzombie.ultimatets.functions.UtilsFunctions;
import fr.discowzombie.ultimatets.mc.UltimateTSCmd;

public class BotManager {
	
	public UltimateTS m(){
		return UltimateTS.g();
	}
	
	public static TS3Api api;
	final TS3Config config = new TS3Config();
	final TS3Query query = new TS3Query(config);
	public static boolean online = false;
	
	public void runBot(String ip, String user, String mdp) {
		config.setHost(ip);
		config.setDebugLevel(Level.ALL);
		try{
			query.connect();
		}catch(Exception e){
			Bukkit.getServer().getPluginManager().disablePlugin(UltimateTS.g());
			return;
		}
		m().logP("(TeamSpeak)", UltimateTS.g().getConfig().getString("messages.bot.online"));
		online = true;

		api = query.getApi();
		try{
			api.login(user, mdp);
		}catch(Exception e){
			Bukkit.getServer().getPluginManager().disablePlugin(UltimateTS.g());
			return;
		}
		try{
			api.selectVirtualServerById(UltimateTS.g().getConfig().getInt("login.virtualServerId"));
		}catch(Exception e){
			Bukkit.getServer().getPluginManager().disablePlugin(UltimateTS.g());
			return;
		}
		api.setNickname(UltimateTS.g().getConfig().getString("bot.name"));

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
						api.sendChannelMessage("This server use "+UltimateTS.g().getDescription().getName()+" v"+UltimateTS.g().getDescription().getVersion()+" created by DiscowZombie.");
					}
				}
			}
			
			@Override
			public void onClientJoin(ClientJoinEvent e) {
				int clientId = e.getClientId();
				ClientInfo client = api.getClientInfo(clientId);
				int dbId = e.getClientDatabaseId();
				String ip = client.getIp();
				
				if(UltimateTSCmd.players.containsKey(ip)){
					UtilsFunctions.asignRanks(client.getDatabaseId(), UltimateTSCmd.players.get(ip));
					for(Player values : UltimateTSCmd.players.values()){
						api.editClient(clientId, Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, UltimateTS.g().getConfig().getString("messages.linked.desc").replace("%mcname%", values.getName())));
						api.pokeClient(clientId, UltimateTS.g().getConfig().getString("messages.linked.sucess").replace("%mcname%", values.getName()));
						UtilsFunctions.link(values, dbId);
						break;
					}
					UltimateTSCmd.players.remove(ip);
				}else if(UltimateTS.g().getConfig().getBoolean("config.kickNotRegister") == true){
					//has defaut rank
					int lastGroup = 0;
					for(ServerGroup sg : api.getServerGroupsByClient(client)){
						lastGroup = sg.getId();
					}
					int i = UltimateTS.g().getConfig().getInt("config.defaultGroupId");
					if(lastGroup == i){
						String kick = UltimateTS.g().getConfig().getString("config.messages.kick");
						String poke = UltimateTS.g().getConfig().getString("config.messages.poke");
						api.pokeClient(clientId, poke);
						api.kickClientFromServer(kick, client);
					}
				}
			}
		});
	}
	
	public void stopBot(){
		try{
			query.exit();
			System.out.println(UltimateTS.g().getConfig().getString("messages.bot.offline"));
		}catch(Exception e){}
	}
	
	public static TS3Api getBot(){
		return api;
	}

}
