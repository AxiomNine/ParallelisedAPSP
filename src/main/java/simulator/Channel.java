package simulator;
import java.util.MissingFormatArgumentException;
import java.util.OptionalDouble;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public class Channel<T extends Number> {
    private T contents;

    public Channel() {
         contents = null;
    }

    public boolean hasValue(){
        return contents == null;
    }

    public T readOut(){
        T retVal = contents;
        contents = null;
        return retVal;
    }

}
