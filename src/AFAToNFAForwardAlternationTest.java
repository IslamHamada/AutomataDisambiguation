import java.util.*;

public class AFAToNFAForwardAlternationTest {
    public static void main(String[] args) {
        Set<Character> init_states = new HashSet<>();
        init_states.add('q');

        HashSet<Character> alphabet = new HashSet<>(Arrays.asList('a','b'));
        HashSet<Character> acc_states = new HashSet<>(Arrays.asList('q', 's'));
        Map<Character, Map<Character, Set<Set<Character>>>> trans = new HashMap<>();

        Map<Character, Set<Set<Character>>> q = new HashMap<>();
        HashSet<Character> d1 = new HashSet<Character>(Arrays.asList('q'));
        HashSet<Character> d2 = new HashSet<Character>(Arrays.asList('s'));
        q.put('a', new HashSet<Set<Character>>(Arrays.asList(d1,d2)));
        trans.put('q', q);

        Map<Character, Set<Set<Character>>> s = new HashMap<>();
        HashSet<Character> d3 = new HashSet<>(Arrays.asList('q', 's'));
        s.put('b', new HashSet<Set<Character>>(Arrays.asList(d3)));
        trans.put('s', s);

        AFA x = new AFA(init_states, alphabet, acc_states, trans);
        System.out.println(x);
        NFA y = x.forwardAlternationRemoval();
        System.out.println(y);
        System.out.println("===============================================================");
    }
}
