package all.material;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TreeSet;

/**
 *
 * @author Sunil
 */
public class Practice {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        MyOuter outer = new MyOuter();
        MyOuter.Iterator iterator = outer.interator();
        while(iterator.hasNext()){
            System.out.println("Item found " + iterator.next());
        }
        System.out.println("NEXT");
        
        iterator = outer.interator();
        while(iterator.hasNext()){
            System.out.println("Item found " + iterator.next());
        }
       
    }

    // A Generic method example 
    static <T> void genericDisplay(T element) {
        System.out.println(element.getClass().getName()
                + " = " + element);
    }
}

class MyOuter {

    private int[] data = {1, 2, 3, 4};

    public Iterator interator() {
        return new Iterator();
    }

    class Iterator {

        int currentIndex;

        boolean hasNext() {
            return currentIndex < data.length - 1;
        }

        Integer next() {
            if (hasNext()) {
                currentIndex++;
                return data[currentIndex];
            } else {
                throw new RuntimeException("No elment found");
            }
        }
    }

}
