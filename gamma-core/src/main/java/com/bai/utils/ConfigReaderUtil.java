package com.bai.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ConfigReaderUtil {

    public static String ConfigReader(String key) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        String value = null;

        try {
            inputStream = ConfigReaderUtil.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(inputStream);
            value = properties.getProperty(key);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    public static List<String> ConfigReaders(String key) {
        Properties properties = new Properties();
        InputStream InputStream = null;
        List<String> values = new ArrayList<>();

        try {
            InputStream = ConfigReaderUtil.class.getClassLoader().getResourceAsStream("config.properties");// 加载配置文件
//            fileInputStream = new FileInputStream("config.properties");
            properties.load(InputStream);

            // 读取配置项
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String currentKey = (String) enumeration.nextElement();
                if (currentKey.equals(key)) {
                    String value = properties.getProperty(currentKey);
                    values.add(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (InputStream != null) {
                try {
                    InputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return values;
    }
}
