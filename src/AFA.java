import java.util.*;

public class AFA<StateCore, Alphabet, InputStateCore, InputTransitionOutput
        > extends Automaton<StateCore, Alphabet, Set<Set<StateCore>>, InputStateCore, InputTransitionOutput>{
    Set<StateCore> init_states;

    public AFA(Set<StateCore> init_states, Set<Alphabet> alphabet, Set<StateCore> acc_states, Map<StateCore, Map<Alphabet, Set<Set<StateCore>>>> trans) {
        super(alphabet, acc_states, trans);
        this.init_states = init_states;
    }

    public Set<StateCore> getInit_states() {
        return init_states;
    }

    public void setInit_states(Set<StateCore> init_states) {
        this.init_states = init_states;
    }

    @Override
    public String toString(){
        return "Automaton{\n" +
                "init_states=" + init_states + super.toString();
    }

    @Override
    public Set<StateCore> get_reachable() {
        //ToDo: can be more strict. because the universal branching can cause clashses
        Set<StateCore> reachable = new HashSet<>();

        Queue<StateCore> queue = new LinkedList<>();
        for(StateCore s : init_states){
            queue.add(s);
        }

        while(!queue.isEmpty()){
            StateCore state = queue.remove();
            reachable.add(state);
            for(Alphabet letter : trans.get(state).keySet()){
                for(Set<StateCore> set : trans.get(state).get(letter)){
                    for(StateCore s : set){
                        if(!reachable.contains(s))
                            queue.add(s);
                    }
                }
            }
        }
        return reachable;
    }

    @Override
    public void expandForward() {
    }

    @Override
    public void expandBackwards() {

    }

    @Override
    public Queue<StateCore> expandForward(StateCore s) {
        return null;
    }

    @Override
    public Queue<StateCore> expandBackwards(StateCore s) {
        return null;
    }

    public NFA reverseDeterminize(){
        Set<Set<StateCore>> acc_states = new HashSet<Set<StateCore>>();
        Set<StateCore> acc_state = new HashSet<StateCore>(getAcc_states());
        acc_states.add(acc_state);

        Set<Alphabet> alphabet = getAlphabet();
        Map in_trans = getTrans();

        ExpandFunction<Set<StateCore>, Alphabet, Map<StateCore, Map<Alphabet, Set<Set<StateCore>>>>, Set<StateCore>> AFAtoNFAExpand = (state, letter, in_tran) -> {
            HashSet<StateCore> outputState = new HashSet<StateCore>();
            for(StateCore s : in_tran.keySet()){
                Set<Set<StateCore>> tran_out = in_tran.get(s).get(letter);
                if(tran_out != null){
                    for(Set<StateCore> set : tran_out){
                        if(state.containsAll(set))
                            outputState.add(s);
                    }
                }
            }
            if(!outputState.isEmpty())
                return outputState;
            else
                return null;
        };

        HasPropertyFunction<StateCore, Set<StateCore>> AFAToNFAIsInitialStateFunction = (inInitStates, state) -> {
            for(StateCore s : state){
                if(inInitStates.contains(s))
                    return true;
            }
            return false;
        };

        NFA A_out = new NFA(alphabet, acc_states, AFAtoNFAExpand, AFAToNFAIsInitialStateFunction, getInit_states(), in_trans);
        A_out.expandBackwards();

        return A_out;
    }
}
