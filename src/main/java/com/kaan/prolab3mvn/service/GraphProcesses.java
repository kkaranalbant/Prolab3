/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.prolab3mvn.service;

import com.kaan.prolab3mvn.model.Article;
import com.kaan.prolab3mvn.model.Author;
import com.kaan.prolab3mvn.model.Graph;
import com.kaan.prolab3mvn.model.Graph.BST;
import com.kaan.prolab3mvn.model.Graph.Node;
import com.kaan.prolab3mvn.model.Graph.PathNode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author kaan
 */
/*
Nesne olusturulurken graph nesnesı parametre olarak verilecek bu sekılde bu sınıfta graph uzerınde ıslemler yapılacak
Nesne olusturulduktan sonra verı setındeki verilerin cekılmesı ve grafın olusturulması ıcın ınıt metodu kullanılacak
 */
public class GraphProcesses {

    private Graph graph;

    private Parser parser;

    public GraphProcesses(Graph graph) {
        this.graph = graph;
        parser = new Parser();
    }

    public void init(String path) {
        parser.parse(graph, path);
        buildEdges();
    }

    public void addAuthor(Author author) {
        graph.getGraph().putIfAbsent(author.getName(), new Node(author));
    }

    private void buildEdges() {
        for (Node node1 : graph.getGraph().values()) {
            for (Node node2 : graph.getGraph().values()) {
                if (!node1.equals(node2)) {
                    int commonArticles = countCommonArticles(node1.getAuthor(), node2.getAuthor());
                    if (commonArticles > 0) {
                        node1.getEdges().put(node2, commonArticles);
                    }
                }
            }
        }
    }

    private int countCommonArticles(Author author1, Author author2) {
        Set<String> articleSet1 = new HashSet<>();
        for (Article article : author1.getArticles()) {
            articleSet1.add(article.getDoi());
        }

        int commonCount = 0;
        for (Article article : author2.getArticles()) {
            if (articleSet1.contains(article.getDoi())) {
                commonCount++;
            }
        }
        return commonCount;
    }

    public Queue<Node> findShortestPath(String startAuthorName, String endAuthorName) {
        if (!graph.getGraph().containsKey(startAuthorName) || !graph.getGraph().containsKey(endAuthorName)) {
            throw new IllegalArgumentException("One or both authors not found in the graph.");
        }

        PriorityQueue<PathNode> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(PathNode::getCost));
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();

        Node startNode = graph.getGraph().get(startAuthorName);
        Node endNode = graph.getGraph().get(endAuthorName);

        for (Node node : graph.getGraph().values()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(startNode, 0);

        priorityQueue.add(new PathNode(startNode, 0, new LinkedList<>()));

        Queue<Node> shortestPathQueue = new LinkedList<>();

        while (!priorityQueue.isEmpty()) {
            PathNode currentPathNode = priorityQueue.poll();
            Node currentNode = currentPathNode.getNode();

            if (currentNode.equals(endNode)) {
                for (Node pathNode : currentPathNode.getPath()) {
                    shortestPathQueue.add(pathNode);
                }
                shortestPathQueue.add(endNode);
                return shortestPathQueue;
            }

            for (Map.Entry<Node, Integer> neighborEntry : currentNode.getEdges().entrySet()) {
                Node neighbor = neighborEntry.getKey();
                int edgeWeight = neighborEntry.getValue();
                int newDistance = distances.get(currentNode) + edgeWeight;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    List<Node> newPath = new LinkedList<>(currentPathNode.getPath());
                    newPath.add(currentNode);
                    priorityQueue.add(new PathNode(neighbor, newDistance, newPath));
                    previousNodes.put(neighbor, currentNode);
                }
            }
        }

        return shortestPathQueue;
    }

    public int findShortestPathCost(String startAuthorName, String endAuthorName) {
        if (!graph.getGraph().containsKey(startAuthorName) || !graph.getGraph().containsKey(endAuthorName)) {
            throw new IllegalArgumentException("One or both authors not found in the graph.");
        }

        PriorityQueue<PathNode> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(PathNode::getCost));
        Map<Node, Integer> distances = new HashMap<>();

        Node startNode = graph.getGraph().get(startAuthorName);
        Node endNode = graph.getGraph().get(endAuthorName);

        for (Node node : graph.getGraph().values()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(startNode, 0);

        priorityQueue.add(new PathNode(startNode, 0, new LinkedList<>()));

        while (!priorityQueue.isEmpty()) {
            PathNode currentPathNode = priorityQueue.poll();
            Node currentNode = currentPathNode.getNode();

            if (currentNode.equals(endNode)) {
                return distances.get(endNode);
            }
            for (Map.Entry<Node, Integer> neighborEntry : currentNode.getEdges().entrySet()) {
                Node neighbor = neighborEntry.getKey();
                int edgeWeight = neighborEntry.getValue();
                int newDistance = distances.get(currentNode) + edgeWeight;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    List<Node> newPath = new LinkedList<>(currentPathNode.getPath());
                    newPath.add(currentNode);
                    priorityQueue.add(new PathNode(neighbor, newDistance, newPath));
                }
            }
        }

        return -1;
    }

    public PriorityQueue<Node> displayCollaborators(String authorName) {
        if (!graph.getGraph().containsKey(authorName)) {
            throw new IllegalArgumentException("Author not found in the graph.");
        }

        Node authorNode = graph.getGraph().get(authorName);
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(node -> -node.getAuthor().getArticles().size()));

        for (Node collaborator : authorNode.getEdges().keySet()) {
            queue.add(collaborator);
        }

        return queue;
    }

    public Map<BST, BST> createAndManipulateBST(Queue<Node> queue, String authorName) {
        if (!graph.getGraph().containsKey(authorName)) {
            throw new IllegalArgumentException("Author not found in the graph.");
        }

        Node authorNode = graph.getGraph().get(authorName);

        BST keyBST = new BST();
        for (Node node : queue) {
            keyBST.insert(node.getAuthor());
        }

        queue.removeIf(node -> node.equals(authorNode));

        BST valueBST = new BST();
        for (Node node : queue) {
            valueBST.insert(node.getAuthor());
        }

        Map<BST, BST> bstMap = new HashMap<>();
        bstMap.put(keyBST, valueBST);

        return bstMap;
    }

    public int countTotalCollaborators(String authorName) {
        if (!graph.getGraph().containsKey(authorName)) {
            throw new IllegalArgumentException("Author not found in the graph.");
        }

        Node authorNode = graph.getGraph().get(authorName);
        return authorNode.getEdges().size();
    }

    public String findAuthorWithMostCollaborations() {
        String authorWithMostCollaborations = null;
        int maxCollaborations = 0;

        for (Node node : graph.getGraph().values()) {
            int collaborations = node.getEdges().size();
            if (collaborations > maxCollaborations) {
                maxCollaborations = collaborations;
                authorWithMostCollaborations = node.getAuthor().getName();
            }
        }

        return authorWithMostCollaborations;
    }

//    public int findLongestPath(String authorName) {
//        if (!graph.getGraph().containsKey(authorName)) {
//            throw new IllegalArgumentException("Author not found in the graph.");
//        }
//
//        Node startNode = graph.getGraph().get(authorName);
//        Set<Node> visited = new HashSet<>();
//        int longestPathLength = dfsLongestPath(startNode, visited);
//
//        System.out.println("The longest path starting from author " + authorName + " contains " + longestPathLength + " nodes.");
//        return longestPathLength;
//    }
//
//    private int dfsLongestPath(Node currentNode, Set<Node> visited) {
//        visited.add(currentNode);
//        int maxLength = 0;
//
//        for (Node neighbor : currentNode.getEdges().keySet()) {
//            if (!visited.contains(neighbor)) {
//                maxLength = Math.max(maxLength, dfsLongestPath(neighbor, visited));
//            }
//        }
//
//        visited.remove(currentNode);
//        return maxLength + 1;
//    }
    public int findLongestPathCost(String authorName) {
        if (!graph.getGraph().containsKey(authorName)) {
            throw new IllegalArgumentException("Author not found in the graph.");
        }

        Node startNode = graph.getGraph().get(authorName);
        Set<Node> visited = new HashSet<>();
        return dfsLongestPathCost(startNode, visited);
    }

    private int dfsLongestPathCost(Node currentNode, Set<Node> visited) {
        visited.add(currentNode);
        int maxLength = 0;

        for (Node neighbor : currentNode.getEdges().keySet()) {
            if (!visited.contains(neighbor)) {
                maxLength = Math.max(maxLength, dfsLongestPathCost(neighbor, visited));
            }
        }

        visited.remove(currentNode);
        return maxLength + 1;
    }

    public Queue<Node> findLongestPath(String authorName) {
        if (!graph.getGraph().containsKey(authorName)) {
            throw new IllegalArgumentException("Author not found in the graph.");
        }

        Node startNode = graph.getGraph().get(authorName);
        Set<Node> visited = new HashSet<>();
        Queue<Node> longestPathQueue = new LinkedList<>();

        dfsLongestPathNodes(startNode, visited, new LinkedList<>(), longestPathQueue);
        return longestPathQueue;
    }

    private void dfsLongestPathNodes(Node currentNode, Set<Node> visited, Queue<Node> currentPath, Queue<Node> longestPath) {
        visited.add(currentNode);
        currentPath.add(currentNode);

        boolean isLeaf = true;
        for (Node neighbor : currentNode.getEdges().keySet()) {
            if (!visited.contains(neighbor)) {
                isLeaf = false;
                dfsLongestPathNodes(neighbor, visited, currentPath, longestPath);
            }
        }

        if (isLeaf && currentPath.size() > longestPath.size()) {
            longestPath.clear();
            longestPath.addAll(currentPath);
        }

        visited.remove(currentNode);
        currentPath.remove(currentNode);
    }

    public void printAuthorNames() {
        System.out.println("Graph contains the following authors:");
        for (Node node : graph.getGraph().values()) {
            System.out.println(node.getAuthor().getName());
        }
    }

    public Graph getGraph() {
        return graph;
    }

}
