/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.kaan.prolab3mvn;

import com.kaan.prolab3mvn.model.Graph;
import com.kaan.prolab3mvn.model.Graph.Node;
import com.kaan.prolab3mvn.service.GraphProcesses;
import java.util.Queue;
import org.apache.poi.util.IOUtils;

/**
 *
 * @author kaan
 */
public class Prolab3Mvn {

    public static void main(String[] args) {
        IOUtils.setByteArrayMaxOverride(150000000);
        Graph graph = new Graph();
        GraphProcesses processes = new GraphProcesses(graph);
        processes.init("C:\\dataset.xlsx");
        //processes.printAuthorNames();

        //System.out.println(processes.countTotalCollaborators("V. Selladurai"));
//        PriorityQueue<Node> queue = processes.displayCollaborators("V. Selladurai") ;
//        while(!queue.isEmpty()) {
//            System.out.println(queue.poll().getAuthor().getName());
//        }
        //System.out.println(processes.findAuthorWithMostCollaborations());
//        Queue<Node> queue = processes.findShortestPath("Rajesh Kumar", "V. Selladurai") ;
//        while (!queue.isEmpty()) {
//            System.out.println(queue.poll().getAuthor().getName());
//        }
//        System.out.println(processes.findShortestPathCost("Rajesh Kumar", "V. Selladurai"));
        Queue<Node> queue = processes.findLongestPath("Rajesh Kumar");
        while (!queue.isEmpty()) {
            System.out.println(queue.poll().getAuthor().getName());
        }

//        for (Map.Entry<String,Node> entry : processes.getGraph().getGraph().entrySet()) {
//            System.out.println(entry.getKey());
//            System.out.println(entry.getValue().getAuthor().getName()+"\n\n");
//        }
    }
}
