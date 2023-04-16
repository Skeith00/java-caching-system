package cache.service;

import cache.connector.CacheConnector;
import cache.connector.CacheConnectorFactory;
import cache.model.VirtualNode;
import cache.utils.HashUtils;

import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CacheManager {

    private final Logger logger = Logger.getLogger(CacheManager.class.getName());
    private final NodeRingManager nodeRingManager;

    public CacheManager(NodeRingManager nodeRingManager) {
        this.nodeRingManager = nodeRingManager;
    }

    /**
     * Method to store a key-value in our distributed caching system.
     * @param key key to store.
     * @param value value to store.
     */
    public void cacheKey(String key, String value) {
        VirtualNode virtualNode = nodeRingManager.findClosestVirtualNodeByKey(key);
        CacheConnector connector = CacheConnectorFactory.getConnector(virtualNode.getNodeType());
        connector.cache(virtualNode.getNodeUrl(), key, value);
        virtualNode.addKey(key);
        logger.info("{ Key: " + key + ", Value: " + value + "} added.");
    }

    /**
     * Method to retrieve a value from our distributed caching system.
     * @param key key to retrieve.
     */
    public String retrieveKey(String key) {
        VirtualNode virtualNode = nodeRingManager.findClosestVirtualNodeByKey(key);
        CacheConnector connector = CacheConnectorFactory.getConnector(virtualNode.getNodeType());
        String value = connector.retrieve(virtualNode.getNodeUrl(), key);
        logger.info("Key retrieved: { Key: " + key + ", Value: " + value + "}.");
        return value;
    }

    /**
     * Method to remove a key and value from our distributed caching system.
     * @param key key to remove.
     */
    public void invalidateKey(String key) {
        VirtualNode virtualNode = nodeRingManager.findClosestVirtualNodeByKey(key);
        CacheConnector connector = CacheConnectorFactory.getConnector(virtualNode.getNodeType());
        logger.info("Removing key " + key + " from Virtual Node" + virtualNode.getVirtualNodeHash());
        connector.invalidate(virtualNode.getNodeUrl(), key);
        virtualNode.removeKey(key);
        logger.info("Key: " + key + " invalidated.");
    }

}

