package com.kaan.prolab3mvn.gui;

import com.kaan.prolab3mvn.model.Author;
import com.kaan.prolab3mvn.model.Graph;
import com.kaan.prolab3mvn.model.Graph.BST;
import com.kaan.prolab3mvn.model.Graph.BST.TreeNode;
import com.kaan.prolab3mvn.model.Graph.Node;
import com.kaan.prolab3mvn.service.GraphProcesses;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.poi.util.IOUtils;

public class PathVisualization extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Graph graph;
    private GraphProcesses graphProcesses;
    private Queue<Node> shortestPathQueue;

    public PathVisualization(Graph graph) {
        this.graph = graph;
        this.graphProcesses = new GraphProcesses(graph);

        setTitle("Graph Visualization");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, "Buttons");

        add(mainPanel);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(10, 1, 10, 10));
        panel.setBackground(Color.LIGHT_GRAY);

        JButton shortestPathCostButton = new JButton("Find Shortest Path Cost");
        JButton collaboratorsButton = new JButton("Display Collaborators");
        JButton totalCollaboratorsButton = new JButton("Count Total Collaborators");
        JButton mostCollaborationsButton = new JButton("Find Author with Most Collaborations");
        JButton longestPathCostButton = new JButton("Find Longest Path Cost");
        JButton longestPathButton = new JButton("Find Longest Path");
        JButton bstManipulationButton = new JButton("Manipulate BST");
        JButton commonArticlesButton = new JButton("Count Common Articles");
        JButton shortestPathButton = new JButton("Find Shortest Path");

        shortestPathCostButton.addActionListener(e -> showShortestPathCost());
        collaboratorsButton.addActionListener(e -> showCollaboratorsPanel());
        totalCollaboratorsButton.addActionListener(e -> showTotalCollaborators());
        mostCollaborationsButton.addActionListener(e -> showAuthorWithMostCollaborations());
        longestPathCostButton.addActionListener(e -> showLongestPathCost());
        longestPathButton.addActionListener(e -> showLongestPathPanel());
        bstManipulationButton.addActionListener(e -> showBSTManipulationPanel());
        commonArticlesButton.addActionListener(e -> showCommonArticles());
        shortestPathButton.addActionListener(e -> showShortestPath());

        panel.add(shortestPathCostButton);
        panel.add(collaboratorsButton);
        panel.add(totalCollaboratorsButton);
        panel.add(mostCollaborationsButton);
        panel.add(longestPathCostButton);
        panel.add(longestPathButton);
        panel.add(bstManipulationButton);
        panel.add(commonArticlesButton);
        panel.add(shortestPathButton);

        return panel;
    }

    private void showCommonArticles() {
        String author1Name = JOptionPane.showInputDialog(this, "Enter First Author Name:");
        String author2Name = JOptionPane.showInputDialog(this, "Enter Second Author Name:");

        if (author1Name == null || author2Name == null || author1Name.isEmpty() || author2Name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide valid author names.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            
            Author author1 = graphProcesses.getGraph().getGraph().get(author1Name).getAuthor() ;
            Author author2 = graphProcesses.getGraph().getGraph().get(author2Name).getAuthor() ;
            int commonArticlesCount = graphProcesses.countCommonArticles(author1, author2);
            JOptionPane.showMessageDialog(this, "Number of common articles: " + commonArticlesCount, "Common Articles", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showShortestPath() {
        String startAuthorName = JOptionPane.showInputDialog(this, "Enter Start Author Name:");
        String endAuthorName = JOptionPane.showInputDialog(this, "Enter End Author Name:");

        if (startAuthorName == null || endAuthorName == null || startAuthorName.isEmpty() || endAuthorName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide valid author names.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
           shortestPathQueue = graphProcesses.findShortestPath(startAuthorName, endAuthorName);

            // Create a panel or dialog to display the shortest path
            StringBuilder pathString = new StringBuilder("Shortest path between " + startAuthorName + " and " + endAuthorName + ":\n");
            for (Node node : shortestPathQueue) {
                pathString.append(node.getAuthor().getName()).append("\n");
            }
            JOptionPane.showMessageDialog(this, pathString.toString(), "Shortest Path", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showShortestPathCost() {
        String startAuthor = JOptionPane.showInputDialog(this, "Enter Start Author Name:");
        String endAuthor = JOptionPane.showInputDialog(this, "Enter Finish Author Name:");

        if (startAuthor == null || endAuthor == null || startAuthor.isEmpty() || endAuthor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide valid author names.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int shortestPathCost = graphProcesses.findShortestPathCost(startAuthor, endAuthor);
            JOptionPane.showMessageDialog(this,
                    "The shortest path cost between " + startAuthor + " and " + endAuthor + " is: " + shortestPathCost,
                    "Shortest Path Cost",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCollaboratorsPanel() {
        String authorName = JOptionPane.showInputDialog(this, "Enter Author Name:");

        if (authorName == null || authorName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide a valid author name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            PriorityQueue<Node> collaboratorsQueue = graphProcesses.displayCollaborators(authorName);
            JPanel collaboratorsPanel = new CollaboratorsPanel(collaboratorsQueue);
            mainPanel.add(collaboratorsPanel, "Collaborators");
            cardLayout.show(mainPanel, "Collaborators");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public class CollaboratorsPanel extends JPanel {

        public CollaboratorsPanel(PriorityQueue<Node> collaboratorsQueue) {
            setLayout(new BorderLayout());
            JLabel header = new JLabel("Collaborators List", SwingConstants.CENTER);
            header.setFont(new Font("Arial", Font.BOLD, 16));
            add(header, BorderLayout.NORTH);

            if (collaboratorsQueue == null || collaboratorsQueue.isEmpty()) {
                add(new JLabel("No collaborators found.", SwingConstants.CENTER), BorderLayout.CENTER);
                return;
            }

            // Create a scrollable list for collaborators
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (Node collaborator : collaboratorsQueue) {
                listModel.addElement(collaborator.getAuthor().getName());
            }
            JList<String> collaboratorsList = new JList<>(listModel);
            collaboratorsList.setFont(new Font("Arial", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(collaboratorsList);
            add(scrollPane, BorderLayout.CENTER);

            // Add a back button
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> {
                Container parent = getParent();
                if (parent instanceof JPanel) {
                    CardLayout layout = (CardLayout) parent.getLayout();
                    layout.show(parent, "Main");
                }
            });
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(backButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private void showTotalCollaborators() {
        String authorName = JOptionPane.showInputDialog(this, "Enter Author Name:");

        try {
            int totalCollaborators = graphProcesses.countTotalCollaborators(authorName);
            JOptionPane.showMessageDialog(this,
                    "Total collaborators for " + authorName + ": " + totalCollaborators,
                    "Total Collaborators",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAuthorWithMostCollaborations() {
        String authorName = graphProcesses.findAuthorWithMostCollaborations();

        JOptionPane.showMessageDialog(this,
                "Author with the most collaborations: " + authorName,
                "Most Collaborations",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showLongestPathCost() {
        String authorName = JOptionPane.showInputDialog(this, "Enter Author Name:");

        try {
            int longestPathCost = graphProcesses.findLongestPathCost(authorName);
            JOptionPane.showMessageDialog(this,
                    "The longest path cost starting from " + authorName + ": " + longestPathCost,
                    "Longest Path Cost",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLongestPathPanel() {
        String authorName = JOptionPane.showInputDialog(this, "Enter Author Name:");

        try {
            Queue<Node> longestPath = graphProcesses.findLongestPath(authorName);
            JPanel longestPathPanel = new PathPanel(longestPath);
            mainPanel.add(longestPathPanel, "LongestPath");
            cardLayout.show(mainPanel, "LongestPath");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class PathPanel extends JPanel {

        private Queue<Node> pathQueue;

        public PathPanel(Queue<Node> pathQueue) {
            this.pathQueue = new LinkedList<>(pathQueue);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 50;
            int y = getHeight() / 2;
            int nodeDiameter = 40;
            int spacing = 100;

            Node previousNode = null;
            while (!pathQueue.isEmpty()) {
                Node currentNode = pathQueue.poll();

                g2d.setColor(Color.BLUE);
                g2d.fillOval(x, y - nodeDiameter / 2, nodeDiameter, nodeDiameter);
                g2d.setColor(Color.WHITE);
                g2d.drawString(currentNode.getAuthor().getName(), x + 10, y + 5);

                if (previousNode != null) {
                    g2d.setColor(Color.BLACK);
                    g2d.drawLine(x - spacing + nodeDiameter, y, x, y);
                    drawArrowHead(g2d, x - spacing + nodeDiameter, y, x, y);
                }

                previousNode = currentNode;
                x += spacing;
            }
        }

        private void drawArrowHead(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            int arrowSize = 10;
            double angle = Math.atan2(y2 - y1, x2 - x1);
            int xArrow1 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
            int yArrow1 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
            int xArrow2 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
            int yArrow2 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));

            g2d.fillPolygon(new int[]{x2, xArrow1, xArrow2}, new int[]{y2, yArrow1, yArrow2}, 3);
        }
    }

    private static class TreeVisualizationPanel extends JPanel {

        private final BST bst;
        private final String title;

        public TreeVisualizationPanel(BST bst, String title) {
            this.bst = bst;
            this.title = title;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawTree(g2d, getWidth() / 2, 50, getWidth() / 4, bst.getRoot(), 50);
        }

        private void drawTree(Graphics2D g2d, int x, int y, int xOffset, TreeNode node, int yOffset) {
            if (node == null) {
                return;
            }

            g2d.setColor(Color.BLACK);
            g2d.fillOval(x - 20, y - 20, 40, 40);
            g2d.setColor(Color.WHITE);
            g2d.drawString(node.getAuthor().getName(), x - 10, y + 5);

            if (node.getLeft() != null) {
                g2d.drawLine(x, y, x - xOffset, y + yOffset);
                drawTree(g2d, x - xOffset, y + yOffset, xOffset / 2, node.getLeft(), yOffset);
            }

            if (node.getRight() != null) {
                g2d.drawLine(x, y, x + xOffset, y + yOffset);
                drawTree(g2d, x + xOffset, y + yOffset, xOffset / 2, node.getRight(), yOffset);
            }
        }
    }

    private void showBSTManipulationPanel() {
        String authorName = JOptionPane.showInputDialog(this, "Enter Author Name:");

        if (authorName == null || authorName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide a valid author name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (shortestPathQueue == null) {
            JOptionPane.showMessageDialog(null, "You must Provide Shortest Path First");
            return;
        }

        try {
            Map<BST, BST> bstMap = graphProcesses.createAndManipulateBST(shortestPathQueue, authorName);

            if (bstMap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No BST data generated.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BST keyBST = bstMap.keySet().iterator().next();
            BST valueBST = bstMap.values().iterator().next();

            JPanel bstPanel = new JPanel(new GridLayout(1, 2));
            bstPanel.add(new TreeVisualizationPanel(keyBST, "Key BST"));
            bstPanel.add(new TreeVisualizationPanel(valueBST, "Value BST"));

            mainPanel.add(bstPanel, "BSTManipulation");
            cardLayout.show(mainPanel, "BSTManipulation");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        IOUtils.setByteArrayMaxOverride(150000000);
        Graph graph = new Graph();
        GraphProcesses processes = new GraphProcesses(graph);
        processes.init("C:\\dataset.xlsx");
        SwingUtilities.invokeLater(() -> {
            PathVisualization frame = new PathVisualization(graph);
            frame.setVisible(true);
        });
    }
}
