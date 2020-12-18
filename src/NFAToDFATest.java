import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

public class NFAToDFATest {
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

//        ExpandFunction<Integer, Character, >

        NFA x = new NFA(init_states, alphabet, acc_states, trans);
        DFA y = x.determinize();
        System.out.println(x);
        System.out.println(y);

//        test2 t = new test2(2);
        System.out.println("========================================================================");
        HashSet<Integer> init_states2 = new HashSet<Integer>(Arrays.asList(0));
        HashSet<Character> alphabet2 = new HashSet<Character>(Arrays.asList('0', '1'));
        HashSet<Integer> acc_states2 = new HashSet<Integer>(Arrays.asList(3));
        Map<Integer, Map<Character, Set<Integer>>> trans2 = new HashMap<Integer, Map<Character, Set<Integer>>>();

        Map<Character, Set<Integer>> q0 = new HashMap<Character, Set<Integer>>();
        q0.put('0', new HashSet<Integer>(Arrays.asList(0,1)));
        q0.put('1', new HashSet<Integer>(Arrays.asList(0)));
        trans2.put(0, q0);

        Map<Character, Set<Integer>> q1 = new HashMap<Character, Set<Integer>>();
        q1.put('0', new HashSet<Integer>(Arrays.asList(3)));
        q1.put('1', new HashSet<Integer>(Arrays.asList(3)));
        trans2.put(1, q1);

//        ExpandFunction<Integer, Character, >

        NFA x2 = new NFA(init_states2, alphabet2, acc_states2, trans2);

        System.out.println(x2);
        System.out.println(x2.determinize());
    }
}
