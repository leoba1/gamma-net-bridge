package com.bai.utils;

import java.util.regex.Pattern;
import static com.bai.constants.Constants.*;

/**
 * 正则表达式工具类
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/17 16:26
 */
public class Regex {

    public static void checkHost(String ip){
        boolean matchesIP = Pattern.compile(IP).matcher(ip).matches();
        boolean matchesDOMIN = Pattern.compile(DOMAIN).matcher(ip).matches();
        if(!matchesIP && !matchesDOMIN && !ip.equals("localhost")){
            throw new RuntimeException("ip或域名格式不正确");
        }
    }

    public static void checkPort(String port){
        boolean matches = Pattern.compile(PORT).matcher(port).matches();
        if(!matches){
            throw new RuntimeException("端口号格式不正确");
        }
    }
}
