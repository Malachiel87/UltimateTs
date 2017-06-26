package ultimatets.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import ultimatets.UltimateTs;
import ultimatets.teamspeak.BotManager;

public class TsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			
			if((args.length >= 1) && (args[0] != null)){
				
				if(args[0].equalsIgnoreCase("broadcast")){	
					if(p.hasPermission("ultimatets.broadcast")){	
						if(args.length == 1){
							p.sendMessage(UltimateTs.messages.getString("messages.broadcast.empty").replace('&', '§'));
						}else{
							StringBuilder sb = new StringBuilder();
							for(int i = 1; i < args.length; i++){
								sb.append(args[i]).append(" ");
							} 
							String message = sb.toString().trim();
							p.sendMessage(UltimateTs.messages.getString("messages.broadcast.to.sender").replace('&', '§').replace("%message%", message));
							BotManager.getBot().sendServerMessage(UltimateTs.messages.getString("messages.broadcast.to.teamspeak").replace("%message%", message));
							Bukkit.broadcastMessage(UltimateTs.messages.getString("messages.broadcast.to.minecraft").replace('&', '§').replace("%message%", message));
						}
					}else{
						displayHelp(p);
					}
				}else if(args[0].equalsIgnoreCase("link")){
					if(PlayerManager.isLinked(p)){
						p.sendMessage(UltimateTs.messages.getString("messages.linked.already").replace('&', '§'));
					}else{
						//LINK
						String ipA = p.getAddress().toString();
						int port = p.getAddress().getPort();
						String ip = ipA.replaceFirst(":", "").replaceFirst("/", "").replace(""+port, "");
						if(PlayerManager.linkedWaiting.containsKey(ip)){
							PlayerManager.linkedWaiting.remove(ip);
						}
						PlayerManager.linkedWaiting.put(ip, p);
						p.sendMessage(UltimateTs.messages.getString("messages.linked.ready").replace('&', '§'));
					}
				}else if(args[0].equalsIgnoreCase("unlink")){
					if(PlayerManager.isLinked(p)){
						//WRITE YES OR NO INTO CHAT (WITHOUT)
						PlayerManager.confirmationReady.add(p);
						p.sendMessage(UltimateTs.messages.getString("messages.unlinked.confirmation.confirmation").replace('&', '§')); 
					}else{
						p.sendMessage(UltimateTs.messages.getString("messages.unlinked.already").replace('&', '§')); 
					}
				}else if(args[0].equalsIgnoreCase("status")){
					if(PlayerManager.isLinked(p)){
						p.sendMessage(UltimateTs.messages.getString("messages.linked.linked").replace('&', '§'));
						int dbId = PlayerManager.getLinkedWithDbId(p);
						p.sendMessage(UltimateTs.messages.getString("messages.linked.info").replace('&', '§').replace("%dbid%", dbId+"").replace("%tsname%", PlayerManager.getClientInfosByDatabaseId(dbId).getNickname()));
					}else{
						p.sendMessage(UltimateTs.messages.getString("messages.unlinked.unlinked").replace('&', '§'));
					}
				}else if(args[0].equalsIgnoreCase("ip")){
					p.sendMessage(UltimateTs.messages.getString("messages.ts.message").replace('&', '§').replace("%tsip%", UltimateTs.messages.getString("messages.ts.displayedIP")));
				}else if(args[0].equalsIgnoreCase("list")){
					if(args.length == 1){
						displayOnMinecraft(p);
						displayOnTeamSpeak(p);
					}else if(args.length == 2){
						if(args[1].equalsIgnoreCase("minecraft")){
							displayOnMinecraft(p);
						}else if(args[1].equalsIgnoreCase("teamspeak")){
							displayOnTeamSpeak(p);
						}else{
							displayHelp(p);
						}
					}else{
						displayHelp(p);
					}
				}else if(args[0].equalsIgnoreCase("update")){
					if(args.length == 2){
						if(args[1].equalsIgnoreCase("ALL")){
							p.sendMessage("§cArgument '"+args[1]+"' not working on this version, wait an update...");
						}else{
							Player cible = Bukkit.getServer().getPlayer(args[1]);
							if(PlayerManager.isLinked(cible)){
								p.sendMessage(UltimateTs.messages.getString("messages.update.player").replace('&', '§').replace("%player%", cible.getName()));
								PlayerManager.updateRanks(cible);
							}else{
								p.sendMessage(UltimateTs.messages.getString("messages.update.nolink").replace('&', '§').replace("%player%", cible.getName()));
							}
						}
					}else{
						displayHelp(p);
					}
				}
			}else{
				displayHelp(p);
			}
		}
		return true;
	}
	
	private void displayOnTeamSpeak(Player p) {
		p.sendMessage(UltimateTs.messages.getString("messages.list.teamspeak.size").replace("%n", BotManager.getBot().getClients().size()+"").replace('&', '§'));
		for(Client c : BotManager.getBot().getClients()){
			int cDbid = c.getDatabaseId();
			p.sendMessage(UltimateTs.messages.getString("messages.list.teamspeak.user")
					.replace("%name%", c.getNickname())
					.replace("%dbid%", cDbid+"")
					.replace('&', '§'));
		}
	}

	private void displayOnMinecraft(Player p) {
		p.sendMessage(UltimateTs.messages.getString("messages.list.minecraft.size").replace("%n", Bukkit.getServer().getOnlinePlayers().size()+"").replace('&', '§'));
		for(Player online : Bukkit.getServer().getOnlinePlayers()){
			if(!PlayerManager.isLinked(online)){
				p.sendMessage(UltimateTs.messages.getString("messages.list.minecraft.player")
						.replace("%player%", online.getName())
						.replace("%uuid%", online.getUniqueId().toString())
						.replace('&', '§'));
				p.sendMessage(UltimateTs.messages.getString("messages.list.minecraft.false")
						.replace("%linked_true_false%", PlayerManager.isLinked(online)+"")
						.replace('&', '§'));
			}else{
				ClientInfo ci = PlayerManager.getClientInfosByDatabaseId(PlayerManager.getLinkedWithDbId(p));
				p.sendMessage(UltimateTs.messages.getString("messages.list.minecraft.player")
						.replace("%player%", online.getName())
						.replace("%uuid%", online.getUniqueId().toString())
						.replace('&', '§'));
				p.sendMessage(UltimateTs.messages.getString("messages.list.minecraft.true")
						.replace("%linked_true_false%", PlayerManager.isLinked(online)+"")
						.replace("%linked_name%", ci.getNickname())
						.replace("%dbid%", ci.getDatabaseId()+"")
						.replace('&', '§'));
			}
		}
	}


	public void displayHelp(Player p){
		p.sendMessage("§6-------- [ UltimateTS ] --------");
		p.sendMessage("");
		p.sendMessage(UltimateTs.messages.getString("messages.ts.message").replace('&', '§').replace("%tsip%", UltimateTs.messages.getString("messages.ts.displayedIP")));
		p.sendMessage("");
		p.sendMessage("* §e/ts ip§7: §rDisplay TeamSpeak IP address.");
		p.sendMessage("* §e/ts status§7: §rGet your account linked or not.");
		p.sendMessage("* §e/ts link§7: §rLink your accounts.");
		p.sendMessage("* §e/ts unlink§7: §rUnlink your accounts.");
		if(p.hasPermission("ultimatets.broadcast")) p.sendMessage("* §e/ts broadcast <message>§7: §rBroadcast a message on TeamSpeak.");
		if(p.hasPermission("ultimatets.list.minecraft") || p.hasPermission("ultimatets.list.teamspeak")) p.sendMessage("* §e/ts list (minecraft|teamspeak)§7: §rView all players online on TeamSpeak and Minecraft.");
		if(p.hasPermission("ultimatets.update")) p.sendMessage("* §e/ts update {player|ALL}§7: §rUpdate player servers groups with new permissions.");
	}

}
