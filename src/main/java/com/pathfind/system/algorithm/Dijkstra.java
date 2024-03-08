/*
 * 클래스 기능 : 다익스트라 알고리즘을 구현한 클래스
 * 최근 수정 일자 : 2024.02.23(월)
 */
package com.pathfind.system.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Dijkstra {
    private static final Logger logger = LoggerFactory.getLogger(Dijkstra.class);

    public static DijkstraResult dijkstra(List<Node> n, Graph graph, Long startId, Long endId) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparing(Node::getDistance));
        List<Node> nodes = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        for (long i = 0L; i < graph.getNumVertices(); i++) {
            Node node = new Node(i, Double.MAX_VALUE, n.get(Math.toIntExact(i)).isBuilding());
            if (i == startId) {
                node.setDistance(0);
                pq.add(node);
            }
            nodes.add(node);
            //pq.add(node);
            path.add(0);
        }

        while (!pq.isEmpty()) {
            Node u = pq.poll();
            for (Graph.Node v : graph.getAdjList().get(u.getId().intValue())) {
                if (v.isBuilding() && v.getV() != endId) continue;
                double alt = u.getDistance() + ((u.getId().equals(startId) && u.isBuilding()) || (v.getV() == endId && v.isBuilding()) ? 0 : v.getWeight());
                //double alt = u.getDistance() + v.getWeight();
                logger.info("u.getDistance : " + u.getDistance() + ", v.getWeight : " + v.getWeight());
                if (alt < nodes.get(v.getV()).getDistance()) {
                    logger.info("edge relaxation -> alt(u.getDistance() + v.getWeight()) : " + alt + " < nodes.get(v.getV()).getDistance() : " + nodes.get(v.getV()).getDistance());
                    nodes.get(v.getV()).setDistance(alt);
                    //pq.remove(nodes.get(v.getV()));
                    pq.offer(nodes.get(v.getV()));
                    path.set(v.getV(), u.getId().intValue());
                }
            }
        }

        return new DijkstraResult(nodes, path);
    }

    public static List<Integer> getShortestRoute(List<Integer> path, Long startId, Long endId) {
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

        return result;
    }
}
