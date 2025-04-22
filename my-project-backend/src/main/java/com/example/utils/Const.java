package com.example.utils;

public class Const {
    //JWT令牌
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";
    public final static String JWT_FREQUENCY = "jwt:frequency:";
    //邮件验证码
    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    public static final String VERIFY_EMAIL_DATA = "verify:email:data:";
    //限流计数器
    public static final String FLOW_LIMIT_COUNTER = "flow:counter:";
    //封禁名单
    public static final String FLOW_LIMIT_BLOCK = "flow:block:";
    //过滤器优先级
    public static final int ORDER_LIMIT = -101;
    public static final int ORDER_CORS = -102;
    //请求自定义属性
    public static final String ATTR_USER_ID = "userId";
    //论坛相关
    public static final String FORUM_WEATHER_CACHE = "weather:";
    public static final String FORUM_IMAGE_COUNTER = "forum:image:";
    public static final String FORUM_TOPIC_CREATE_COUNTER = "forum:topic:create:";
    public static final String FORUM_TOPIC_PREVIEW_CACHE = "topic:preview:";
}
