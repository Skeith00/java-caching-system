package cache.connector;

import cache.exception.KeyNotFound;

import java.util.HashMap;
import java.util.Map;

public class GenericConnector implements CacheConnector {

    private final Map<String, Map<String, String>> cacheEntriesPerNode = new HashMap<>();

    public void cache(String url, String key, String value) {
        Map<String, String> cacheEntriesInNode = cacheEntriesPerNode.computeIfAbsent(url, nodeKey -> {
            HashMap<String, String> cacheEntries = new HashMap<>();
            cacheEntries.put(key, value);
            cacheEntriesPerNode.put(nodeKey, cacheEntries);
            return cacheEntries;
        });
        cacheEntriesInNode.put(key, value);
    }

    public String retrieve(String url, String key) {
        Map<String, String> cacheEntriesInNode = cacheEntriesPerNode.get(url);
        if (cacheEntriesInNode == null || cacheEntriesInNode.get(key) == null) {
            throw new KeyNotFound("Key " + key + " not found.");
        }
        return cacheEntriesInNode.get(key);
    }

    public void invalidate(String url, String key) {
        Map<String, String> cacheEntriesInNode = cacheEntriesPerNode.get(url);
        if (cacheEntriesInNode == null || cacheEntriesInNode.get(key) == null) {
            throw new KeyNotFound("Key " + key + " not found.");
        }
        cacheEntriesInNode.remove(key);
    }
}
