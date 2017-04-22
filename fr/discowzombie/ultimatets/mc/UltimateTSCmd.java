package fr.discowzombie.ultimatets.mc;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.discowzombie.ultimatets.UltimateTS;
import fr.discowzombie.ultimatets.functions.UtilsFunctions;
import fr.discowzombie.ultimatets.sql.SqlRequest;
import fr.discowzombie.ultimatets.ts.BotManager;

public class UltimateTSCmd implements CommandExecutor {
	
	public static HashMap<String, Player> players = new HashMap<>();
	public static ArrayList<Player> confirmationReady = new ArrayList<>();
	
	private SqlRequest sql;
	   
	public UltimateTSCmd(SqlRequest sql) {
        this.sql = sql;
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			
			if((args.length >= 1) && (args[0] != null)){
				
				if(args[0].equalsIgnoreCase("broadcast")){	
					if(p.hasPermission("ultimatets.broadcast")){	
						if(args.length == 1){
							p.sendMessage(UltimateTS.g().getConfig().getString("messages.broadcast.empty").replace('&', '§'));
						}else{
							StringBuilder sb = new StringBuilder();
							for(int i = 1; i < args.length; i++){
								sb.append(args[i]).append(" ");
							} 
							String message = sb.toString().trim();
							p.sendMessage(UltimateTS.g().getConfig().getString("messages.broadcast.to.sender").replace('&', '§').replace("%message%", message));
							BotManager.getBot().sendServerMessage(UltimateTS.g().getConfig().getString("messages.broadcast.to.teamspeak").replace("%message%", message));
							Bukkit.broadcastMessage(UltimateTS.g().getConfig().getString("messages.broadcast.to.minecraft").replace('&', '§').replace("%message%", message));
						}
					}else{
						displayHelp(p);
					}
				}else if(args[0].equalsIgnoreCase("link")){
					if(UtilsFunctions.isLinked(p)){
						p.sendMessage(UltimateTS.g().getConfig().getString("messages.linked.already").replace('&', '§'));
					}else{
						//LINK
						String ipA = p.getAddress().toString();
						int port = p.getAddress().getPort();
						String ip = ipA.replaceFirst(":", "").replaceFirst("/", "").replace(""+port, "");
						if(players.containsKey(ip)){
							players.remove(ip);
						}
						players.put(ip, p);
						p.sendMessage(UltimateTS.g().getConfig().getString("messages.linked.ready").replace('&', '§'));
					}
				}else if(args[0].equalsIgnoreCase("unlink")){
					if(UtilsFunctions.isLinked(p)){
						//WRITE YES OR NO INTO CHAT (WITHOUT)
						confirmationReady.add(p);
						p.sendMessage(UltimateTS.g().getConfig().getString("messages.unlinked.confirmation.confirmation").replace('&', '§')); 
					}else{
						p.sendMessage(UltimateTS.g().getConfig().getString("messages.unlinked.confirmation.already").replace('&', '§')); 
					}
				}else if(args[0].equalsIgnoreCase("status")){
					if(UtilsFunctions.isLinked(p)){
						p.sendMessage(UltimateTS.g().getConfig().getString("messages.linked.linked").replace('&', '§'));
						int dbId = 0;
						if(sql != null && sql.useDataBase()){
							dbId = sql.getLinkedId(p.getUniqueId().toString());
						}else{
							dbId = UltimateTS.g().getConfig().getInt("linked."+p.getUniqueId().toString());
						}
						p.sendMessage(UltimateTS.g().getConfig().getString("messages.linked.info").replace('&', '§').replace("%dbid%", dbId+""));
					}else{
						p.sendMessage(UltimateTS.g().getConfig().getString("messages.unlinked.unlinked").replace('&', '§'));
					}
				}else if(args[0].equalsIgnoreCase("list")){
					if(p.hasPermission("ultimatets.list")){
						if(args.length == 1){
							p.sendMessage(UltimateTS.g().getConfig().getString("messages.list.empty").replace('&', '§'));
						}else if((args.length == 2) && (args[1].equalsIgnoreCase("TS"))){
							UtilsFunctions.getAndBroadcastTSClients(p);
						}else if((args.length == 2) && (args[1].equalsIgnoreCase("IG"))){
							UtilsFunctions.getAndBroadcastIGClients(p);
						}
					}else{
						displayHelp(p);
					}
				}else{
					displayHelp(p);
				}	
			}else{
				displayHelp(p);
			}
		}
		return true;
	}
	
	
	public void displayHelp(Player p){
		p.sendMessage("§6-------- [ UltimateTS ] --------");
		p.sendMessage("");
		p.sendMessage("* §e/ts status§7: §rGet your account linked or not.");
		p.sendMessage("* §e/ts link§7: §rLink your accounts.");
		p.sendMessage("* §e/ts unlink§7: §rUnlink your accounts.");
		if(p.hasPermission("ultimatets.broadcast")) p.sendMessage("* §e/ts broadcast <message>§7: §rBroadcast a message on TeamSpeak.");
		if(p.hasPermission("ultimatets.list")) p.sendMessage("* §e/ts list <TS|IG>§7: §rDisplay list of players");
	}

}
