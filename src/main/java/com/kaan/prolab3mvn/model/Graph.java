/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.prolab3mvn.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kaan
 */
public class Graph {

    private final Map<String, Node> graph;

    public Graph() {
        this.graph = new HashMap<>();
    }

    public static class Node {

        private Author author;
        private Map<Node, Integer> edges;

        public Node(Author author) {
            this.author = author;
            this.edges = new HashMap<>();
        }

        public Author getAuthor() {
            return author;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }

        public Map<Node, Integer> getEdges() {
            return edges;
        }

        public void setEdges(Map<Node, Integer> edges) {
            this.edges = edges;
        }

    }

    public Map<String, Node> getGraph() {
        return graph;
    }

    public static class PathNode {

        private final Node node;
        private final int cost;
        private final List<Node> path;

        public PathNode(Node node, int cost, List<Node> path) {
            this.node = node;
            this.cost = cost;
            this.path = path;
        }

        public Node getNode() {
            return node;
        }

        public int getCost() {
            return cost;
        }

        public List<Node> getPath() {
            return path;
        }
    }

    public static class BST {

        public class TreeNode {

            private Author author;
            private TreeNode left, right;

            public TreeNode(Author author) {
                this.author = author;
            }

            public Author getAuthor() {
                return author;
            }

            public void setAuthor(Author author) {
                this.author = author;
            }

            public TreeNode getLeft() {
                return left;
            }

            public void setLeft(TreeNode left) {
                this.left = left;
            }

            public TreeNode getRight() {
                return right;
            }

            public void setRight(TreeNode right) {
                this.right = right;
            }

        }

        private TreeNode root;

        public void insert(Author author) {
            root = insertRec(root, author);
        }

        private TreeNode insertRec(TreeNode root, Author author) {
            if (root == null) {
                root = new TreeNode(author);
                return root;
            }

            if (author.getName().compareTo(root.author.getName()) < 0) {
                root.left = insertRec(root.left, author);
            } else if (author.getName().compareTo(root.author.getName()) > 0) {
                root.right = insertRec(root.right, author);
            }

            return root;
        }

        public void delete(String authorName) {
            root = deleteRec(root, authorName);
        }

        private TreeNode deleteRec(TreeNode root, String authorName) {
            if (root == null) {
                return root;
            }

            if (authorName.compareTo(root.author.getName()) < 0) {
                root.left = deleteRec(root.left, authorName);
            } else if (authorName.compareTo(root.author.getName()) > 0) {
                root.right = deleteRec(root.right, authorName);
            } else {
                if (root.left == null) {
                    return root.right;
                } else if (root.right == null) {
                    return root.left;
                }

                root.author = findMin(root.right).author;
                root.right = deleteRec(root.right, root.author.getName());
            }

            return root;
        }

        private TreeNode findMin(TreeNode root) {
            while (root.left != null) {
                root = root.left;
            }
            return root;
        }

        public void display() {
            displayRec(root, 0);
        }

        private void displayRec(TreeNode root, int level) {
            if (root != null) {
                displayRec(root.right, level + 1);
                System.out.println("  ".repeat(level) + root.author.getName());
                displayRec(root.left, level + 1);
            }
        }

        public TreeNode getRoot() {
            return root;
        }

        public void setRoot(TreeNode root) {
            this.root = root;
        }

    }

}
