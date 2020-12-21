import java.util.*;

public class convertToSingleInitialState {
    public static void main(String[] args) {
        test_only_existential_branching();
//        test_only_universal_branching();
//        test_only_universal_branching2();
        test_complement();
    }

    private static void test_complement() {

    }

//    private static void test_only_universal_branching2() {
//        Set<Set<Integer>> initial_states = new HashSet<>();
//        initial_states.add(new HashSet<>(Arrays.asList(1,2)));
//
//        HashSet<Character> alphabet = new HashSet<>(Arrays.asList('a','b'));
//
//        Map<Integer, Map<Character, Set<Set<Integer>>>> trans = new HashMap<>();
//
//        Map<Character, Set<Set<Integer>>> q1 = new HashMap<>();
//        HashSet<Integer> d1 = new HashSet<>(Arrays.asList(3,4));
//        HashSet<Integer> d2 = new HashSet<>(Arrays.asList(5));
//        q1.put('a', new HashSet<Set<Integer>>(Arrays.asList(d1, d2)));
//        trans.put(1, q1);
//
//
//        Map<Character, Set<Set<Integer>>> q2 = new HashMap<>();
//        HashSet<Integer> d3 = new HashSet<>(Arrays.asList(6,7));
//        HashSet<Integer> d4 = new HashSet<>(Arrays.asList(8));
//        q2.put('a', new HashSet<Set<Integer>>(Arrays.asList(d3, d4)));
//        trans.put(2, q2);
//
//        AFA a = new AFA(initial_states, alphabet, null, trans);
//        System.out.println(a);
//
//        a.convertToSingleInitialState();
//        System.out.println("=====================================================");
//        System.out.println(a);
//    }

//    private static void test_only_universal_branching() {
//        Set<Set<Integer>> initial_states = new HashSet<>();
//        initial_states.add(new HashSet<>(Arrays.asList(1,2)));
//
//        HashSet<Character> alphabet = new HashSet<>(Arrays.asList('a','b'));
//
//        Map<Integer, Map<Character, Set<Set<Integer>>>> trans = new HashMap<>();
//
//        Map<Character, Set<Set<Integer>>> q1 = new HashMap<>();
//        HashSet<Integer> d1 = new HashSet<>(Arrays.asList(3));
//        q1.put('a', new HashSet<Set<Integer>>(Arrays.asList(d1)));
//
//        HashSet<Integer> d2 = new HashSet<>(Arrays.asList(4));
//        q1.put('b', new HashSet<Set<Integer>>(Arrays.asList(d2)));
//        trans.put(1, q1);
//
//
//        Map<Character, Set<Set<Integer>>> q2 = new HashMap<>();
//        HashSet<Integer> d3 = new HashSet<>(Arrays.asList(5));
//        q2.put('a', new HashSet<Set<Integer>>(Arrays.asList(d3)));
//
//        HashSet<Integer> d4 = new HashSet<>(Arrays.asList(6));
//        q2.put('b', new HashSet<Set<Integer>>(Arrays.asList(d4)));
//        trans.put(2, q2);
//
//        AFA a = new AFA(initial_states, alphabet, null, trans);
//        System.out.println(a);
//
//        a.convertToSingleInitialState();
//        System.out.println("=====================================================");
//        System.out.println(a);
//    }

    private static void test_only_existential_branching() {
        Set<Integer> initial_states = new HashSet<>();
        initial_states.add(1);
        initial_states.add(2);

        HashSet<Character> alphabet = new HashSet<>(Arrays.asList('a','b'));

        Map<Integer, Map<Character, Set<Set<Integer>>>> trans = new HashMap<>();

        Map<Character, Set<Set<Integer>>> q1 = new HashMap<>();
        HashSet<Integer> d1 = new HashSet<>(Arrays.asList(3));
        q1.put('a', new HashSet<Set<Integer>>(Arrays.asList(d1)));

        HashSet<Integer> d2 = new HashSet<>(Arrays.asList(4));
        q1.put('b', new HashSet<Set<Integer>>(Arrays.asList(d2)));
        trans.put(1, q1);


        Map<Character, Set<Set<Integer>>> q2 = new HashMap<>();
        HashSet<Integer> d3 = new HashSet<>(Arrays.asList(5));
        q2.put('a', new HashSet<Set<Integer>>(Arrays.asList(d3)));

        HashSet<Integer> d4 = new HashSet<>(Arrays.asList(6));
        q2.put('b', new HashSet<Set<Integer>>(Arrays.asList(d4)));
        trans.put(2, q2);

        AFA a = new AFA(initial_states, alphabet, null, trans);
        System.out.println(a);

        a.convertToSingleInitialState();
        System.out.println("=====================================================");
        System.out.println(a);
    }
}
