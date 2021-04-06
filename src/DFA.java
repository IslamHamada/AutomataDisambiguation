import java.util.*;

/**
 * a deterministic finite automaton class
 * @param <StateCore>
 * @param <Alphabet>
 * @param <InputTranKey> the transition key type of the transition map of the input automaton, if provided
 * @param <InputTranValue> the transition value type of the transition map of the input automaton, if provided
 */
public class DFA<StateCore, Alphabet, InputTranKey, InputTranValue> extends Automaton<StateCore, Alphabet, StateCore, InputTranKey, InputTranValue>{
    StateCore init_state;

    /**
     * a constructor to build a DFA given another input automaton, i.e., initial states, input transitions, an expansion function and a function to check whether a state is an acceptance state
     * @param init_state
     * @param alphabet
     * @param expandFunction a function to expand the states forward
     * @param isAcceptStateFunction a function to check whether a state is an acceptance state or not
     * @param in_trans the input transitions of the input automaton
     * @param in_acc_states the acceptance states of the input automaton
     */
    public DFA(StateCore init_state, Set<Alphabet> alphabet, ExpandFunction<StateCore, Alphabet, Map<InputTranKey, Map<Alphabet, InputTranValue>>, StateCore> expandFunction, HasPropertyFunction<InputTranKey, StateCore> isAcceptStateFunction, Map<InputTranKey, Map<Alphabet, InputTranValue>> in_trans, Set<InputTranKey> in_acc_states) {
        super(alphabet, expandFunction, isAcceptStateFunction, in_trans, in_acc_states);
        this.init_state = init_state;
    }

    public StateCore getInit_state() {
        return init_state;
    }

    @Override
    public Set<StateCore> getInit_states(){
        return new HashSet<>(Arrays.asList(init_state));
    }

    public void setInit_state(StateCore init_state) {
        this.init_state = init_state;
    }

    @Override
    public String toString(){
        return "Automaton{\n" +
                "init_states=" + init_state + super.toString();
    }

    /**
     * @return returns the state space of the automaton
     */
    @Override
    protected Set<StateCore> calcStateSpace() {
        Set<StateCore> state_space = new HashSet<>();
        state_space.add(init_state);

        for(StateCore state : trans.keySet()){
            for(Alphabet letter : trans.get(state).keySet()){
                state_space.add(trans.get(state).get(letter));
            }
        }

        return state_space;
    }

    @Override
    public boolean isInitialState(StateCore s) {
        return false;
    }

    /**
     * a function to expand a state forward
     * @param s a state
     * @return the states resulting from expansion
     */
    @Override
    public Queue<StateCore> expandForward(StateCore s){
        Queue<StateCore> queue = new LinkedList<>();
        HashMap<Alphabet, StateCore> state_map = new HashMap<>();
        for(Alphabet c : getAlphabet()){
            StateCore out = expandForwardFunction.apply(s, c, getIn_trans());
            if(out != null) {
                state_map.put(c, out);
                queue.add(out);
            }
        }
        if(!state_map.isEmpty())
            getTrans().put(s,state_map);
        return queue;
    }

    /**
     * a function to expand a state backwards
     * @param s a state
     * @return the states resulting from expansion
     */
    @Override
    public Queue<StateCore> expandBackwards(StateCore s) {
        return null;
    }

    /**
     * @return all reachable states
     */
    @Override
    public Set<StateCore> get_reachable_states() {
        Set<StateCore> reachable = new HashSet<>();

        Queue<StateCore> queue = new LinkedList<>();
        queue.add(init_state);

        while(!queue.isEmpty()){
            StateCore state = queue.remove();
            reachable.add(state);
            Map<Alphabet, StateCore> state_map = trans.get(state);
            if(state_map != null) {
                for(Alphabet letter : state_map.keySet()) {
                    StateCore state2 = state_map.get(letter);
                    if (!reachable.contains(state2))
                        queue.add(state2);
                }
            }
        }
        return reachable;
    }

    /**
     * completes the automata by adding a dead state, if missing, and proper transitions to it
     */
    @Override
    public void complete_aut() {
        Set<StateCore> reachable = get_reachable_states();
        StateCore deadState = createANewState();
        Map<Alphabet, StateCore> deadState_map = new HashMap<>();
        for(Alphabet letter : getAlphabet()){
            deadState_map.put(letter, deadState);
        }

        boolean already_exists = true;

        for(StateCore s : reachable){
            Map<Alphabet, StateCore> state_map = trans.get(s);
            if(state_map == null){
                already_exists = false;
                state_map = new HashMap<>();
                trans.put(s, state_map);
            }
            for(Alphabet letter : getAlphabet()){
                StateCore state_letter_state = trans.get(s).get(letter);
                if(state_letter_state == null){
                    already_exists = false;
                    state_letter_state = deadState;
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

    @Override
    public void trim() {
        Set<StateCore> states_that_lead_to_accpetance = this.get_states_that_can_lead_to_acceptance();
        Iterator<StateCore> iter = this.getInit_states().iterator();
        while(iter.hasNext()){
            if(!states_that_lead_to_accpetance.contains(iter.next()))
                iter.remove();
        }

        Iterator<Map.Entry<StateCore, Map<Alphabet, StateCore>>> iter2 = this.getTrans().entrySet().iterator();
        while(iter2.hasNext()){
            Map.Entry<StateCore, Map<Alphabet, StateCore>> entry = iter2.next();
            StateCore state = entry.getKey();
            if(!states_that_lead_to_accpetance.contains(state)) {
                iter2.remove();
                continue;
            }

            Iterator<Map.Entry<Alphabet, StateCore>> iter3 = entry.getValue().entrySet().iterator();
            while(iter3.hasNext()){
                Map.Entry<Alphabet, StateCore> entry2 = iter3.next();
                StateCore tran_output = entry2.getValue();
                if(!states_that_lead_to_accpetance.contains(tran_output))
                    iter3.remove();
            }
            if(entry.getValue().size() == 0)
                iter2.remove();
        }
    }

    @Override
    public Set<StateCore> get_states_that_can_lead_to_acceptance() {
        Set<StateCore> states_accessible_from_initial_states = new HashSet<>(get_reachable_states());

        Map<StateCore, Map<Alphabet, Set<StateCore>>> trans_reverse = new HashMap<>();
        for(StateCore s : getTrans().keySet()){
            for(Alphabet letter : getTrans().get(s).keySet()){
                StateCore s2 = getTrans().get(s).get(letter);
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
}
