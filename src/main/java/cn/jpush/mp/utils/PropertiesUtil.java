package cn.jpush.mp.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

    static private Logger Log = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties prop = new Properties();

    private String getString(String key) {
        try {
            return prop.getProperty(key).trim();
        } catch (NullPointerException ex) {
            return null;
        }
    }

    private Integer getInt(String key) {
        try {
            return Integer.valueOf(getString(key));
        } catch (Exception ex) {
            return -1;
        }
    }

    private Boolean getBoolean(String key) {
        try {
            String value = prop.getProperty(key).trim();
            return "true".equals(value) || "TRUE".equals(value) || "1".equals(value);
        } catch (NullPointerException ex) {
            return false;
        }
    }

    private double getDouble(String key) {
        return Double.valueOf(prop.getProperty(key).trim());
    }

    private float getFloat(String key) {
        return Float.valueOf(prop.getProperty(key).trim());
    }


    private PropertiesUtil() {
    }
    /**
     * chenshunyang
     * 2016年8月12日 下午7:01:44
     */
    public static Properties getProperty(String configFileName) {
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(configFileName);
        try {
        	prop.load(in);
			return prop;
		} catch (IOException e1) {
			Log.error(String.format("configFileName %s no exist", configFileName));
		}
        return null;
    }

    public static String getProperty(String configFileName, String property) {
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(configFileName);
        try {
        	prop.load(in);
			return prop.getProperty(property);
		} catch (IOException e1) {
			Log.error(String.format("configFileName %s no exist", configFileName));
		}
        return "";
    }
    
    public static void main(String[] args) {
		System.out.println(getProperty("config.properties", "RABBIT_MQ_SERVER"));
	}
}
