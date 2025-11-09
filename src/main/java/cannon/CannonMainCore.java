package cannon;

import simulator.Channel;
import simulator.MainCore;

public class CannonMainCore extends MainCore {
    public CannonMainCore(String fileAddress, int torusLength){
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
                cores[i][j] = new CannonWorker(i, j, hchannels[i][j], vchannels[i][(j + 1) % torusLength], hchannels[(j + 1) % torusLength][j], vchannels[i][j]);
            }
        }
    }
    @Override
    public void execute() {

    }
}
