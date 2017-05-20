package ultimatets.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;
import ultimatets.UltimateTs;
import ultimatets.utils.enums.PermsSystemType;

public class PermsManager {
	
	protected String[] s = { "PermissionsEx", "zPermissions", "LuckPerms", "Vault" };
	public List<String> app = Arrays.asList(s);
	public Plugin permsPlugin = null;
	public Permission perms;

	public void setPermsSystem() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		for(Plugin pl : pm.getPlugins()){
			if(pl.isEnabled()){
				String plName = pl.getName();
				if(app.contains(plName)){
					permsPlugin = pm.getPlugin(plName);
				}
			}
		}
	}
	
	public Plugin getPermsSystem(){
		return permsPlugin;
	}
	
	public PermsSystemType getPermsSystemType(){
		Plugin p = getPermsSystem();
		if(p != null){
			if(p.getName().equalsIgnoreCase("PermissionsEx")) return PermsSystemType.PermissionsEx;
			if(p.getName().equalsIgnoreCase("zPerms")) return PermsSystemType.zPerms;
			if(p.getName().equalsIgnoreCase("LuckPerms")) return PermsSystemType.LuckPerms;
			if(p.getName().equalsIgnoreCase("Vault")) return PermsSystemType.Vault;
		}
		return PermsSystemType.Minecraft;
	}
	
	//Vault
	public boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = UltimateTs.main().getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	
	//Vault
	public Permission getPermissions() {
        return perms;
    }

}
