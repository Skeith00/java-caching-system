package cache.service;

import cache.connector.CacheConnector;
import cache.connector.CacheConnectorFactory;
import cache.model.Node;
import cache.model.VirtualNode;
import cache.utils.HashUtils;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NodeManager {

    private final Logger logger = Logger.getLogger(NodeManager.class.getName());

    private final NodeRingManager nodeRingManager;

    public NodeManager(NodeRingManager nodeRingManager) {
        this.nodeRingManager = nodeRingManager;
    }

    /**
     * Method to add a Node to the hash ring and redistribute some keys to this new Node.
     * @param node Node to add.
     */
    public void addNode(Node node) {
        boolean empty = nodeRingManager.isEmpty();
        logger.info("Adding node " + node.getNodeId());
        List<VirtualNode> virtualNodes = nodeRingManager.addNode(node);
        if (!empty) {
            virtualNodes.forEach(this::transferKeysToAddedVirtualNode);
        }
    }

    /**
     * Method to remove a Node to the hash ring and transfer all keys to a different Node.
     * @param node Node to add.
     */
    public void removeNode(Node node) {
        logger.info("Removing node " + node.getNodeId());
        List<VirtualNode> virtualNodes = nodeRingManager.removeNode(node);
        boolean empty = nodeRingManager.isEmpty();
        if (empty) {
            logger.warning("Last Node of the ring removed. All data stored has been lost forever!");
            return;
        }
        virtualNodes.forEach(this::transferKeysFromRemovedVirtualNode);
    }

    /**
     * Method to transfer all keys from a removed Node to an active one.
     * @param removedVirtualNode VirtualNode removed from the hash ring.
     */
    private void transferKeysFromRemovedVirtualNode(VirtualNode removedVirtualNode) {
        Set<String> keys = removedVirtualNode.getKeys();
        if (!keys.isEmpty()) {
            VirtualNode nextVirtualNodeByHash = nodeRingManager.findClosestVirtualNodeByHash(removedVirtualNode.getVirtualNodeHash() + 1);
            Set<String> keysToTransfer = new HashSet<>();
            if (removedVirtualNode.getNodeId() == nextVirtualNodeByHash.getNodeId()) {
                logger.info("Virtually transferring keys from " + removedVirtualNode.getVirtualNodeHash() + " to " + nextVirtualNodeByHash.getVirtualNodeHash());
                keysToTransfer.addAll(keys);
            } else {
                keys.forEach(key -> {
                    int keyHash = HashUtils.generateHash(key);
                    logger.info("Transferring Virtual Node Key " + key + ", Hash " + keyHash +
                            " from " + removedVirtualNode.getVirtualNodeHash() + "(" + removedVirtualNode.getNodeUrl() + ")"
                            + " to " + nextVirtualNodeByHash.getVirtualNodeHash() + "(" + nextVirtualNodeByHash.getNodeUrl() + ")");

                    transferKeys(removedVirtualNode, nextVirtualNodeByHash, key);
                    keysToTransfer.add(key);
                });
            }
            nextVirtualNodeByHash.addKeys(keysToTransfer);
            removedVirtualNode.removeKeys(keysToTransfer);
        }
    }

    /**
     * Method to transfer keys to a new Node.
     * @param addedVirtualNode VirtualNode added to the hash ring.
     */
    private void transferKeysToAddedVirtualNode(VirtualNode addedVirtualNode) {
        VirtualNode nextVirtualNodeByHash = nodeRingManager.findClosestVirtualNodeByHash(addedVirtualNode.getVirtualNodeHash() + 1);

        logger.info("Added Virtual Node: " + addedVirtualNode.getVirtualNodeHash() +
                        ". Closest Virtual Node is " + nextVirtualNodeByHash.getVirtualNodeHash() +
                        ", containing keys " + nextVirtualNodeByHash.getKeys()
                            .stream()
                            .map(key -> String.valueOf(HashUtils.generateHash(key)))
                            .collect(Collectors.joining(",", "{", "}")));

        transferKeysToAddedVirtualNode(nextVirtualNodeByHash, addedVirtualNode);
    }

    /**
     * Method to transfer keys between nodes
     * @param nextVirtualNodeByHash VirtualNode closest to the newly added Node.
     * @param newNode VirtualNode added to the hash ring.
     */
    private void transferKeysToAddedVirtualNode(VirtualNode nextVirtualNodeByHash, VirtualNode newNode) {
        Set<String> keys = nextVirtualNodeByHash.getKeys();
        Set<String> keysToTransfer = new HashSet<>();

        if (keys.isEmpty()) {
            return;
        }


        boolean sameNode = nextVirtualNodeByHash.getNodeId() == newNode.getNodeId();

        keys.forEach(key -> {
            int keyHash = HashUtils.generateHash(key);
            VirtualNode closestVirtualNodeToProvidedKey = nodeRingManager.findClosestVirtualNodeByHash(keyHash);
            boolean transferToNewNode = closestVirtualNodeToProvidedKey == newNode;
            if (transferToNewNode && sameNode) {
                logger.info("Key " + keyHash + " with hash " + keyHash + " virtually transferring key" +
                        " from Virtual Node " + nextVirtualNodeByHash.getVirtualNodeHash() +
                        " to Virtual Node " + newNode.getVirtualNodeHash());
                keysToTransfer.add(key);
            } else if (transferToNewNode) {
                logger.info("Transferring Virtual Node Key " + key + ", Hash " + keyHash +
                        " from " + nextVirtualNodeByHash.getVirtualNodeHash() + "(" + nextVirtualNodeByHash.getNodeUrl() + ")"
                        + " to " + newNode.getVirtualNodeHash() + "(" + newNode.getNodeUrl() + ")");

                transferKeys(nextVirtualNodeByHash, newNode, key);
                keysToTransfer.add(key);
            }
        });
        newNode.addKeys(keysToTransfer);
        nextVirtualNodeByHash.removeKeys(keysToTransfer);
    }

    private void transferKeys(VirtualNode originNode, VirtualNode destinationNode, String key) {
        CacheConnector originNodeConnector = CacheConnectorFactory.getConnector(originNode.getNodeType());
        CacheConnector destinationNodeConnector = CacheConnectorFactory.getConnector(destinationNode.getNodeType());
        String value = originNodeConnector.retrieve(originNode.getNodeUrl(), key);
        destinationNodeConnector.cache(destinationNode.getNodeUrl(), key, value);
        originNodeConnector.invalidate(originNode.getNodeUrl(), key);
    }

}

