package org.awesome.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

    public static String getNowDate() {
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
        String sysDate = time.format(nowTime);
        return sysDate;
    }

    public static String getNowTime() {
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sysDate = time.format(nowTime);
        return sysDate;
    }

    public static String getNowTimeNoFm() {
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
        String sysDate = time.format(nowTime);
        return sysDate;
    }


}
