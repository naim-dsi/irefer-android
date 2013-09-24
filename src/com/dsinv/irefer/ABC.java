package com.dsinv.irefer;

public class ABC {
	//final static String HOST_URL = "http://203.202.248.108/dsi/admin/";
	//final static String WEB_URL = HOST_URL+"index.php?r="; 
	//final static String PIC_URL = HOST_URL+"pics/";
	final static String WEB_URL = "http://103.4.147.139/irefer-dsi/index.php/services/"; 

	enum Language {
		ENGLISH(1, "English"),
		FRENCH(2, "French"),
		SPANISH(3, "Spanish"),
		GERMAN(4, "German"),
		ITALIAN(5, "Italian"),
		CHINESE(6, "Chinese"),
		RUSSIAN(7, "Russian"),
		HINDI(8, "Hindi"),
		HEBREW(9, "Hebrew"),
		DUTCH(10, "Dutch"),
		ARABIC(11, "Arabic"),
		THAI(12, "Thai"),
		URDU(13, "Urdu"),
		TAMIL(14, "Tamil"),
		BENGALI(15, "Bengali"),
		POLISH(16, "Polish"),
		KOREAN(17, "Korean"),
		ROMANIAN(18, "Romanian"),
		FARSI(19, "Farsi"),
		TURKISH(20, "Turkish");
		public final String name;
		public final int id;
		Language(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}
	enum OfficeHour {
		WEEKEND(1, "Weekend Hours ?"),
		EVENING(2, "Evening Hours ?");
		public final String name;
		public final int id;
		OfficeHour(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}
}