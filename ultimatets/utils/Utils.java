package ultimatets.utils;

import ultimatets.utils.enums.UtilsStorage;

public class Utils {
	
	public static UtilsStorage utilsstorage = UtilsStorage.FILE;
	
	public static UtilsStorage getStorageType(){
		return utilsstorage;
	}
	
	public static void setStorageType(UtilsStorage us){
		utilsstorage = us;
	}

}
