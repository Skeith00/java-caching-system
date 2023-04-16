package cache.service;

import cache.exception.EmptyNodeRingFound;
import cache.exception.NodeNotFound;
import cache.model.Node;
import cache.model.VirtualNode;
import cache.utils.HashUtils;

import java.util.*;

public class NodeRingManager {

    private static final int NUM_REPLICAS = 3;
    private final SortedMap<Integer, VirtualNode> nodesCluster;

    public NodeRingManager() {
        nodesCluster =  new TreeMap<>();
    }

    protected List<VirtualNode> addNode(Node node) {
        List<VirtualNode> virtualNodes = new ArrayList<>();
        for (int j = 0; j < NUM_REPLICAS; j++) {
            String replica = node.getNodeId() + "-" + j;
            int hash = HashUtils.generateHash(replica);
            VirtualNode virtualNode = new VirtualNode(hash, node);
            nodesCluster.put(hash, virtualNode);
            virtualNodes.add(virtualNode);
        }
        return virtualNodes;
    }

    protected List<VirtualNode> removeNode(Node node) {
        List<VirtualNode> virtualNodes = new ArrayList<>();
        for (Iterator<Map.Entry<Integer, VirtualNode>> iterator = nodesCluster.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<Integer, VirtualNode> virtualNodeEntry = iterator.next();
            if (virtualNodeEntry.getValue().getNodeId() == node.getNodeId()) {
                iterator.remove();
                virtualNodes.add(virtualNodeEntry.getValue());
            }
        }
        if (virtualNodes.isEmpty()) {
            throw new NodeNotFound("Node " + node.getNodeId().toString() + " not found.");
        }
        return virtualNodes;
    }

    protected VirtualNode findClosestVirtualNodeByKey(String key) {
        int hash = HashUtils.generateHash(key);
        return findClosestVirtualNodeByHash(hash, true);
    }

    protected VirtualNode findClosestVirtualNodeByHash(int hash, boolean clockwise) {
        if (nodesCluster.isEmpty()) {
            throw new EmptyNodeRingFound("Node Cluster is empty.");
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

    protected boolean isEmpty() {
        return nodesCluster.size() == 0;
    }

}
