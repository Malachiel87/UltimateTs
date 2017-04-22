package fr.discowzombie.ultimatets.mc;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import fr.discowzombie.ultimatets.UltimateTS;
import fr.discowzombie.ultimatets.functions.UtilsFunctions;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class RankManager {
	
	static int a = 0;
	
	public static void scanIGRank(){
		if(!UltimateTS.g().getConfig().contains("config.asignWhenregister")){
			//START FOR THE FIRST TIME
			System.out.println("--------------- [ UltimateTS - First Time ] ---------------");
			System.out.println("Starting UltimateTS for the first time ..."); 
			System.out.println("I just generated configurations before shut down...");
			System.out.println("Please wait a little...");
			System.out.println("Setup OK.");
			System.out.println("--------------- [ UltimateTS - First Time ] ---------------");
			
			if(UtilsFunctions.getPermissionSystem(false) != null){
				
				Plugin permsSystem = UtilsFunctions.getPermissionSystem(false);
				if(permsSystem.isEnabled()){
					
					System.out.println("Using "+permsSystem.getName()+".");
					UltimateTS.g().getConfig().set("config.asignWhenregister", 0);
					UltimateTS.g().saveConfig();

					if(permsSystem == UtilsFunctions.getPEX()){
						for(PermissionGroup g : PermissionsEx.getPermissionManager().getGroupList()){
							UltimateTS.g().getConfig().set("config.ranks."+g.getName().toString(), 0);
							UltimateTS.g().saveConfig();
						}
						UltimateTS.g().getConfig().set("config.perms.exemplePerms", 10);
						UltimateTS.g().saveConfig();
					}else if(permsSystem == UtilsFunctions.getzPerms()){
						UltimateTS.g().getConfig().set("config.perms.exemplePerms", 10);
						UltimateTS.g().saveConfig();
					}else if(permsSystem == UtilsFunctions.getLuckPerms()){
						UltimateTS.g().getConfig().set("config.perms.exemplePerms", 10);
						UltimateTS.g().saveConfig();
					}else if(permsSystem == UtilsFunctions.getVault()){
						for(String groups : UltimateTS.perms.getGroups()){
							UltimateTS.g().getConfig().set("config.ranks."+groups.toString(), 0);
							UltimateTS.g().saveConfig();
						}
					}else{
						UtilsFunctions.log(Level.SEVERE, "No valid permissions system found !");
						UtilsFunctions.log(Level.SEVERE, "All ranks linking functions are disabled.");
						return;
					}

					System.out.println("--------------- [ UltimateTS - Ranks Setup ] ---------------");
					System.out.println("Starting UltimateTS for the first time ..."); 
					System.out.println("Syncronization of all groups successfully!");
					System.out.println("All groups have been added in the configurations.");
					System.out.println("Go to the configuration to add the values for each groups.");
					System.out.println("Setup ending!");
					System.out.println("--------------- [ UltimateTS - Ranks Setup ] ---------------");
					Bukkit.getServer().shutdown();
				}else{
					UtilsFunctions.log(Level.WARNING, "No valid permissions system found !");
					UtilsFunctions.log(Level.WARNING, "All ranks linking functions are disabled.");
					UltimateTS.g().getConfig().set("config.asignWhenregister", 0);
					UltimateTS.g().saveConfig();
					Bukkit.getServer().shutdown();
				}

			}else{
				//NO PERMS SYSTEM FOUND
				UtilsFunctions.log(Level.WARNING, "No permissions system found !");
				UtilsFunctions.log(Level.WARNING, "All ranks linking functions are disabled.");
				UltimateTS.g().getConfig().set("config.asignWhenregister", 0);
				UltimateTS.g().saveConfig();
				Bukkit.getServer().shutdown();
			}
		}	
	}

}
