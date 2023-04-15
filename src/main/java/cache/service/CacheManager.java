package cache.service;

import cache.connector.CacheConnector;
import cache.connector.CacheConnectorFactory;
import cache.event.NodeEventHandler;
import cache.event.NodeEventHandlerImpl;
import cache.model.Node;
import cache.model.NodeType;
import cache.model.VirtualNode;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.logging.Logger;

public class CacheManager {

    private final Logger logger = Logger.getLogger(CacheManager.class.getName());

    // 360 representing the number of degrees in a ring
    public static final int MOD = 360;
    public static final int NUM_REPLICAS = 3;

    private final SortedMap<Integer, VirtualNode> nodesCluster = new TreeMap<>();
    //private final NodeEventHandler nodeEventHandler = new NodeEventHandlerImpl();
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    public CacheManager(int numNodes) {
        new NodeEventHandlerImpl(this);
        // Create a TreeSet and inserting elements
        for (int i = 0; i < numNodes; i++) {
            UUID uuid = UUID.randomUUID();
            addNode(uuid, NodeType.randomNode());
        }
    }

    public void cacheKey(String key, String value) {
        VirtualNode virtualNode = findClosestVirtualNodeByKey(key);
        CacheConnector connector = CacheConnectorFactory.getConnector(virtualNode.getNodeType());
        connector.cache(virtualNode.getNodeUrl(), key, value);
        virtualNode.addKey(key);
    }

    public String retrieveKey(String key) {
        // TODO: Throw exception if virtualNode not found
        VirtualNode virtualNode = findClosestVirtualNodeByKey(key);
        CacheConnector connector = CacheConnectorFactory.getConnector(virtualNode.getNodeType());
        return connector.retrieve(virtualNode.getNodeUrl(), key);
    }

    public void invalidateKey(String key) {
        VirtualNode virtualNode = findClosestVirtualNodeByKey(key);
        CacheConnector connector = CacheConnectorFactory.getConnector(virtualNode.getNodeType());
        connector.invalidate(virtualNode.getNodeUrl(), key);
        virtualNode.removeKey(key);
    }

    public void addNode(UUID uuid, NodeType type) {
        Node node = new Node(uuid, type);
        for (int j = 0; j < NUM_REPLICAS; j++) {
            String replica = uuid + "-" + j;
            int hash = generateHash(replica);
            VirtualNode virtualNode = new VirtualNode(node);
            nodesCluster.put(hash, virtualNode);
            transferKeysToAddedVirtualNode(hash, virtualNode);
        }
        changes.firePropertyChange("addNode", nodesCluster, node);

        //nodeEventHandler.nodeAdded(node);
    }

    public void removeNode(UUID nodeId) {
        Node node = null;
        for (Iterator<Map.Entry<Integer, VirtualNode>> iterator = nodesCluster.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<Integer, VirtualNode> virtualNodeEntry = iterator.next();
            if (virtualNodeEntry.getValue().getNodeId() == nodeId) {
                iterator.remove();
                node = virtualNodeEntry.getValue().getNode();
                transferKeysFromRemovedVirtualNode(virtualNodeEntry.getKey(), virtualNodeEntry.getValue());
            }
        }
        if (node == null) {
            throw new RuntimeException("Node Cluster is empty.");
        }
        changes.firePropertyChange("removeNode", nodesCluster, node);
        //nodeEventHandler.nodeRemoved(node);
    }

    public void transferKeysFromRemovedVirtualNode(int hash, VirtualNode removedVirtualNode) {
        VirtualNode nextVirtualNodeByHash = findClosestVirtualNodeByHash(hash, true);
        transferKeysBetweenNodes(removedVirtualNode, nextVirtualNodeByHash);
    }

    public void transferKeysToAddedVirtualNode(int hash, VirtualNode addedVirtualNode) {
        VirtualNode previousVirtualNodeByHash = findClosestVirtualNodeByHash(hash, false);
        transferKeysBetweenNodes(previousVirtualNodeByHash, addedVirtualNode);
    }

    public void transferKeysBetweenNodes(VirtualNode oldNode, VirtualNode newNode) {
        List<String> keys = oldNode.getKeys();
        CacheConnector originNodeConnector = CacheConnectorFactory.getConnector(oldNode.getNodeType());
        CacheConnector destinationNodeConnector = CacheConnectorFactory.getConnector(newNode.getNodeType());
        keys.forEach(key -> {
            String value = originNodeConnector.retrieve(oldNode.getNodeUrl(), key);
            destinationNodeConnector.cache(newNode.getNodeUrl(), key, value);
        });
    }

    private VirtualNode findClosestVirtualNodeByKey(String key) {
        int hash = generateHash(key);
        return findClosestVirtualNodeByHash(hash, true);
    }

    private VirtualNode findClosestVirtualNodeByHash(int hash, boolean clockwise) {
        if (nodesCluster.isEmpty()) {
            throw new RuntimeException("Node Cluster is empty.");
        }
        if (nodesCluster.containsKey(hash)) {
            return nodesCluster.get(hash);
        }

        int nodeId;
        if (!clockwise) {
            SortedMap<Integer, VirtualNode> headMap = nodesCluster.headMap(hash);
            nodeId = headMap.isEmpty() ? nodesCluster.lastKey() : headMap.lastKey();
        } else {
            SortedMap<Integer, VirtualNode> tailMap = nodesCluster.tailMap(hash);
            nodeId = tailMap.isEmpty() ? nodesCluster.firstKey() : tailMap.firstKey();
        }
        return nodesCluster.get(nodeId);
    }

    private int generateHash(String key) {
        int hash = key.hashCode();
        return hash % MOD;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }

}

