package unit;

import cache.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class HashUtilsTest {

    @Test
    public void testGenerateHashCollisions() {
        for (int i = 0; i < 1000; i++) {
            UUID uuid = UUID.randomUUID();
            String replica1 = "1-" + uuid;
            String replica2 = "2-" + uuid;

            int hash1 = HashUtils.generateHash(replica1);
            int hash2 = HashUtils.generateHash(replica2);

            Assert.assertTrue(hash1 != hash2);
            //Assert.assertTrue(hash1 > 0 && hash1 < 360);
            //Assert.assertTrue(hash2 > 0 && hash2 < 360);
        }
    }

}
