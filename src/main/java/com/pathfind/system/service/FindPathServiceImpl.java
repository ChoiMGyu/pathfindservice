package com.pathfind.system.service;

import com.pathfind.system.algorithm.Edge;
import com.pathfind.system.domain.RoadEdge;
import com.pathfind.system.domain.RoadVertex;
import com.pathfind.system.repository.FindPathRepository;
import com.pathfind.system.domain.RoadEdge;
import com.pathfind.system.domain.RoadVertex;
import com.pathfind.system.domain.SidewalkEdge;
import com.pathfind.system.domain.SidewalkVertex;
import com.pathfind.system.repository.RoadEdgeRepository;
import com.pathfind.system.repository.RoadVertexRepository;
import com.pathfind.system.repository.SidewalkEdgeRepository;
import com.pathfind.system.repository.SidewalkVertexRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindPathServiceImpl implements FindPathService {

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    private final FindPathRepository findPathRepository;
/*    private final RoadEdgeRepository roadEdgeRepository;
    private final RoadVertexRepository roadVertexRepository;
    private final SidewalkEdgeRepository sidewalkEdgeRepository;
    private final SidewalkVertexRepository sidewalkVertexRepository;*/

    @Override
    public List<Edge> findEdgeAll() {
        List<RoadEdge> edgeList = findPathRepository.findRoadEdgeAll();
        List<Edge> edges = new LinkedList<>();
        for (RoadEdge roadEdge : edgeList) {
            RoadVertex start = roadEdge.getRoadVertex1();
            RoadVertex end = roadEdge.getRoadVertex2();
            edges.add(new Edge(start.getId() - 1, end.getId() - 1, (double) roadEdge.getLength()));
        }
        return edges;
    }

   /* @Override
    @Transactional
    public List<RoadVertex> findRoadPath(Long start, Long end) {
        List<RoadVertex> vertices = roadVertexRepository.findAll();
        List<RoadEdge> edges = roadEdgeRepository.findAll();
        logger.info("roadVertices size: {}", vertices.size());
        logger.info("roadEdges size: {}", edges.size());
        logger.info("도로 정점의 개수를 반환");
        Dijkstra dijkstra = new Dijkstra(vertices.size());
        for (RoadEdge edge : edges) {
            dijkstra.putEdge(edge.getRoadVertex1().getId().intValue(), edge.getRoadVertex2().getId().intValue(), edge.getLength());
        }
        logger.info("findPath {} to {}", start.intValue(), end.intValue());
        List<Integer> result = dijkstra.findPath(start.intValue(), end.intValue());
        List<RoadVertex> answer = new ArrayList<>();
        Stack<Integer> path = new Stack<>();
        int endToStart = end.intValue();
        path.push(endToStart);
        while (endToStart != start.intValue()) {
            endToStart = result.get(endToStart);
            path.push(endToStart);
        }
        while (!path.isEmpty()) {
            logger.info("path: {}", path.peek());
            answer.add(vertices.get(path.pop() - 1));
        }
        return answer;
    }

    @Override
    @Transactional
    public List<SidewalkVertex> findSidewalkPath(Long start, Long end) {
        List<SidewalkVertex> vertices = sidewalkVertexRepository.findAll();
        List<SidewalkEdge> edges = sidewalkEdgeRepository.findAll();
        logger.info("sidewalkVertices size: {}", vertices.size());
        logger.info("sidewalkEdges size: {}", edges.size());
        Dijkstra dijkstra = new Dijkstra(vertices.size());
        for (SidewalkEdge edge : edges) {
            dijkstra.putEdge(edge.getSidewalkVertex1().getId().intValue(), edge.getSidewalkVertex2().getId().intValue(), edge.getLength());
        }
        logger.info("findPath {} to {}", start.intValue(), end.intValue());
        List<Integer> result = dijkstra.findPath(start.intValue(), end.intValue());
        List<SidewalkVertex> answer = new ArrayList<>();
        Stack<Integer> path = new Stack<>();
        int endToStart = end.intValue();
        path.push(endToStart);
        while (endToStart != start.intValue()) {
            endToStart = result.get(endToStart);
            path.push(endToStart);
        }
        while (!path.isEmpty()) {
            logger.info("path: {}", path.peek());
            answer.add(vertices.get(path.pop() - 1));
        }
        return answer;
    }

    class Dijkstra {
        ArrayList<Node>[] adjacency;
        ArrayList<Integer> path;
        PriorityQueue<Node> priorityQueue;
        double[] distance;

        public Dijkstra(int num) {
            adjacency = new ArrayList[num + 1];
            path = new ArrayList<>();
            distance = new double[num + 1];
            for (int i = 0; i <= num; i++) {
                distance[i] = Double.MAX_VALUE;
                adjacency[i] = new ArrayList<>();
                path.add(0);
            }
        }

        public List<Integer> findPath(int start, int end) {
            priorityQueue = new PriorityQueue<>();
            priorityQueue.offer(new Node(start, 0));
            distance[start] = 0;

            while (!priorityQueue.isEmpty()) {
                Node node = priorityQueue.poll();
                if (distance[node.v] < node.length) continue;
                for (int i = 0; i < adjacency[node.v].size(); i++) {
                    Node nxt = adjacency[node.v].get(i);
                    if (distance[nxt.v] > node.length + nxt.length) {
                        distance[nxt.v] = node.length + nxt.length;
                        priorityQueue.offer(new Node(nxt.v, distance[nxt.v]));
                        path.set(nxt.v, node.v);
                    }
                }
            }
            return path;
        }

        public void putEdge(int a, int b, double length) {
            adjacency[a].add(new Node(b, length));
        }

        class Node implements Comparable<Node> {
            int v;
            double length;

            public Node(int v, double length) {
                this.v = v;
                this.length = length;
            }

            @Override
            public int compareTo(Node o) {
                return Double.compare(this.length, o.length);
            }
        }
    }*/
}
