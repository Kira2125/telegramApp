package com.example.demo;

import com.github.badoualy.telegram.api.TelegramApp;

public class Utils {
    public static final int API_ID = 0;
    public static final String API_HASH = "";

    // What you want to appear in the "all sessions" screen
    public static final String APP_VERSION = "AppVersion";
    public static final String MODEL = "Model";
    public static final String SYSTEM_VERSION = "SysVer";
    public static final String LANG_CODE = "en";

    public static TelegramApp application = new TelegramApp(API_ID, API_HASH, MODEL, SYSTEM_VERSION, APP_VERSION, LANG_CODE);

}
