package dijkstra;

import simulator.MainCore;

public class DijkstraMainCore extends MainCore {
    public DijkstraMainCore(String fileAddress, int torusLength) {
        super(fileAddress, torusLength);
        for(int i = 0; i < torusLength; i++) {
            for (int j = 0; j < torusLength; j++) {
                this.cores[i][j] = new DijkstraWorker(i, j);
            }
        }
    }
    @Override
    public void execute() {

    }
}
