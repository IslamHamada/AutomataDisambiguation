import java.util.*;

public class NFASelfProductTest {
    public static void main(String[] args) {
        HashSet<Integer> init_states = new HashSet<Integer>(Arrays.asList(1));
        HashSet<Character> alphabet = new HashSet<Character>(Arrays.asList('a'));
        HashSet<Integer> acc_states = new HashSet<Integer>(Arrays.asList(4));
        Map<Integer, Map<Character, Set<Integer>>> trans = new HashMap<Integer, Map<Character, Set<Integer>>>();

        Map<Character, Set<Integer>> a1 = new HashMap<Character, Set<Integer>>();
        a1.put('a', new HashSet<Integer>(Arrays.asList(2,3)));
        trans.put(1, a1);

        Map<Character, Set<Integer>> a3 = new HashMap<Character, Set<Integer>>();
        a3.put('a', new HashSet<Integer>(Arrays.asList(4)));
        trans.put(3, a3);

        Map<Character, Set<Integer>> a2 = new HashMap<Character, Set<Integer>>();
        a2.put('a', new HashSet<Integer>(Arrays.asList(4)));
        trans.put(2, a2);

        NFA x = new NFA(init_states, alphabet, acc_states, trans);

        System.out.println(x);
        System.out.println("=======================================================================");

        NFA y = x.self_product();
        y.expandForward();
        System.out.println(y);
    }
}
