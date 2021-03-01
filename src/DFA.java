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
}
