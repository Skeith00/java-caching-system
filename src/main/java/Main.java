import cache.service.CacheManager;

public class Main {
    public static void main(String[] args) {
        CacheManager cacheManager = new CacheManager(3);
        cacheManager.cacheKey("key1", "value1");
        cacheManager.cacheKey("key2", "value2");
        cacheManager.cacheKey("key3", "value3");

        System.out.println(cacheManager.retrieveKey("key1")); // Output: value1
        System.out.println(cacheManager.retrieveKey("key2")); // Output: value2
        System.out.println(cacheManager.retrieveKey("key3")); // Output: value3
    }
}