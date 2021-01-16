import java.util.*;

public class Tests {
    public static void main(String[] args) {
//        aut1();
        aut2();
    }

    private static void aut2() {
    }

    public static void aut1() {
        HashSet<Integer> init_states = new HashSet<>();
        init_states.add(0);

        HashSet<Character> alphabet = new HashSet<>(Arrays.asList('a', 'b', 'c', 'd'));

        HashSet<Integer> acc_states = new HashSet<>();
        acc_states.add(3);

        Map<Integer, Map<Character, Set<Set<Integer>>>> trans = new HashMap<>();

        Map<Character, Set<Set<Integer>>> s0_map = new HashMap<>();
        Set<Integer> s0 = new HashSet<>(Arrays.asList(0));
        Set<Integer> s1 = new HashSet<>(Arrays.asList(1));
        Set<Integer> s2 = new HashSet<>(Arrays.asList(2));

        s0_map.put('a', new HashSet<>(Arrays.asList(s0, s1, s2)));
        s0_map.put('b', new HashSet<>(Arrays.asList(s0, s1)));
        s0_map.put('c', new HashSet<>(Arrays.asList(s2)));

        trans.put(0, s0_map);

        Map<Character, Set<Set<Integer>>> s1_map = new HashMap<>();
        Set<Integer> s3 = new HashSet<>(Arrays.asList(3));

        s1_map.put('d', new HashSet<>(Arrays.asList(s3)));

        trans.put(1, s1_map);

        Map<Character, Set<Set<Integer>>> s2_map = new HashMap<>();
        s2_map.put('d', new HashSet<>(Arrays.asList(s3)));

        trans.put(2, s2_map);

        AFA x = new AFA(init_states, alphabet, acc_states, trans);

        x.disambiguateByComplement();
//        System.out.println(x);
        x.trim();
        System.out.println(x);
//        System.out.println(x.complement);
        System.out.println(x.forwardAlternationRemoval());
        NFA y = x.reverseDeterminize();
        y.trim();
        System.out.println(y);
    }
}
