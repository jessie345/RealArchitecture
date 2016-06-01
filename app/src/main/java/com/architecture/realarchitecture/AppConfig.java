package com.architecture.realarchitecture;

public class AppConfig {


    /**
     * 0,RELEASE
     * 1,线上测试
     * 2,测试环境
     * ....
     * tips:发布时必须置为0;
     */
    public static final int APP_ENV = 0;

    /**
     * 是否输出日志 tips:发布时请置为fasle
     */
    public final static boolean LOG_ENABLED = true;
    public final static String LOG_TAG = "test";

    // TODO: 16/4/26   base url default release
    public static String BASE_API_URL = "http://www.baidu.com";
    public static String BASE_Point_URL = "http://www.baidu.com";
    public static String BASE_H5_URL = "http://www.baidu.com";

    static {
        switch (APP_ENV) {// TODO: 16/6/1 根据具体情况，配置url域名
            case 0://发布环境
                break;
            case 1://线上测试
                break;
            case 2:// 内网测试环境
                break;
        }
    }
}
