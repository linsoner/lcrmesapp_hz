package com.dyg.siginprint.http.api;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class ApiList {

    //获取系统版本号
    public static String API_SYS_VERSION = "/api/sys/version";
    //下载apk
    public static String API_GET_APK = "/api/files/app";
    //登录
    public static String API_LOGIN = "/api/user/login";
    //权限升级
    public static String API_USER_UPGRADE = "/api/user/upgrade";
    //扫描结果
    public static String API_STOCK_BILLINFO = "/api/stockbill/billno";
    //扫描结果
    public static String API_SCAN_RESULT = "/api/label/split";
    //保存扫描记录
    public static String API_SAVESCAN = "/api/label/savescan";
    //基础信息
    public static String API_BaseData = "/api/sys/basedata";
    //二维码解析,采购
    public static String API_StockbillSplit = "/api/stockbill/split";
    //保持扫码结果，采购
    public static String API_Stockbill_Savescan = "/api/stockbill/savescan";
    //拆箱打印接口
    public static String API_SYS_Print = "/api/sys/print";
    //设备维护二维码解析
    public static String API_Machine = "/api/sys/machine";
    //获取设备保养项目
    public static String API_Machineitems = "/api/sys/machineitems";
    //保存保养项目
    public static String API_Savemchine = "/api/sys/savemachine";

}
