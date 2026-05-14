package origin;

import base.MainCore;
import cannon.CannonMainCore;
import dijkstra.DijkstraMainCore;
import dynamic.DynamicImplementation;
import fox.FoxMainCore;

public class Main {
    public static void main(String[] args) {

        String fileAddress = Main.class.getResource("/" + args[2]).getPath();
        MainCore core;
        switch(args[0]) {
            default:
                core = new DijkstraMainCore(fileAddress, Integer.parseInt(args[1]), args[3]);
                break;
            case "F":
                core = new FoxMainCore(fileAddress, Integer.parseInt(args[1]), args[3]);
                break;
            case "C":
                core = new CannonMainCore(fileAddress, Integer.parseInt(args[1]), args[3]);
                break;
        }
        if (!args[4].equals("N")){
            new DynamicImplementation(core, Integer.valueOf(args[4]), args[5]);
        }
    }
}