package fr.discowzombie.ultimatets.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsPlugin;
import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.DatabaseClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import fr.discowzombie.ultimatets.UltimateTS;
import fr.discowzombie.ultimatets.ts.BotManager;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.PermissionData;
import me.lucko.luckperms.api.caching.UserData;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import net.milkbowl.vault.Vault;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class UtilsFunctions {
	
	public static void log(Level lvl, String message){
		UltimateTS.g().getLogger().log(lvl, message);
	}
	
	public static JavaPlugin getPermissionSystem(boolean displayInConsole){
		
		String permsS = UltimateTS.g().getConfig().getString("config.permsSystem");
		if((permsS != null) && (permsS.equalsIgnoreCase("PEX"))){
			if(displayInConsole) log(Level.INFO, "Checking for PermissionsEx...");
			try{
				boolean pexS = Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx");
				if(pexS){
					if(displayInConsole) log(Level.INFO, "PermissionsEx found !");
					return (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
				}else{
					if(displayInConsole) log(Level.SEVERE, "PermissionsEx not found !");
				}
			}catch(Exception e){}
			
		}else if(permsS != null && permsS.equalsIgnoreCase("zPerms")){
			if(displayInConsole) log(Level.INFO, "Checking for zPermissions...");
			try{
				boolean zPerms = Bukkit.getServer().getPluginManager().isPluginEnabled("zPermissions");
				if(zPerms){
					if(displayInConsole) log(Level.INFO, "zPermissions found !");
					return (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("zPermissions");
				}else{
					if(displayInConsole) log(Level.SEVERE, "zPermissions not found !");
				}
			}catch(Exception e){}
			
		}else if(permsS != null && permsS.equalsIgnoreCase("LuckPerms")){
			if(displayInConsole) log(Level.INFO, "Checking for LuckPerms...");
			try{
				boolean lperms = Bukkit.getServer().getPluginManager().isPluginEnabled("LuckPerms");
				if(lperms){
					if(displayInConsole) log(Level.INFO, "LuckPerms found !");
					return (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("LuckPerms");
				}else{
					if(displayInConsole) log(Level.SEVERE, "LuckPerms not found !");
				}
			}catch(Exception e){}
			
		}else if(permsS != null && permsS.equalsIgnoreCase("Vault")){
			if(displayInConsole) log(Level.INFO, "Checking for Vault...");
			try{
				boolean vault = Bukkit.getServer().getPluginManager().isPluginEnabled("Vault");
				if(vault){
					if(displayInConsole) log(Level.INFO, "Vault found !");
					return (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("Vault");
				}else{
					if(displayInConsole) log(Level.SEVERE, "Vault not found !");
				}
			}catch(Exception e){}
			
		}else{
			if(displayInConsole) log(Level.WARNING, "No valid permissions system found !");
			if(displayInConsole) log(Level.WARNING, "All ranks linking functions are disabled.");
		}
		return null;
	}
	
	public static PermissionsEx getPEX(){
		try{
			boolean pexS = Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx");
			if(pexS){
				return (PermissionsEx) Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx");
			}
		}catch(Exception e){}
		return null;
	}
	
	public static ZPermissionsPlugin getzPerms(){
		try{
			boolean zPerms = Bukkit.getServer().getPluginManager().isPluginEnabled("zPermissions");
			if(zPerms){
				return (ZPermissionsPlugin) Bukkit.getServer().getPluginManager().getPlugin("zPermissions");
			}
		}catch(Exception e){}
		return null;
	}
	
	public static LuckPermsPlugin getLuckPerms(){
		try{
			boolean lperms = Bukkit.getServer().getPluginManager().isPluginEnabled("LuckPerms");
			if(lperms){
				return (LuckPermsPlugin) Bukkit.getServer().getPluginManager().getPlugin("LuckPerms");
			}
		}catch(Exception e){}
		return null;
	}
	
	public static Vault getVault(){
		try{
			boolean vault = Bukkit.getServer().getPluginManager().isPluginEnabled("Vault");
			if(vault){
				return (Vault) Bukkit.getServer().getPluginManager().getPlugin("Vault");
			}
		}catch(Exception e){}
		return null;
	}
	
	public static boolean isLinked(Player p){
		if((UltimateTS.g().sql != null) && (UltimateTS.g().sql.useDataBase())){
			if(UltimateTS.g().sql.isUUIDLinked(p.getUniqueId().toString()) == true){
				return true;
			}
		}else{
			ConfigurationSection cs = UltimateTS.g().getConfig().getConfigurationSection("linked");
			if(cs.contains(p.getUniqueId().toString())){
				if(cs.get(p.getUniqueId().toString()) != null){
					return true;
				}
			}	
		}
		return false;
	}
	
	public static void link(Player p, int dbId){
		if((UltimateTS.g().sql != null) && (UltimateTS.g().sql.useDataBase() == true)){
			UltimateTS.g().sql.validLink(p.getUniqueId().toString(), dbId);
		}else{
			ConfigurationSection cs = UltimateTS.g().getConfig().getConfigurationSection("linked");
			cs.set(p.getUniqueId().toString(), dbId);
			UltimateTS.g().saveConfig();
		}
	}
	
	public static void unlink(Player p, int dbId){
		BotManager.getBot().editDatabaseClient(dbId, Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, ""));
		for(ServerGroup sg : BotManager.getBot().getServerGroupsByClientId(dbId)){
			if(sg != null){
				BotManager.getBot().removeClientFromServerGroup(sg.getId(), dbId);
			}
		}
		
		if((UltimateTS.g().sql != null) && (UltimateTS.g().sql.useDataBase() == true)){
			UltimateTS.g().sql.unlink(p.getUniqueId().toString());
		}else{
			ConfigurationSection cs = UltimateTS.g().getConfig().getConfigurationSection("linked");
			cs.set(p.getUniqueId().toString(), null);
			UltimateTS.g().saveConfig();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void asignRanks(int id, Player p){
		if(UtilsFunctions.getPermissionSystem(false) != null){
			Plugin permsSystem = UtilsFunctions.getPermissionSystem(false);
			if(permsSystem == UtilsFunctions.getPEX()){
				for(String pRank : PermissionsEx.getPermissionManager().getUser(p).getGroupsNames()){
					int rankId = UltimateTS.g().getConfig().getInt("config.ranks."+pRank.toString());
					if(rankId > 0){
						BotManager.api.addClientToServerGroup(rankId, id);
					}
				}
				for(List<String> perms : PermissionsEx.getPermissionManager().getUser(p).getAllPermissions().values()){
					for(String str : perms){
						if((UltimateTS.g().getConfig().contains("config.perms."+str)) && (UltimateTS.g().getConfig().get("config.perms."+str.toString()) != null)){
							int tsRangIg = UltimateTS.g().getConfig().getInt("config.perms."+str.toString());
							if(tsRangIg > 0){
								BotManager.api.addClientToServerGroup(tsRangIg, id);
							}
						}
					}
				}
			}else if(permsSystem == UtilsFunctions.getzPerms()){
				for(PermissionAttachmentInfo pai : p.getEffectivePermissions()){
					if(pai.getPermission().startsWith("group.")){
						int rankId = UltimateTS.g().getConfig().getInt("config.perms."+pai.getPermission().toString());
						if(rankId > 0){
							BotManager.api.addClientToServerGroup(rankId, id);
						}
					}else{
						if((UltimateTS.g().getConfig().contains("config.perms."+pai)) && (UltimateTS.g().getConfig().get("config.perms."+pai.toString()) != null)){
							int tsRangIg = UltimateTS.g().getConfig().getInt("config.perms."+pai.toString());
							if(tsRangIg > 0){
								BotManager.api.addClientToServerGroup(tsRangIg, id);
							}
						}
					}
				}
			}else if(permsSystem == UtilsFunctions.getLuckPerms()){
				LuckPermsApi api = LuckPerms.getApi();
				for(Group g : api.getGroups()){
					if(p.hasPermission("group."+g.getName().toString())){
						int rankId = UltimateTS.g().getConfig().getInt("config.perms."+g.getName().toString());
						if(rankId > 0){
							BotManager.api.addClientToServerGroup(rankId, id);
						}
					}
				}
				User user = api.getUserSafe(p.getUniqueId().toString()).orElse(null);
				if (user == null) {
				    return;
				}
				UserData userData = user.getUserDataCache().orElse(null);
				if (userData == null) {
				    return;
				}
				Contexts contexts = api.getContextForUser(user).orElse(null);
				if (contexts == null) {
				    return;
				}
				PermissionData permissionData = userData.getPermissionData(contexts);
				Map<String, Boolean> data = permissionData.getImmutableBacking();
				for(String perms : data.keySet()){
					if((UltimateTS.g().getConfig().contains("config.perms."+perms)) && (UltimateTS.g().getConfig().get("config.perms."+perms.toString()) != null)){
						int tsRangIg = UltimateTS.g().getConfig().getInt("config.perms."+perms.toString());
						if(tsRangIg > 0){
							BotManager.api.addClientToServerGroup(tsRangIg, id);
						}
					}
				}
			}else if(permsSystem == UtilsFunctions.getVault()){
				for(String pRank : UltimateTS.perms.getPlayerGroups(p)){
					int rankId = UltimateTS.g().getConfig().getInt("config.ranks."+pRank.toString());
					if(rankId > 0){
						BotManager.api.addClientToServerGroup(rankId, id);
					}
				}
			}
			
		}
		int asignWR = UltimateTS.g().getConfig().getInt("config.asignWhenregister");
		if(asignWR > 0){
			BotManager.api.addClientToServerGroup(asignWR, id);
		}
	}
	
	public static void getAndBroadcastTSClients(Player p){
		p.sendMessage(UltimateTS.g().getConfig().getString("messages.list.ts").replace('&', '§').replace("%n", BotManager.getBot().getClients().size()+""));
		int qtt = 0;
		String r = "";
		
		HashMap<Integer, String> tsServerGroupsName = new HashMap<>();
		tsServerGroupsName.clear();
		for(ServerGroup sg : BotManager.getBot().getServerGroups()){
			tsServerGroupsName.put(sg.getId(), sg.getName());
		}
		
		for(Client c : BotManager.getBot().getClients()){
			String sq = "";
			if(c.isServerQueryClient()) sq = UltimateTS.g().getConfig().getString("messages.list.broadcast.ts.isQuery");
			
			p.sendMessage(UltimateTS.g().getConfig().getString("messages.list.broadcast.ts.name").replace('&', '§')
					.replace("%name%", c.getNickname())
					.replace("%id%", c.getDatabaseId()+"")+sq.replace('&', '§'));
			for(int sg : c.getServerGroups()){
				if(!r.contains(tsServerGroupsName.get(sg))){
					r = r + "§f" + tsServerGroupsName.get(sg) + "§7, ";
					qtt++;
				}
			}
			p.sendMessage(UltimateTS.g().getConfig().getString("messages.list.broadcast.ts.ranks").replace('&', '§')
					.replace("%qtt%", qtt+"")
					.replace("%next%", r+""));
			
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void getAndBroadcastIGClients(Player p){
		p.sendMessage(UltimateTS.g().getConfig().getString("messages.list.ig").replace('&', '§').replace("%n", BotManager.getBot().getClients().size()+""));
		Plugin permsSystem = UtilsFunctions.getPermissionSystem(false);
		int qtt = 0;
		String r = "";
		ConfigurationSection cs = UltimateTS.g().getConfig().getConfigurationSection("linked");
		for(Player p2 : Bukkit.getServer().getOnlinePlayers()){
			p.sendMessage(UltimateTS.g().getConfig().getString("messages.list.broadcast.ig.name").replace('&', '§')
					.replace("%name%", p2.getName())
					.replace("%uuid%", p2.getUniqueId().toString()));
			
			if((permsSystem != null) && (permsSystem == UtilsFunctions.getPEX())){
				for(String pRank : PermissionsEx.getPermissionManager().getUser(p).getGroupsNames()){
					r = r + "§f" + pRank + "§7, ";
					qtt++;
				}
			}else if((permsSystem != null) && (permsSystem == UtilsFunctions.getzPerms())){
				for(PermissionAttachmentInfo pai : p.getEffectivePermissions()){
					if(pai.getPermission().startsWith("group.")){
						r = r + "§f" + pai.getPermission().replace("group.", "").toString();
						qtt++;
					}
				}
			}
			
			if(permsSystem != null) p.sendMessage(UltimateTS.g().getConfig().getString("messages.list.broadcast.ig.ranks").replace('&', '§')
					.replace("%qtt%", qtt+"")
					.replace("%next%", r+"")); 
			if(UtilsFunctions.isLinked(p2)){
				int linkedWithId = cs.getInt(p2.getUniqueId().toString());
				DatabaseClientInfo client = BotManager.getBot().getDatabaseClientInfo(linkedWithId);
				String name = client.getNickname();
				int id = client.getDatabaseId();
				p.sendMessage(UltimateTS.g().getConfig().getString("messages.list.broadcast.ig.linked.linked").replace('&', '§')
						.replace("%name%", name)
						.replace("%id%", id+""));
				
			}else{
				p.sendMessage(UltimateTS.g().getConfig().getString("messages.list.broadcast.ig.linked.nolinked").replace('&', '§'));
			}
		}
	}
}
