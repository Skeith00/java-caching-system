package cache.service;

import cache.connector.CacheConnector;
import cache.connector.CacheConnectorFactory;
import cache.model.VirtualNode;

import java.util.logging.Logger;

public class CacheManager {

    private final NodeRingManager nodeRingManager;
    private final Logger logger = Logger.getLogger(CacheManager.class.getName());

    public CacheManager(NodeRingManager nodeRingManager) {
        this.nodeRingManager = nodeRingManager;
    }

    public void cacheKey(String key, String value) {
        VirtualNode virtualNode = nodeRingManager.findClosestVirtualNodeByKey(key);
        CacheConnector connector = CacheConnectorFactory.getConnector(virtualNode.getNodeType());
        connector.cache(virtualNode.getNodeUrl(), key, value);
        virtualNode.addKey(key);
        logger.info("Key: " + key + ", Value: " + value + " added.");
    }

    public String retrieveKey(String key) {
        VirtualNode virtualNode = nodeRingManager.findClosestVirtualNodeByKey(key);
        CacheConnector connector = CacheConnectorFactory.getConnector(virtualNode.getNodeType());
        return connector.retrieve(virtualNode.getNodeUrl(), key);
    }

    public void invalidateKey(String key) {
        VirtualNode virtualNode = nodeRingManager.findClosestVirtualNodeByKey(key);
        CacheConnector connector = CacheConnectorFactory.getConnector(virtualNode.getNodeType());
        connector.invalidate(virtualNode.getNodeUrl(), key);
        virtualNode.removeKey(key);
        logger.info("Key: " + key + " invalidated.");
    }

}

