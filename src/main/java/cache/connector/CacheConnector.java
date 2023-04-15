package cache.connector;

public interface CacheConnector {
    void cache(String url, String key, String value);
    String retrieve(String url, String key);
    void invalidate(String url, String key);
}
