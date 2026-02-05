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
        String fileAddress = "C:\\Users\\Daniel\\Documents\\ParallelProject\\ParallelisedAPSP\\src\\main\\resources\\" + args[2];
        switch(args[0]) {
            case "D":
                new DijkstraMainCore(fileAddress, Integer.parseInt(args[1]));
                break;
            case "F":
                new FoxMainCore(fileAddress, Integer.parseInt(args[1]));
                break;
            case "C":
                new CannonMainCore(fileAddress, Integer.parseInt(args[1]));
                break;
        }
    }
}