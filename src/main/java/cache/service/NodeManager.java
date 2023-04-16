package cache.service;

import cache.connector.CacheConnector;
import cache.connector.CacheConnectorFactory;
import cache.event.NodeEventHandlerImpl;
import cache.model.Node;
import cache.model.NodeType;
import cache.model.VirtualNode;
import cache.utils.HashUtils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.logging.Logger;

public class NodeManager {

    private final Logger logger = Logger.getLogger(NodeManager.class.getName());

    private final NodeRingManager nodeRingManager;

    public NodeManager(NodeRingManager nodeRingManager) {
        this.nodeRingManager = nodeRingManager;
    }

    public void addNode(Node node) {
        boolean empty = nodeRingManager.isEmpty();
        List<VirtualNode> virtualNodes = nodeRingManager.addNode(node);
        if (!empty) {
            virtualNodes.forEach(this::transferKeysToAddedVirtualNode);
        }
    }

    public void removeNode(Node node) {
        List<VirtualNode> virtualNodes = nodeRingManager.removeNode(node);
        boolean empty = nodeRingManager.isEmpty();
        if (empty) {
            logger.warning("Last Node of the ring removed. All data stored has been lost forever!");
            return;
        }
        virtualNodes.forEach(this::transferKeysFromRemovedVirtualNode);
    }

    private void transferKeysFromRemovedVirtualNode(VirtualNode removedVirtualNode) {
        if (!removedVirtualNode.getKeys().isEmpty()) {
            VirtualNode nextVirtualNodeByHash = nodeRingManager.findClosestVirtualNodeByHash(removedVirtualNode.getVirtualNodeHash(), true);
            transferKeysBetweenNodes(removedVirtualNode, nextVirtualNodeByHash);
        }
    }

    private void transferKeysToAddedVirtualNode(VirtualNode addedVirtualNode) {
        VirtualNode previousVirtualNodeByHash = nodeRingManager.findClosestVirtualNodeByHash(addedVirtualNode.getVirtualNodeHash(), false);
        transferKeysBetweenNodes(previousVirtualNodeByHash, addedVirtualNode);
    }

    private void transferKeysBetweenNodes(VirtualNode oldNode, VirtualNode newNode) {
        Set<String> keys = oldNode.getKeys();
        if (keys.isEmpty()) {
            return;
        }
        if (oldNode.getNodeId() != newNode.getNodeId()) {
            CacheConnector originNodeConnector = CacheConnectorFactory.getConnector(oldNode.getNodeType());
            CacheConnector destinationNodeConnector = CacheConnectorFactory.getConnector(newNode.getNodeType());
            keys.forEach(key -> {
                String value = originNodeConnector.retrieve(oldNode.getNodeUrl(), key);
                destinationNodeConnector.cache(newNode.getNodeUrl(), key, value);
            });
        }
        newNode.addKeys(keys);
        oldNode.removeKeys();
    }

}

