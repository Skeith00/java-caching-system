import cache.model.Node;
import cache.model.NodeType;
import cache.service.CacheManager;
import cache.service.NodeManager;
import cache.service.NodeRingManager;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        NodeRingManager nodeRingManager = new NodeRingManager();
        NodeManager nodeManager = new NodeManager(nodeRingManager);
        CacheManager cacheManager = new CacheManager(nodeRingManager);
        for (int i = 0; i < 3; i++) {
            Node node = new Node(UUID.randomUUID(), NodeType.randomNode());
            nodeManager.addNode(node);
        }

        cacheManager.cacheKey("key1", "value1");
        cacheManager.cacheKey("key2", "value2");
        cacheManager.cacheKey("key3", "value3");

        System.out.println(cacheManager.retrieveKey("key1")); // Output: value1
        System.out.println(cacheManager.retrieveKey("key2")); // Output: value2
        System.out.println(cacheManager.retrieveKey("key3")); // Output: value3
    }
}