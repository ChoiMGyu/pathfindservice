package com.pathfind.system.algorithm;

import com.pathfind.system.controller.MemberController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    private static final Logger logger = LoggerFactory.getLogger(Dijkstra.class);

    public List<Node> shortestPath(Graph graph, Long startId) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparing(Node::getDistance));
        List<Node> nodes = new ArrayList<>();
        for(long i = 0L; i < graph.getNumVertices(); i++) {
            Node node = new Node(i, Long.MAX_VALUE);
            if(i == startId) {
                node.setDistance(0);
            }
            nodes.add(node);
            pq.add(node);
        }

        while(!pq.isEmpty()) {
            Node u = pq.poll();
            for(Graph.Node v : graph.getAdjList().get(u.getId().intValue())) {
                double alt = u.getDistance() + v.getWeight();
                logger.info("u.getDistance : " + u.getDistance() + ", v.getWeight : " + v.getWeight());
                if(alt < nodes.get(v.getV()).getDistance()) {
                    logger.info("edge relaxation -> alt(u.getDistance() + v.getWeight()) : " + alt + " < nodes.get(v.getV()).getDistance() : " + nodes.get(v.getV()).getDistance());
                    nodes.get(v.getV()).setDistance(alt);
                    pq.remove(nodes.get(v.getV()));
                    pq.offer(nodes.get(v.getV()));
                }
            }
        }
        return nodes;
    }
}
