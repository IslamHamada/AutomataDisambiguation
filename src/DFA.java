import java.util.*;

public class DFA<StateCore, Alphabet, InputTranKey, InputTranValue> extends Automaton<StateCore, Alphabet, StateCore, InputTranKey, InputTranValue>{

    public DFA(StateCore init_state, Set<Alphabet> alphabet, ExpandFunction<StateCore, Alphabet, Map<InputTranKey, Map<Alphabet, InputTranValue>>, StateCore> expandFunction, HasPropertyFunction<InputTranKey, StateCore> isAcceptStateFunction, Map<InputTranKey, Map<Alphabet, InputTranValue>> in_trans, Set<InputTranKey> in_acc_states) {
        super(new HashSet<StateCore>(Arrays.asList(init_state)), alphabet, expandFunction, isAcceptStateFunction, in_trans, in_acc_states);
    }

    public StateCore getInit_state(){
        return getInit_states().iterator().next();
    }

    @Override
    public void expandForward() {
        Queue<StateCore> queue = new LinkedList<StateCore>();
        Set<StateCore> expanded = new HashSet<StateCore>();
        queue.addAll(getInit_states());
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
    public void expandBackwards() {

    }
}
