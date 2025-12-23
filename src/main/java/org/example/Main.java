package org.example;

import cannon.CannonMainCore;
import dijkstra.DijkstraMainCore;
import fox.FoxMainCore;
import simulator.MainCore;
import simulator.SimulatedCache;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        MainCore mainCore;
        switch(args[0]){
            case "D":
                mainCore = new DijkstraMainCore("C:\\Users\\Daniel\\Documents\\ParallelProject\\ParallelisedAPSP\\src\\main\\resources\\basic_graph.txt", Integer.parseInt(args[1]));
                break;
            case "F":
                mainCore = new FoxMainCore("C:\\Users\\Daniel\\Documents\\ParallelProject\\ParallelisedAPSP\\src\\main\\resources\\basic_graph.txt", Integer.parseInt(args[1]));
                break;
            case "C":
                mainCore = new CannonMainCore("fileAddress", Integer.parseInt(args[1]));
                break;
        }
    }
}