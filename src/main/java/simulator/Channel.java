package simulator;
import java.util.MissingFormatArgumentException;
import java.util.OptionalDouble;

public class Channel {
    private OptionalDouble contents;

    public Channel() {
         contents = OptionalDouble.empty();
    }

    public double readOut(){
        if (contents.isPresent()){
            return contents.getAsDouble();
        } else {
            throw new MissingFormatArgumentException("Trying to read an empty channel");
        }
    }

    public void writeIn(double val){
        if (contents.isEmpty()) {
            contents = OptionalDouble.of(val);
        } else {
            throw new MissingFormatArgumentException("Trying to write into a full channel");
        }
    }
}
