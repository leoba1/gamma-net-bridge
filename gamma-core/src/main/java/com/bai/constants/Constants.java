package com.bai.constants;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/11 18:39
 */
public interface Constants {
    //魔数
    byte[] MAGIC_NUM={'C','A','F','E'};
    String IP = "^((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
    String DOMAIN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";
    String PORT = "^([1-9]|[1-9][0-9]{1,3}|[1-6][0-5][0-5][0-3][0-5])$";

}
