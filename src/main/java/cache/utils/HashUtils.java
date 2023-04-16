package cache.utils;


public class HashUtils {

    private HashUtils() {
    }

    // 360 representing the number of degrees in a ring
    public static final int MOD = 360;
    public static int generateHash(String key) {
        int hash = key.hashCode();
        return hash % MOD;
    }

}
