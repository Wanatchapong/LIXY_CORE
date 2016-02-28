package com.lixy.ftapi.conf;

public class Constant {
	
	public static final String CREATED_BY = "FTAPI"; // NOSONAR
	public static int CACHE_TIMEOUT_IN_SEC = 60*60; // NOSONAR
	
	public static final int MAIL_CHECKER_FIXED_RATE = 15000; //15 sn // NOSONAR
	public static final int TOKEN_EXPIRE_CHECKER_FIXED_RATE = 30000; //15 sn // NOSONAR
	
	public static String GMAIL_ADDR = "fail@gmail.com"; // NOSONAR
	public static String GMAIL_PASS = "fail"; // NOSONAR
	public static String GMAIL_USER = "fail_user"; // NOSONAR
	
	public static String ALARM_INFORM_LIST = ""; // NOSONAR
	
	public static String DB_REQUEST_LOG_ACTIVE = "A"; // NOSONAR
	
	public static int MAXIMUM_FORTUNE_REQUEST_RESULT = 100; // NOSONAR
	
	public static final String HEADER_AUTH_TOKEN = "X-AUTH-TOKEN";
	
	public static String ROOT_FILE_PATH = "TEMP_FOLDER"; // NOSONAR
	
	public static Long UPLOAD_SERVER_ID = null;
	
	public static final String DEFAULT_LOCALE = "tr";
	
	private Constant(){
		//true
	}
}
