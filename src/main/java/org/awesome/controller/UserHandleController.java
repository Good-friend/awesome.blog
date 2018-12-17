package org.awesome.controller;

import org.awesome.Dao.RedisDao;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserHandleController {

    private RedisDao redisDao;
    private static String getNowTime() {
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sysDate = time.format(nowTime);
        return sysDate;
    }
    private static String getNowTimeNoFm() {
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
        String sysDate = time.format(nowTime);
        return sysDate;
    }
    static String str = "0000";
    private String getMajorKeyId(String type){
        String id = (String)redisDao.get("major_key_id");
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
        String sysDate = time.format(nowTime);
        int p = Integer.parseInt(str) + 1;
        if(p > 9999) {
            p = 0;
        }
        str = String.format("%04d",p);
        id = type+sysDate+str;
        redisDao.set("major_key_id",id);
        return id;
    }
}
