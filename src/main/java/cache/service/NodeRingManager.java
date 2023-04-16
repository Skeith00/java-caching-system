package cache.service;

import cache.exception.EmptyNodeRingFound;
import cache.exception.NodeNotFound;
import cache.model.Node;
import cache.model.VirtualNode;
import cache.utils.HashUtils;

import java.util.*;
import java.util.logging.Logger;

/**
 * Class representing the Consistent hash ring.
 * This class is the only one to access and manipulate the ring.
 */
public class NodeRingManager {

    private static final int NUM_REPLICAS = 3;

    private final Logger logger = Logger.getLogger(NodeRingManager.class.getName());

    private final SortedMap<Integer, VirtualNode> nodesCluster;

    public NodeRingManager() {
        nodesCluster =  new TreeMap<>();
    }

    /**
     * @param node node to add to our hash ring.
     * @return list of virtual nodes that have been created and associated with the provided node and now added to our hash ring.
     */
    protected List<VirtualNode> addNode(Node node) {
        List<VirtualNode> virtualNodes = new ArrayList<>();
        for (int j = 0; j < NUM_REPLICAS; j++) {
            String replica = j + "-" + node.getNodeId();
            int hash = HashUtils.generateHash(replica);
            VirtualNode virtualNode = new VirtualNode(hash, node);
            logger.info("Creating Virtual Node " + node.getType() + " with hash " + virtualNode.getVirtualNodeHash());
            nodesCluster.put(hash, virtualNode);
            virtualNodes.add(virtualNode);
        }
        return virtualNodes;
    }

    /**
     * @param node node to remove from our hash ring.
     * @return list of virtual nodes that were associated with the provided node and now removed from our hash ring.
     */
    protected List<VirtualNode> removeNode(Node node) {
        List<VirtualNode> virtualNodes = new ArrayList<>();
        for (Iterator<Map.Entry<Integer, VirtualNode>> iterator = nodesCluster.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<Integer, VirtualNode> virtualNodeEntry = iterator.next();
            if (virtualNodeEntry.getValue().getNodeId() == node.getNodeId()) {
                iterator.remove();
                logger.info("Removing Virtual Node with hash " + virtualNodeEntry.getValue().getVirtualNodeHash());
                virtualNodes.add(virtualNodeEntry.getValue());
            }
        }
        if (virtualNodes.isEmpty()) {
            throw new NodeNotFound("Node " + node.getNodeId().toString() + " not found.");
        }
        return virtualNodes;
    }

    /**
     * Method to find the closes Virtual Node/replica in the hash ring.
     * @param key value used to generate a hash numerical code to find the closest Virtual Node in the Ring.
     * @return Closest Virtual Node found.
     */
    protected VirtualNode findClosestVirtualNodeByKey(String key) {
        int hash = HashUtils.generateHash(key);
        return findClosestVirtualNodeByHash(hash);
    }

    /**
     * Method to find the closes Virtual Node/replica in the hash ring.
     * @param hash number that will be user to find the closest Virtual Node in the Ring.
     * @return Closest Virtual Node found.
     */
    protected VirtualNode findClosestVirtualNodeByHash(int hash) {
        if (nodesCluster.isEmpty()) {
            throw new EmptyNodeRingFound("Node Cluster is empty.");
        }
        if (nodesCluster.containsKey(hash)) {
            return nodesCluster.get(hash);
        }

        SortedMap<Integer, VirtualNode> tailMap = nodesCluster.tailMap(hash);
        int nodeId = tailMap.isEmpty() ? nodesCluster.firstKey() : tailMap.firstKey();

        return nodesCluster.get(nodeId);
    }

    protected boolean isEmpty() {
        return nodesCluster.size() == 0;
    }

    // method added purely for Console Menu, option "Send a nodeShuttingDown event"
    public Node getRandomNode() {
        if (nodesCluster.isEmpty()) {
            throw new EmptyNodeRingFound("Node Cluster is empty.");
        }
        Integer key = nodesCluster.firstKey();
        return nodesCluster.get(key).getNode();
    }

}
