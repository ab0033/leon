package proxy;

public record ProxyConfig(String host, int port, String username, String password) {

    public boolean hasAuth() {
        return username != null && !username.isEmpty();
    }
}
