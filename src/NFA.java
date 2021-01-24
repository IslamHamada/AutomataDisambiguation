import java.util.*;

public class NFA <StateCore, Alphabet, InputStateCore, InputTranOutput> extends Automaton<StateCore, Alphabet, Set<StateCore>, InputStateCore, InputTranOutput>{

    Set<StateCore> init_states;

    public NFA(Set<StateCore> init_states, Set<Alphabet> alphabet, Set<StateCore> acc_states, Map<StateCore, Map<Alphabet, Set<StateCore>>> trans) {
        super(alphabet, acc_states, trans);
        this.init_states = init_states;
    }


    public NFA(Set<Alphabet> alphabet, Set<StateCore> acc_states, ExpandFunction<StateCore, Alphabet, Map<InputStateCore, Map<Alphabet, InputTranOutput>>, StateCore> expandBackwardsFunction, HasPropertyFunction<InputStateCore, StateCore> isInitialStateFunction, Set<InputStateCore> in_init_states, Map<InputStateCore, Map<Alphabet, InputTranOutput>> in_trans) {
        super(alphabet, acc_states, expandBackwardsFunction, isInitialStateFunction, in_init_states, in_trans);
        init_states = new HashSet<>();
    }

    public NFA(Set<StateCore> init_states, Set<Alphabet> alphabet, ExpandFunction<StateCore, Alphabet, Map<InputStateCore, Map<Alphabet, InputTranOutput>>, Set<StateCore>> expandForwardFunction, HasPropertyFunction<InputStateCore, StateCore> isAcceptStateFunction, Map<InputStateCore, Map<Alphabet, InputTranOutput>> in_trans, Set<InputStateCore> in_acc_states) {
        super(alphabet, expandForwardFunction, isAcceptStateFunction, in_trans, in_acc_states);
        this.init_states = init_states;
    }

    @Override
    public String toString(){
        return "Automaton{\n" +
                "init_states=" + init_states + super.toString();
    }

    @Override
    protected Set<StateCore> calcStateSpace() {
        Set<StateCore> state_space = new HashSet<>();
        state_space.addAll(init_states);

        for(StateCore state : trans.keySet()){
            for(Alphabet letter : trans.get(state).keySet()){
                state_space.addAll(trans.get(state).get(letter));
            }
        }

        return state_space;
    }

    @Override
    public Set<StateCore> getInit_states() {
        return init_states;
    }

    public void setInit_states(Set<StateCore> init_states) {
        this.init_states = init_states;
    }

    @Override
    public boolean isInitialState(StateCore s) {
        boolean isInitialState =  isInitialStateFunction.apply(getIn_init_states(), s);
        if(isInitialState)
            init_states.add(s);
        return isInitialState;
    }

    @Override
    public Queue<StateCore> expandForward(StateCore s) {
        Queue<StateCore> queue = new LinkedList<>();
        HashMap<Alphabet, Set<StateCore>> state_map = new HashMap<>();
        for(Alphabet c : getAlphabet()){
            Set<StateCore> out = expandForwardFunction.apply(s, c, getIn_trans());
            if(out != null) {
                state_map.put(c, out);
                queue.addAll(out);
            }
        }
        if(!state_map.isEmpty())
            getTrans().put(s,state_map);
        return queue;
    }

    public Queue<StateCore> expandBackwards(StateCore s){
        Queue<StateCore> queue = new LinkedList<>();
        for(Alphabet c : getAlphabet()) {
            StateCore computed_state = expandBackwardsFunction.apply(s, c, getIn_trans());
            if (computed_state != null) {
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
            }
        }
        return queue;
    }

    /**
     * @return all reachable states
     */
    @Override
    public Set<StateCore> get_reachable_states() {
        Set<StateCore> reachable = new HashSet<>();

        Queue<StateCore> queue = new LinkedList<>();
        queue.addAll(getInit_states());

        while(!queue.isEmpty()){
            StateCore state = queue.remove();
            reachable.add(state);
            Map<Alphabet, Set<StateCore>> state_map = trans.get(state);
            if(state_map != null){
                for(Alphabet letter : state_map.keySet()){
                    for(StateCore state2 : state_map.get(letter)){
                        if(!reachable.contains(state2))
                            queue.add(state2);
                    }
                }
            }
        }
        return reachable;
    }

    @Override
    public void complete_aut() {
        Set<StateCore> reachable = get_reachable_states();
        StateCore deadState = createANewState();
        Map<Alphabet, Set<StateCore>> deadState_map = new HashMap<>();
        for(Alphabet letter : getAlphabet()){
            deadState_map.put(letter, new HashSet<>(Arrays.asList(deadState)));
        }

        boolean already_exists = true;

        for(StateCore s : reachable){
            Map<Alphabet, Set<StateCore>> state_map = trans.get(s);
            if(state_map == null){
                already_exists = false;
                state_map = new HashMap<>();
                trans.put(s, state_map);
            }
            for(Alphabet letter : getAlphabet()){
                Set<StateCore> state_letter_state = trans.get(s).get(letter);
                if(state_letter_state == null){
                    already_exists = false;
                    state_letter_state = new HashSet<>(Arrays.asList(deadState));
                    trans.get(s).put(letter, state_letter_state);
                }
            }
        }

        if(!already_exists){
            trans.put(deadState, deadState_map);
            getState_space().add(deadState);
        } else {
            getState_space().remove(deadState);
        }
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

    public NFA self_product(){
        Set<StateCore> input_init_states = getInit_states();
        Set<Pair<StateCore>> out_init_states = new HashSet<>();
        for(StateCore s : input_init_states){
            for(StateCore s2 : input_init_states){
                out_init_states.add(new Pair<>(s, s2));
            }
        }

        ExpandFunction<Pair<StateCore>, Alphabet, Map<StateCore, Map<Alphabet, Set<StateCore>>>, Set<Pair<StateCore>>> expand_product = (state, letter, inputTrans) -> {
            Set<Pair<StateCore>> out_states = new HashSet<>();
            StateCore left = state.getLeft();
            StateCore right = state.getRight();
            if(inputTrans.get(left) == null || inputTrans.get(left).get(letter) == null)
                return null;
            Set<StateCore> left_tran = inputTrans.get(left).get(letter);
            if(inputTrans.get(right) == null || inputTrans.get(right).get(letter) == null)
                return null;
            Set<StateCore> right_tran = inputTrans.get(right).get(letter);
            for(StateCore s1 : left_tran){
                for(StateCore s2 : right_tran){
                    out_states.add(new Pair<>(s1, s2));
                }
            }
            return out_states;
        };

        HasPropertyFunction<StateCore, Pair<StateCore>> NfaToSelfProductIsAcceptStateFunction = (inAccStates, state) -> {
            StateCore left = state.getLeft();
            StateCore right = state.getRight();
            return inAccStates.contains(left) && inAccStates.contains(right);
        };

        NFA product = new NFA(out_init_states, getAlphabet(), expand_product, NfaToSelfProductIsAcceptStateFunction, getTrans(), getAcc_states());
        product.expandForward();
        return product;
    }
    public Set<StateCore> get_states_that_can_lead_to_acceptance(){
        Set<StateCore> states_accessible_from_initial_states = new HashSet<>(get_reachable_states());

        Map<StateCore, Map<Alphabet, Set<StateCore>>> trans_reverse = new HashMap<>();
        for(StateCore s : getTrans().keySet()){
            for(Alphabet letter : getTrans().get(s).keySet()){
                for(StateCore s2 : getTrans().get(s).get(letter)){
                    Map<Alphabet, Set<StateCore>> current_state_map = trans_reverse.get(s2);
                    if(current_state_map == null){
                        current_state_map = new HashMap<>();
                        trans_reverse.put(s2, current_state_map);
                    }
                    Set<StateCore> current_state_letter_set = current_state_map.get(letter);
                    if(current_state_letter_set == null){
                        current_state_letter_set = new HashSet<>();
                        current_state_map.put(letter, current_state_letter_set);
                    }
                    current_state_letter_set.add(s);
                }
            }
        }

        Set<StateCore> states_accessible_from_acc_states = new HashSet<>();
        Queue<StateCore> queue2 = new LinkedList<>(getAcc_states());
        while (!queue2.isEmpty()){
            StateCore head = queue2.remove();
            if(states_accessible_from_acc_states.contains(head))
                continue;
            states_accessible_from_acc_states.add(head);
            Map<Alphabet, Set<StateCore>> state_map = trans_reverse.get(head);
            if(state_map != null){
                for(Alphabet letter : state_map.keySet()){
                    queue2.addAll(state_map.get(letter));
                }
            }
        }

        states_accessible_from_initial_states.retainAll(states_accessible_from_acc_states);
        return states_accessible_from_initial_states;
    }

    public void trim(){
        Set<StateCore> states_that_lead_to_accpetance = this.get_states_that_can_lead_to_acceptance();
        Iterator<StateCore> iter = this.getInit_states().iterator();
        while(iter.hasNext()){
            if(!states_that_lead_to_accpetance.contains(iter.next()))
                iter.remove();
        }

        Iterator<Map.Entry<StateCore, Map<Alphabet, Set<StateCore>>>> iter2 = this.getTrans().entrySet().iterator();
        while(iter2.hasNext()){
            Map.Entry<StateCore, Map<Alphabet, Set<StateCore>>> entry = iter2.next();
            StateCore state = entry.getKey();
            if(!states_that_lead_to_accpetance.contains(state)) {
                iter2.remove();
                continue;
            }

            Iterator<Map.Entry<Alphabet, Set<StateCore>>> iter3 = entry.getValue().entrySet().iterator();
            while(iter3.hasNext()){
                Map.Entry<Alphabet, Set<StateCore>> entry2 = iter3.next();
                Set<StateCore> tran_output = entry2.getValue();
                tran_output.retainAll(states_that_lead_to_accpetance);
                if(tran_output.size() == 0)
                    iter3.remove();
            }
            if(entry.getValue().size() == 0)
                iter2.remove();
        }
    }
    
}
