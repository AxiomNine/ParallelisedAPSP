package fox;

import simulator.MainCore;
import simulator.Channel;

public class FoxMainCore extends MainCore {
    public FoxMainCore(String fileAddress, int torusLength){
        super(fileAddress, torusLength);
        Channel[][] hchannels = new Channel[torusLength][torusLength];
        Channel[][] vchannels = new Channel[torusLength][torusLength];
        for (int i = 0; i < torusLength; i++){
            for (int j = 0; j < torusLength; j++){
                hchannels[i][j] = new Channel();
                vchannels[i][j] = new Channel();
            }
        }
        for (int i = 0; i < torusLength; i++){
            for (int j = 0; j < torusLength; j++){
                cores[i][j] = new FoxWorker(i, j, hchannels[i][j], vchannels[i][(j + 1) % torusLength], hchannels[(j + 1) % torusLength][j], vchannels[i][j]);
            }
        }
    }
    @Override
    public void execute() {

    }
}
