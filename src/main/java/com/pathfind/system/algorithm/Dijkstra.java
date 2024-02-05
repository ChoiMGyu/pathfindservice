package com.pathfind.system.algorithm;

import com.pathfind.system.controller.MemberController;
import com.pathfind.system.domain.RoadVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Dijkstra {
    private static final Logger logger = LoggerFactory.getLogger(Dijkstra.class);

    public static DijkstraResult shortestPath(Graph graph, Long startId, Long endId) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparing(Node::getDistance));
        List<Node> nodes = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        for(long i = 0L; i < graph.getNumVertices(); i++) {
            Node node = new Node(i, Double.MAX_VALUE);
            if(i == startId) {
                node.setDistance(0);
            }
            nodes.add(node);
            pq.add(node);
            path.add(0);
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
                    path.set(v.getV(), u.getId().intValue());
                }
            }
        }

        List<Integer> result = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        int endToStart = endId.intValue();
        stack.push(endToStart);
        while (endToStart != startId.intValue()) {
            endToStart = path.get(endToStart);
            stack.push(endToStart);
        }
        logger.info("find path start({}) to end({})", startId, endId);
        while (!stack.isEmpty()) {
            logger.info("path: {}", stack.peek());
            result.add(stack.pop());
        }

        return new DijkstraResult(nodes, result);
    }
}
