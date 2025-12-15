package com.dyg.siginprint.base.model;

/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/06
 * desc : * 口令
 */
public class Tokens {
    public final static String Agreement = "agreement";//协议 默认http
    public final static String ServerAdr = "server_address";//服务器地址
    public final static String LoginBean = "save_login_bean";//登录实体
    public final static String UUID = "uuid";
    public static class PlayerMsg{
        public static final int PLAY_MSG = 110;
        public static final int PAUSE_MSG = 119;
        public static final int STOP_MSG = 120;
    }

}
