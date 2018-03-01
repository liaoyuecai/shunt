package cn.shunt.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置加载
 */
final class ParamsLoader {

    final static int gatewayPort;
    final static int shuntPort;

    static {
        try {
            String filePath = System.getProperty("user.dir") + "/server.properties";
            File file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("Server start fail, missing configuration file 'server.properties'");
            }
            InputStream in = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(in);
            gatewayPort = Integer.valueOf(prop.getProperty("server.gateway.port"));
            if (gatewayPort == 0) {
                if (!file.exists()) {
                    throw new RuntimeException("Server start fail, missing configuration 'gateway port'");
                }
            }
            shuntPort = Integer.valueOf(prop.getProperty("server.shunt.port"));
            if (gatewayPort == 0) {
                if (!file.exists()) {
                    throw new RuntimeException("Server start fail, missing configuration 'shunt port'");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
