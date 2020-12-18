import java.util.*;

public class NFA <StateCore, Alphabet, InputStateCore, InputTranOutput> extends Automaton<StateCore, Alphabet, Set<StateCore>, InputStateCore, InputTranOutput>{

    public NFA(Set<StateCore> init_states, Set<Alphabet> alphabet, Set<StateCore> acc_states, Map<StateCore, Map<Alphabet, Set<StateCore>>> trans) {
        super(init_states, alphabet, acc_states, trans);
    }


    public NFA(Set<Alphabet> alphabet, Set<StateCore> acc_states, ExpandFunction<StateCore, Alphabet, Map<InputStateCore, Map<Alphabet, InputTranOutput>>, StateCore> expandBackwardsFunction, HasPropertyFunction<InputStateCore, StateCore> isInitialStateFunction, Set<InputStateCore> in_init_states, Map<InputStateCore, Map<Alphabet, InputTranOutput>> in_trans) {
        super(alphabet, acc_states, expandBackwardsFunction, isInitialStateFunction, in_init_states, in_trans);
    }

    @Override
    public void expandForward() {
    }

    @Override
    public Queue<Set<StateCore>> expandForward(StateCore s) {
        return null;
    }

    @Override
    public void expandBackwards() {
        Queue<StateCore> queue = new LinkedList<StateCore>();
        Set<StateCore> expanded = new HashSet<StateCore>();
        queue.addAll(getAcc_states());
        while(!queue.isEmpty()){
            StateCore s = queue.remove();
            if(!expanded.contains(s)) {
                queue.addAll(expandBackwards(s));
                isInitialState(s);
            }
            expanded.add(s);
        }
    }

    public Queue<StateCore> expandBackwards(StateCore s){
        Queue<StateCore> queue = new LinkedList<>();
        Set<StateCore> expanded = new HashSet<>();
        for(Alphabet c : getAlphabet()) {
            StateCore computed_state = expandBackwardsFunction.apply(s, c, getIn_trans());
            expanded.add(s);
            if (computed_state != null) {
//                for (StateCore s2 : in_states) {
//                    Set<StateCore> out_state = new HashSet<StateCore>(Arrays.asList(s));
                    Map<Alphabet, Set<StateCore>> state_map = getTrans().get(computed_state);
                    if(state_map == null){
                        state_map = new HashMap<>();
                        trans.put(computed_state, state_map);
                    }
                    Set<StateCore> state_letter_map = getTrans().get(computed_state).get(c);
                    if(state_letter_map == null){
                        state_letter_map = new HashSet<>();
                        state_map.put(c, state_letter_map);
                    }
                    state_letter_map.add(s);
                    queue.add(computed_state);
//                }
            }
        }
        return queue;
    }

    public DFA determinize(){
        Set<StateCore> init_state = getInit_states();
        Set<Alphabet> alphabet = getAlphabet();
        Map<StateCore, Map<Alphabet, Set<StateCore>>> in_trans = getTrans();

        ExpandFunction<Set<StateCore>, Alphabet, Map<StateCore, Map<Alphabet, Set<StateCore>>>, Set<StateCore>> NfaToDfaExpand = (state, letter, in_tran) -> {
            HashSet<StateCore> outputState = new HashSet<StateCore>();
            for(StateCore s : state){
                Map<Alphabet, Set<StateCore>> s_trans = in_tran.get(s);
                if(s_trans != null) {
                    Set<StateCore> tran_out = s_trans.get(letter);
                    if (tran_out != null) {
                        for (StateCore s2 : tran_out)
                            outputState.add(s2);
                    }
                }
            }
            if(!outputState.isEmpty())
                return outputState;
            else
                return null;
        };

        HasPropertyFunction<StateCore, Set<StateCore>> NfaToDfaIsAcceptStateFunction = (inAccStates, state) -> {
            for (StateCore s : state) {
                if (inAccStates.contains(s))
                    return true;
            }
            return false;
        };

        DFA A_out = new DFA(init_state, alphabet, NfaToDfaExpand, NfaToDfaIsAcceptStateFunction, in_trans, getAcc_states());
        A_out.expandForward();

        return A_out;
    }

}
