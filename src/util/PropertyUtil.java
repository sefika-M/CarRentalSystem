package util;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertyUtil {
    public static String getPropertyString(String filename) throws IOException {
        String connStr = null;
        Properties pro = new Properties();
        pro.load(new FileInputStream(filename));
        String host = pro.getProperty("host");
        String db = pro.getProperty("db");
        String protocol = pro.getProperty("protocol");
        String user = pro.getProperty("user");
        String pass = pro.getProperty("password");
        String port = pro.getProperty("port");
        connStr = protocol + "//" + host + ":" + port + "/" + db + "?user=" + user + "&password=" + pass;
        return connStr;
    }
}

