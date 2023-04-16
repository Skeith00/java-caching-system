package cache.utils;


public class HashUtils {

    private HashUtils() {
    }

    // Modulus number used in our hash function
    public static final int MOD = 1080;
    public static int generateHash(String key) {
        int hash = key.hashCode() & Integer.MAX_VALUE;
        return hash % MOD;
    }

}
