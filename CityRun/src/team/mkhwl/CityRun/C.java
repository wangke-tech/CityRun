package team.mkhwl.CityRun;

public final class C {

	public static final class database {
		public static final String table_gamepath = "gamepath";
		public static final String table_gamepoint = "gamepoint";
		public static final String table_userdata = "userdata";
		public static final String table_usergamepoint = "usergamepoint";
		public static final String table_usergamepath = "usergamepath";
	}

	public static final class game {
		public static final int gamePointStatus_NotFinish = 0;// 未完成
		public static final int gamePointStatus_OnGoing = 1;// 正在进行或创建
		public static final int gamePointStatus_Finished = 2;// 已经完成
	}

	public static final class handler {
		public static final int creat_gamepath = 1;
		public static final int location_changed = 2;
		public static final int start_game = 3;
		public static final int finish_game = 4;
	}

	public static final class baidu {
		public static final String Key = "BAAD8A2EF8395C110B97E1EE2C278AB5E63D4E9B";
	}

	public static final class renren {

		public static final String API_KEY = "b5cf84c5b280444fa1b2c49c2695d5fb";

		public static final String SECRET_KEY = "6dbcc494b13a41feb3850626124f8314";

		public static final String APP_ID = "196666";
	}
	
	public static final class game_prop{
		public static final int TEMP_GAME = 1;
		public static final int SELF_GAME = 2;
		public static final int DEF_GMAE = 3;
		public static final int ONLINE_GAME = 4;
	}

}
