package cache.model;

import java.util.Random;

public enum NodeType {
    REDIS,
    MEMCACHE,
    HAZELCAST;

    private static final Random RND = new Random();

    public static NodeType randomNode() {
        NodeType[] nodeTypes = values();
        return nodeTypes[RND.nextInt(nodeTypes.length)];
    }
}
