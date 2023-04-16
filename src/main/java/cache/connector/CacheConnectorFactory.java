package cache.connector;

import cache.exception.NodeTypeNotFound;
import cache.model.NodeType;

public class CacheConnectorFactory {

    private static final HazelcastConnector HAZELCAST_CONNECTOR = new HazelcastConnector();
    private static final MemcacheConnector MEMCACHE_CONNECTOR = new MemcacheConnector();
    private static final RedisConnector REDIS_CONNECTOR = new RedisConnector();

    public static CacheConnector getConnector(NodeType type) {
        if (type == null) {
            throw new NodeTypeNotFound("Invalid NodeType.");
        }
        switch (type) {
            case REDIS:
                return REDIS_CONNECTOR;
            case MEMCACHE:
                return MEMCACHE_CONNECTOR;
            case HAZELCAST:
                return HAZELCAST_CONNECTOR;
            default:
                throw new NodeTypeNotFound(type + " connector not found");
        }
    }
}
