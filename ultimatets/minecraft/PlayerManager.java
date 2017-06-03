package ultimatets.minecraft;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.DatabaseClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;

import ultimatets.UltimateTs;
import ultimatets.teamspeak.BotManager;
import ultimatets.utils.Utils;
import ultimatets.utils.enums.UtilsStorage;

public abstract class PlayerManager {
	
	public static ArrayList<Player> confirmationReady = new ArrayList<>();
	public static HashMap<String, Player> linkedWaiting = new HashMap<>();
	
	public static boolean isLinked(Player p){
		String uuid = p.getUniqueId().toString();
		
		UtilsStorage storageType = Utils.getStorageType();
		if(storageType == UtilsStorage.FILE){
			if(UltimateTs.linkedPlayers.getConfigurationSection("linked") == null) UltimateTs.linkedPlayers.createSection("linked");
			ConfigurationSection cs = UltimateTs.linkedPlayers.getConfigurationSection("linked");
			if((cs.contains(uuid)) && (cs.getInt(uuid) > 0)) return true;
		}else if(storageType == UtilsStorage.SQL){
			if(UltimateTs.main().sql.isUUIDLinked(uuid)){
				int linkedId = UltimateTs.main().sql.getLinkedId(uuid);
				if(linkedId > 0){
					return true;
				}
			}
		}
		return false;
	}
	
	public static void link(Player p, int tsDbId){
		String uuid = p.getUniqueId().toString();
		
		UtilsStorage storageType = Utils.getStorageType();
		if(storageType == UtilsStorage.FILE){
			if(!isLinked(p)){
				ConfigurationSection cs = UltimateTs.linkedPlayers.getConfigurationSection("linked");
				cs.set(uuid, tsDbId);
				UltimateTs.linkedPlayers.save();
			}
		}else if(storageType == UtilsStorage.SQL){
			if(!UltimateTs.main().sql.isUUIDLinked(uuid)){
				UltimateTs.main().sql.validLink(uuid, tsDbId);
			}
		}
		assignRanks(p, tsDbId);
	}
	
	public static int getLinkedWithDbId(Player p){
		UtilsStorage storageType = Utils.getStorageType();
		if(storageType == UtilsStorage.FILE){
			if(isLinked(p)){
				ConfigurationSection cs = UltimateTs.linkedPlayers.getConfigurationSection("linked");
				int dbid = cs.getInt(p.getUniqueId().toString());
				return dbid;
			}
		}else if(storageType == UtilsStorage.SQL){
			if(UltimateTs.main().sql.isUUIDLinked(p.getUniqueId().toString())){
				int dbId = UltimateTs.main().sql.getLinkedId(p.getUniqueId().toString());
				return dbId;
			}
		}
		return 0;
	}
	
	public static void unlink(Player p){
		String uuid = p.getUniqueId().toString();
		
		UtilsStorage storageType = Utils.getStorageType();
		removeRanks(p);
		if(storageType == UtilsStorage.FILE){
			if(isLinked(p)){
				ConfigurationSection cs = UltimateTs.linkedPlayers.getConfigurationSection("linked");
				cs.set(uuid, 0);
				UltimateTs.linkedPlayers.save();
			}
		}else if(storageType == UtilsStorage.SQL){
			if(UltimateTs.main().sql.isUUIDLinked(uuid)){
				UltimateTs.main().sql.unlink(uuid);
			}
		}
	}
	
	public static int convertDatabaseIdToClientId(int tsDbId){
		DatabaseClientInfo dbclient = BotManager.getBot().getDatabaseClientInfo(tsDbId);
		ClientInfo client = BotManager.getBot().getClientByUId(dbclient.getUniqueIdentifier());
		int clientId = client.getId();
		return clientId;
	}
	
	public static ClientInfo getClientInfosByDatabaseId(int tsdbId){
		DatabaseClientInfo dbclient = BotManager.getBot().getDatabaseClientInfo(tsdbId);
		ClientInfo client = BotManager.getBot().getClientByUId(dbclient.getUniqueIdentifier());
		return client;
	}
	
	public static void assignRanks(Player p, int tsDbId){
		
		//Permissions
		for(PermissionAttachmentInfo pai : p.getEffectivePermissions()) {
			String perms = pai.getPermission();
			if(UltimateTs.main().getConfig().get("perms."+perms) != null){
				int gTs = UltimateTs.main().getConfig().getInt("perms."+perms);
				BotManager.getBot().addClientToServerGroup(gTs, tsDbId);
			}
	    }

		//Default Ranks
		int groupToAssign = UltimateTs.main().getConfig().getInt("config.assignWhenRegister");
		if(groupToAssign > 0){
			BotManager.getBot().addClientToServerGroup(groupToAssign, tsDbId);
		}
	}
	
	public static void updateRanks(Player p){
		if(isLinked(p)){
			int tsDbId = getLinkedWithDbId(p);
			
			//Perms
			for(PermissionAttachmentInfo pai : p.getEffectivePermissions()) {
				String perms = pai.getPermission();
				if(UltimateTs.main().getConfig().get("perms."+perms) != null){
					int gTs = UltimateTs.main().getConfig().getInt("perms."+perms);
					DatabaseClientInfo cdi = BotManager.getBot().getDatabaseClientInfo(tsDbId);
					for(int lsg : BotManager.getBot().getClientByUId(cdi.getUniqueIdentifier()).getServerGroups()){
						if(gTs != lsg){
							BotManager.getBot().addClientToServerGroup(gTs, tsDbId);
						}
					}
				}
		    }
			
			//Default Ranks
			int groupToAssign = UltimateTs.main().getConfig().getInt("config.assignWhenRegister");
			DatabaseClientInfo cdi = BotManager.getBot().getDatabaseClientInfo(tsDbId);
			for(int lsg : BotManager.getBot().getClientByUId(cdi.getUniqueIdentifier()).getServerGroups()){
				if(groupToAssign != lsg){
					if(groupToAssign > 0){
						BotManager.getBot().addClientToServerGroup(groupToAssign, tsDbId);
					}
				}
			}

		}
	}
	
	public static void removeRanks(Player p){
		if(isLinked(p)){
			int tsDbId = getLinkedWithDbId(p);
			if(tsDbId < 0) return;
			DatabaseClientInfo dbclient = BotManager.getBot().getDatabaseClientInfo(tsDbId);
			ClientInfo client = BotManager.getBot().getClientByUId(dbclient.getUniqueIdentifier());
			int clientId = client.getId();
			for(ServerGroup sg : BotManager.getBot().getServerGroupsByClientId(tsDbId)){
				BotManager.getBot().removeClientFromServerGroup(sg.getId(), tsDbId);
			}
			if(UltimateTs.main().getConfig().getBoolean("config.kickNotRegister") == true) kickNotRegister(client, clientId);
		}
	}
	
	public static void kickNotRegister(ClientInfo client, int clientId){
		int lastGroup = 0;
		for(ServerGroup sg : BotManager.getBot().getServerGroupsByClient(client)){
			lastGroup = sg.getId();
		}
		int i = UltimateTs.main().getConfig().getInt("config.defaultGroupId");
		if(lastGroup == i){
			String kick = UltimateTs.messages.getString("messages.kick");
			String poke = UltimateTs.messages.getString("messages.poke");
			BotManager.getBot().pokeClient(clientId, poke);
			BotManager.getBot().kickClientFromServer(kick, client);
		}
	}

}
