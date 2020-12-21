import java.util.*;

public class DFA<StateCore, Alphabet, InputTranKey, InputTranValue> extends Automaton<StateCore, Alphabet, StateCore, InputTranKey, InputTranValue>{
    StateCore init_state;

    public DFA(StateCore init_state, Set<Alphabet> alphabet, ExpandFunction<StateCore, Alphabet, Map<InputTranKey, Map<Alphabet, InputTranValue>>, StateCore> expandFunction, HasPropertyFunction<InputTranKey, StateCore> isAcceptStateFunction, Map<InputTranKey, Map<Alphabet, InputTranValue>> in_trans, Set<InputTranKey> in_acc_states) {
        super(alphabet, expandFunction, isAcceptStateFunction, in_trans, in_acc_states);
        this.init_state = init_state;
    }

    public StateCore getInit_state() {
        return init_state;
    }

    public void setInit_state(StateCore init_state) {
        this.init_state = init_state;
    }


    @Override
    public String toString(){
        return "Automaton{\n" +
                "init_states=" + init_state + super.toString();
    }

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

    @Override
    public void expandForward() {
        Queue<StateCore> queue = new LinkedList<StateCore>();
        Set<StateCore> expanded = new HashSet<StateCore>();
        queue.add(init_state);
        while(!queue.isEmpty()){
            StateCore s = queue.remove();
            if(!expanded.contains(s)) {
                queue.addAll(expandForward(s));
                isAcceptState(s);
            }
            expanded.add(s);
        }
    }

    @Override
    public Queue<StateCore> expandForward(StateCore s){
        Queue<StateCore> queue = new LinkedList<>();
        Set<StateCore> expanded = new HashSet<>();
        HashMap<Alphabet, StateCore> state_map = new HashMap<>();
        for(Alphabet c : getAlphabet()){
            StateCore out = expandForwardFunction.apply(s, c, getIn_trans());
            expanded.add(s);
            if(out != null) {
                state_map.put(c, out);
                queue.add(out);
            }
        }
        if(!state_map.isEmpty())
            getTrans().put(s,state_map);
        return queue;
    }

    @Override
    public Queue<StateCore> expandBackwards(StateCore s) {
        return null;
    }

    @Override
    public Set<StateCore> get_reachable() {
        Set<StateCore> reachable = new HashSet<>();

        Queue<StateCore> queue = new LinkedList<>();
        queue.add(init_state);

        while(!queue.isEmpty()){
            StateCore state = queue.remove();
            reachable.add(state);
            for(Alphabet letter : trans.get(state).keySet()){
                StateCore state2 = trans.get(state).get(letter);
                if(!reachable.contains(state2))
                    queue.add(state2);
            }
        }
        return reachable;
    }

    @Override
    public void complete_aut() {

    }

    @Override
    public void expandBackwards() {

    }
}
