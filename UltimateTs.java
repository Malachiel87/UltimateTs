package ultimatets;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ultimatets.minecraft.TsCommand;
import ultimatets.minecraft.TypeYesOrNo;
import ultimatets.sql.SqlConnection;
import ultimatets.teamspeak.BotManager;
import ultimatets.utils.Config;
import ultimatets.utils.PermsManager;
import ultimatets.utils.Utils;
import ultimatets.utils.enums.LogEnum;
import ultimatets.utils.enums.UtilsStorage;

public class UltimateTs extends JavaPlugin{
	
	public String n = this.getDescription().getName();
	public String v = this.getDescription().getVersion();
	public String a = this.getDescription().getAuthors().get(0);
	private String bV = Bukkit.getServer().getBukkitVersion();
	private String sV = Bukkit.getServer().getVersion();
	private String rV = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	
	public String teamSpeakIP = getConfig().getString("login.ip");
	public String teamSpeakUsername = getConfig().getString("login.username");
	public String teamSpeakPassword = getConfig().getString("login.password");
	
	public boolean databaseEnabled = getConfig().getBoolean("database.enable");
	public String databaseHost = getConfig().getString("database.host");
	public String databaseName = getConfig().getString("database.name");
	public String databaseUser = getConfig().getString("database.user");
	public String databasePassword = getConfig().getString("database.password");
	
	public static Config linkedPlayers;
	public static Config messages;
	
	public SqlConnection sql;
	
	public static UltimateTs i;
	
	BotManager tsbm = new BotManager();
	PermsManager pm = new PermsManager();
	PluginManager pmb = Bukkit.getServer().getPluginManager();
	
	public static UltimateTs main(){
		return i;
	}
	
	public void log(LogEnum ue, Level level, String message){
		if(ue != LogEnum.NULL) getLogger().log(level, "("+ue+") "+message);
		else getLogger().log(level, message);
	}
	
	@Override
	public void onEnable() {
		
		i = this;
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		messages = new Config(this, "messages.yml", "messages.yml");
		messages.save();
		linkedPlayers = new Config(this, "linkedPlayers.yml", "linkedPlayers.yml");
		linkedPlayers.save();
		Utils.setStorageType(UtilsStorage.FILE);

		pm.setPermsSystem();
		
		tsbm.runBot(teamSpeakIP, teamSpeakUsername, teamSpeakPassword);
		
		if(getConfig().getBoolean("database.enable") == true){
			sql = new SqlConnection("jdbc:mysql://", databaseHost, databaseName, databaseUser, databasePassword);
			sql.connection();
			sql.createTables();
			if(sql.connection != null) Utils.setStorageType(UtilsStorage.SQL);
		}
		
		pmb.registerEvents(new TypeYesOrNo(), this);
		
		getCommand("teamspeak").setExecutor(new TsCommand());
		
		log(LogEnum.NULL, Level.INFO, "[] ------------------------------ []");
		log(LogEnum.NULL, Level.INFO, n+" | v"+v+" | Author: "+a+".");
		log(LogEnum.NULL, Level.INFO, "Using "+bV+" | "+sV+" | "+rV+".");
		if(pm.getPermsSystem() != null) log(LogEnum.NULL, Level.INFO, "Your perms system is "+pm.getPermsSystem().getName()+".");
		else log(LogEnum.NULL, Level.INFO, "No permissions system found.");
		log(LogEnum.NULL, Level.INFO, "You can links ranks with "+pm.getPermsSystemType()+".");
		log(LogEnum.NULL, Level.INFO, "Using "+Utils.getStorageType()+" as storage type.");
		log(LogEnum.NULL, Level.INFO, "[] ------------------------------ []");
		
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		
		tsbm.stopBot();
		
		if((UltimateTs.main().getConfig().getBoolean("database.enable") == true) && (sql.useDataBase())){
			sql.disconnect();
		}
		
		super.onDisable();
	}

}
