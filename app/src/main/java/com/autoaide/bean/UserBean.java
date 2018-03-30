package com.autoaide.bean;


import android.text.TextUtils;

import com.autoaide.db.dao.IUser;

import z.db.ShareDB;

/**
 * 说明：用户信息的Bean
 */
public class UserBean implements IUser {

    //用户-学生ID
    public String student_no;
    //用户登录标识
    public String token;
    //用户昵称
    public String name;
    //用户联系方式
    public String mobile;
    //头像
    public String head_img_url;
    //性别1-男 2-女 3-保密
    public int gender = 1;
    //城市ID
    public String city_id;
    //城市名字
    public String city_name;

    //-----------多余字段 start---------

    // 本地登录信息
    public int login = 0;

    //-----------多余字段 End ---------


    // 防止空id在其他模块出错
    public static String getId() {
        ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
        String uid = sec.getString(ID);
        return TextUtils.isEmpty(uid) ? "null" : uid;
    }

    public static String getSex() {
        //1 男 0 女
        ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
        return sec.getString(GENDER);
    }

    public static String getAvatar() {
        ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
        String avatar = sec.getString(AVATAR);
        return avatar;
    }

    public void toSec(ShareDB.Sec s) {
        if (!TextUtils.isEmpty(student_no)) s.put(ID, student_no);
        if (name != null) s.put(NAME, name);
        if (head_img_url != null) s.put(AVATAR, head_img_url);
        if (!TextUtils.isEmpty(token)) s.put(TOKEN, token);
        if (mobile != null) s.put(MOBILE, mobile);
        if (gender != -1) s.put(GENDER, gender);
        if (!TextUtils.isEmpty(city_id)) s.put(CITY_ID, city_id);
        if (!TextUtils.isEmpty(city_name)) s.put(CITY_NAME, city_name);
    }

    public void fromSec(ShareDB.Sec s) {
        student_no = s.getString(ID);
        token = s.getString(TOKEN);
        mobile = com(s.getString(MOBILE));
        name = com(s.getString(NAME));
        gender = s.getInt(GENDER);
        head_img_url = com(s.getString(AVATAR));
        city_id = com(s.getString(CITY_ID));
        city_name = com(s.getString(CITY_NAME));
    }

    //兼容空指针
    private String com(String s) {
        return s == null ? "" : s;
    }

}
