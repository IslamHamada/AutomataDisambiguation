import java.util.*;

public class test5_1 {
    public static void main(String[] args) {
        HashSet<Integer> init_states = new HashSet<>();
        init_states.add(0);

        HashSet<Character> alphabet = new HashSet<>(Arrays.asList('a', 'b'));

        HashSet<Integer> acc_states = new HashSet<>(Arrays.asList(8));

        Map<Integer, Map<Character, Set<Set<Integer>>>> trans = new HashMap<>();

        Set<Integer> s0 = new HashSet<>(Arrays.asList(0));
        Set<Integer> s1 = new HashSet<>(Arrays.asList(1));
        Set<Integer> s2 = new HashSet<>(Arrays.asList(2));
        Set<Integer> s3 = new HashSet<>(Arrays.asList(3));
        Set<Integer> s4 = new HashSet<>(Arrays.asList(4));
        Set<Integer> s5 = new HashSet<>(Arrays.asList(5));
        Set<Integer> s6 = new HashSet<>(Arrays.asList(6));
        Set<Integer> s7 = new HashSet<>(Arrays.asList(7));
        Set<Integer> s8 = new HashSet<>(Arrays.asList(8));

        Map<Character, Set<Set<Integer>>> s0_map = new HashMap<>();

        s0_map.put('a', new HashSet<>(Arrays.asList(s1)));
        s0_map.put('b', new HashSet<>(Arrays.asList(s1)));

        trans.put(0, s0_map);

        Map<Character, Set<Set<Integer>>> s1_map = new HashMap<>();

        s1_map.put('a', new HashSet<>(Arrays.asList(s1)));
        s1_map.put('b', new HashSet<>(Arrays.asList(s2)));

        trans.put(1, s1_map);

        Map<Character, Set<Set<Integer>>> s2_map = new HashMap<>();

        s2_map.put('a', new HashSet<>(Arrays.asList(s6)));
        s2_map.put('b', new HashSet<>(Arrays.asList(s3, s5)));

        trans.put(2, s2_map);

        Map<Character, Set<Set<Integer>>> s3_map = new HashMap<>();

        s3_map.put('a', new HashSet<>(Arrays.asList(s4)));
        s3_map.put('b', new HashSet<>(Arrays.asList(s3)));

        trans.put(3, s3_map);

        Map<Character, Set<Set<Integer>>> s4_map = new HashMap<>();

        s4_map.put('a', new HashSet<>(Arrays.asList(s7)));

        trans.put(4, s4_map);

        Map<Character, Set<Set<Integer>>> s5_map = new HashMap<>();

        s5_map.put('b', new HashSet<>(Arrays.asList(s7)));

        trans.put(5, s5_map);

        Map<Character, Set<Set<Integer>>> s6_map = new HashMap<>();

        s6_map.put('a', new HashSet<>(Arrays.asList(s7)));

        trans.put(6, s6_map);

        Map<Character, Set<Set<Integer>>> s7_map = new HashMap<>();

        s7_map.put('a', new HashSet<>(Arrays.asList(s8)));
        s7_map.put('b', new HashSet<>(Arrays.asList(s8)));

        trans.put(7, s7_map);

        AFA x = new AFA(init_states, alphabet, acc_states, trans);
        System.out.println(x);
//        x.disambiguateByComplement();
//        x.trim();
//        System.out.println(x);

        NFA y = x.reverseDeterminize();
        y.trim();
        System.out.println(y);
        DFA z = y.determinize();
        System.out.println(z);

//        NFA y = x.forwardAlternationRemoval();
//        y.trim();
//        System.out.println(y);
//        DFA z = y.determinize();
//        System.out.println(z);
    }
}
