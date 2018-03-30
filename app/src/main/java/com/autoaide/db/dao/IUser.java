package com.autoaide.db.dao;

import android.text.TextUtils;

import com.autoaide.bean.UserBean;
import com.k12lib.afast.log.Logger;

import z.db.ShareDB;


/**
 * 说明：本地对用户信息操作的类
 */
public interface IUser {
    String KEY_AUTO_LOGIN = "autologin";
    String KEY_LOGIN = "login";
    String SEC_USERBEAN = "userbean";
    String SEC_AUTOLOGIN = "userinfo";

    //用户id
    String ID = "id";
    // 昵称
    String NAME = "name";
    //用户登录标识
    String TOKEN = "token";

    //手机号
    String MOBILE = "mobile";
    //密码
    String PWD = "password";
    //性别 1-男 2-女
    String GENDER = "gender";
    //头像
    String AVATAR = "head_img_url";
    //城市id
    String CITY_ID = "city_id";
    //城市名称
    String CITY_NAME = "city_name";
    //是否付费
    String VIP = "vip";

    class Dao {
        private static UserBean mUser = null;

        // 获取用户
        public static UserBean getUser() {
            if (mUser == null) {
                mUser = new UserBean();
                ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
                mUser.fromSec(sec);
            }
            return mUser;
        }

        public static String getUserId() {
            return UserBean.getId();
        }

        // 检查用户是否登录
        public static boolean checkAutoLogin() {
            ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
            return sec.getBoolean(KEY_AUTO_LOGIN);
        }

        public static void clearUser() {
            ShareDB.Sec.clearSec(SEC_USERBEAN);
        }

        public static void saveUser() {
            if (mUser == null) return;
            saveUser(mUser);
        }

        // 保存用户
        public static void saveUser(UserBean user) {
            ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
            if (!TextUtils.isEmpty(user.student_no)) {
                String oldId = sec.getString(IUser.ID);
                if (oldId == null || !oldId.equals(user.student_no)) {
                    clearUser();
                    sec.clearAttrs();
                }
            } else {
                //防止id为空
                user.student_no = sec.getString(ID);
            }

            user.toSec(sec);
            sec.save(false);
            if (mUser != null && user != mUser) {
                mUser.fromSec(sec);
            }
        }

        public static void exitUser() {
            clearUser();
            if (mUser != null) {
                // 保留id值不清除
                ShareDB.Key.update(SEC_USERBEAN, ID, mUser.student_no);
                mUser = null;
            }
        }

        // 更新用户头像
        public static void updateAvatar(String avatar) {
            UserBean user = getUser();
            if (user.head_img_url != null && user.head_img_url.equals(avatar)) return;
            user.head_img_url = avatar;
            ShareDB.Key.update(SEC_USERBEAN, AVATAR, avatar);
        }

        // 更新用户性别
        public static void updateSex(int gender) {
            UserBean user = getUser();
            if (user.gender != -1 && user.gender == gender) return;
            user.gender = gender;
            ShareDB.Key.update(SEC_USERBEAN, GENDER, gender);
        }

        public static void updateLogin(int status) {
            UserBean user = getUser();
            if (user == null || user.login == status) return;
            user.login = status;
            ShareDB.Key.update(SEC_AUTOLOGIN, KEY_LOGIN, status);
        }

        public static boolean isLogin() {
            ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
            sec.load();
            return sec.getInt(KEY_LOGIN) > 0;
        }

        // 更新用户头像
        public static void updataImage(String avatar) {
            UserBean user = getUser();
            if (user.head_img_url != null && user.head_img_url.equals(avatar)) return;
            user.head_img_url = avatar;
            ShareDB.Key.update(SEC_USERBEAN, AVATAR, avatar);

        }

        public static void updateToken(String token) {
            UserBean user = getUser();
            if (user != null) {
                user.token = token;
            }
            Logger.i("注册完成后更新token >>> " + token);
            ShareDB.Key.update(SEC_AUTOLOGIN, IUser.TOKEN, token);
            ShareDB.Key.update(SEC_USERBEAN, IUser.TOKEN, token);
            //saveUser(user);
        }

        public static String getToken() {
            UserBean user = getUser();
            if (user == null) return null;
            return user.token;
        }

        public static void updateAccAndPwd(String acc, String pwd) {
            ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
            sec.put(KEY_AUTO_LOGIN, true);
            sec.put(IUser.MOBILE, acc);
            sec.put(IUser.PWD, pwd);
            sec.save(false);
        }
    }
}
