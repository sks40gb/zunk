package all.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Practice {

    public static void main(String[] args) {
        Predicate<String> startsWithS = (s) -> {return s.startsWith("S");};
        Function<String, Integer> f = (s)->{return s.length();};
        Supplier<String> s = ()->{return "singh";};
        Consumer<String> c = (str)->{System.out.println("S : " + str);};
        
        if(startsWithS.test("S")){
            System.out.println(f.apply("sunil"));
            System.out.println("supplier " + s.get());
            c.accept("Kumar");
        }
        
        List<String> list = new ArrayList<>();
        list.add("Sunil");
        list.add("Kumar");
        list.add("Singh");
        list.stream().filter(startsWithS).forEach(System.out::println);
    }
}


