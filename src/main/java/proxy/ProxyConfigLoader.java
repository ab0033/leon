package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ProxyConfigLoader {
    private static final String PROPS_PATH = "/application.properties";

    private ProxyConfigLoader() {
    }

    public static ProxyConfig loadFromClasspathProperties() {
        Properties props = new Properties();
        try (InputStream in = ProxyConfigLoader.class.getResourceAsStream(PROPS_PATH)) {
            if (in != null) props.load(in);
        } catch (IOException ignored) {
        }

        String host = props.getProperty("proxy.host", "").trim();
        int port = 0;
        try {
            port = Integer.parseInt(props.getProperty("proxy.port", "0").trim());
        } catch (NumberFormatException ignored) {
        }

        String user = props.getProperty("proxy.user", "").trim();
        String pass = props.getProperty("proxy.pass", "").trim();

        if (host.isEmpty() || port <= 0) return null;
        return new ProxyConfig(host, port, user.isEmpty() ? null : user, pass.isEmpty() ? null : pass);
    }
}
