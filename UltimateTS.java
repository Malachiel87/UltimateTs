package fr.discowzombie.ultimatets;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.discowzombie.ultimatets.functions.UtilsFunctions;
import fr.discowzombie.ultimatets.mc.RankManager;
import fr.discowzombie.ultimatets.mc.TypeYesOrNo;
import fr.discowzombie.ultimatets.mc.UltimateTSCmd;
import fr.discowzombie.ultimatets.sql.SqlRequest;
import fr.discowzombie.ultimatets.ts.BotManager;

public class UltimateTS extends JavaPlugin{
	
	public static HashMap<Player, String> p = new HashMap<>();
	String host, dbName, user, password;
	public SqlRequest sql;
	
	public static UltimateTS i;
	BotManager bm = new BotManager();
	
	public static UltimateTS g(){
		return i;
	}
	
	@Override
	public void onEnable() {
		i = this;
		
		getConfig().options().copyDefaults(true);
		if(getConfig().getConfigurationSection("linked") == null){
			UltimateTS.g().getConfig().createSection("linked", p);
		}

		if(getConfig().getBoolean("database.enable") == true){
			if(getConfig().getString("database.host") != null && getConfig().getString("database.host") != ""){ host = getConfig().getString("database.host"); }
			if(getConfig().getString("database.name") != null && getConfig().getString("database.name") != ""){ dbName = getConfig().getString("database.name"); }
			if(getConfig().getString("database.user") != null && getConfig().getString("database.user") != ""){ user = getConfig().getString("database.user"); }
			if(getConfig().getString("database.password") != null && getConfig().getString("database.password") != ""){ password = getConfig().getString("database.password"); }
			sql = new SqlRequest("jdbc:mysql://", host, dbName, user, password);
	        sql.connection();
	        sql.createTables();
		}
		
		if(sql != null) System.out.println("(SQL) Use database: "+sql.useDataBase());
		else System.out.println("(SQL) Use database: false");
		
		saveConfig();
		getCommand("ts").setExecutor(new UltimateTSCmd(sql));
		getServer().getPluginManager().registerEvents(new TypeYesOrNo(sql), this);
		
		UtilsFunctions.getPermissionSystem(true);
		RankManager.scanIGRank();
		
		if((getConfig().getString("login.ip") != "") && (getConfig().getString("login.ip") != null)){
			bm.runBot(getConfig().getString("login.ip"), getConfig().getString("login.username"), getConfig().getString("login.password"));
		}
		
		System.out.println(i.getDescription().getName()+" v"+i.getDescription().getVersion()+" is enabled !");
		System.out.println("Thank for using "+i.getDescription().getName()+" created by DiscowZombie.");
		
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		if(sql.useDataBase() && sql.isConnected()){
			sql.disconnect();
		}
		bm.stopBot();
		super.onDisable();
	}

}
