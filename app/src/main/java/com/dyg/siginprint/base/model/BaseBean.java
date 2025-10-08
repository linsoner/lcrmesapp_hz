package com.dyg.siginprint.base.model;


/**
 * Created by Administrator on 2018-02-20.
 */

public class BaseBean {

    /**
     * "code": 0,      //返回类型：1001成功   1002失败  1098登录过期  1099异常
     * "msg": "string", //调用不成功时的提示信息
     * "data": {}        //返回对象或字符串，根据具体接口定
     *
     * {"code":1002,"msg":"English Message : Connection open error . 用户 'sa' 登录失败。\r\nChinese Message :  连接数据库过程中发生错误，检查服务器是否正常连接字符串是否正确，实在找不到原因请先Google错误信息：用户 'sa' 登录失败。.","data":null}
     */
    private int code; //1001时为用户已离线，需要登录
    private String data;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
