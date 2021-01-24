import java.util.*;

public abstract class Automaton<StateCore, Alphabet, TransitionOutput, InputStateCore, InputTranOutput>{
    Set<Alphabet> alphabet;
    Set<StateCore> acc_states;
    Map<StateCore, Map<Alphabet, TransitionOutput>> trans;

    ExpandFunction<StateCore, Alphabet, Map<InputStateCore, Map<Alphabet, InputTranOutput>>, TransitionOutput> expandForwardFunction;
    // TODO: 11.12.2020 might need to create a new generic type for the state core of the input automaton instead of using InputTranKey
    HasPropertyFunction<InputStateCore, StateCore> isAcceptStateFunction;

    ExpandFunction<StateCore, Alphabet, Map<InputStateCore, Map<Alphabet, InputTranOutput>>, StateCore> expandBackwardsFunction;
    // TODO: 11.12.2020 might need to create a new generic type for the state core of the input automaton instead of using InputTranKey
    HasPropertyFunction<InputStateCore, StateCore> isInitialStateFunction;

    private Set<InputStateCore> in_init_states;

    // TODO: 11.12.2020 might need to create a new generic type for the state core of the input automaton instead of using InputTranKey
    private Map<InputStateCore, Map<Alphabet, InputTranOutput>> in_trans;
    private Set<InputStateCore> in_acc_states;

    private Set<StateCore> state_space;

    public Map<StateCore, Map<Alphabet, TransitionOutput>> getTrans() {
        return trans;
    }

    public void setTrans(Map<StateCore, Map<Alphabet, TransitionOutput>> trans) {
        this.trans = trans;
    }

    public Automaton(){

    }

    public Automaton(Set<Alphabet> alphabet, ExpandFunction<StateCore, Alphabet, Map<InputStateCore, Map<Alphabet, InputTranOutput>>, TransitionOutput> expandFunction, HasPropertyFunction<InputStateCore, StateCore> isAcceptStateFunction, Map<InputStateCore, Map<Alphabet, InputTranOutput>> in_trans, Set<InputStateCore> in_acc_states) {
        trans = new HashMap<>();
        acc_states = new HashSet<>();

        this.alphabet = alphabet;
        this.expandForwardFunction = expandFunction;
        this.isAcceptStateFunction = isAcceptStateFunction;
        this.in_trans = in_trans;
        this.in_acc_states = in_acc_states;
    }

    public Automaton(Set<Alphabet> alphabet, Set<StateCore> acc_states, ExpandFunction<StateCore, Alphabet, Map<InputStateCore, Map<Alphabet, InputTranOutput>>, StateCore> expandBackwardsFunction, HasPropertyFunction<InputStateCore, StateCore> isInitialStateFunction, Set<InputStateCore> in_init_states, Map<InputStateCore, Map<Alphabet, InputTranOutput>> in_trans) {
        trans = new HashMap<>();

        this.acc_states = acc_states;
        this.alphabet = alphabet;
        this.expandBackwardsFunction = expandBackwardsFunction;
        this.isInitialStateFunction = isInitialStateFunction;
        this.in_trans = in_trans;
        this.in_init_states = in_init_states;
    }

    public Automaton(Set<Alphabet> alphabet, Set<StateCore>acc_states, Map<StateCore, Map<Alphabet, TransitionOutput>> trans){
//        this.init_states = init_states;
        this.acc_states = acc_states;
        this.alphabet = alphabet;
        this.trans = trans;
    }

//    public Set<StateCore> getInit_states() {
//        return init_states;
//    }

//    public void setInit_states(Set<StateCore> init_states) {
//        this.init_states = init_states;
//    }

    public Set<StateCore> getAcc_states() {
        return acc_states;
    }

    public void setAcc_states(Set<StateCore> acc_states) {
        this.acc_states = acc_states;
    }

    public Set<Alphabet> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Set<Alphabet> alphabet) {
        this.alphabet = alphabet;
    }

    public Set<InputStateCore> getIn_acc_states() {
        return in_acc_states;
    }

    public void setIn_acc_states(Set<InputStateCore> in_acc_states) {
        this.in_acc_states = in_acc_states;
    }

    public Set<InputStateCore> getIn_init_states() {
        return in_init_states;
    }

    public void setIn_init_states(Set<InputStateCore> in_init_states) {
        this.in_init_states = in_init_states;
    }

    public Set<StateCore> getState_space() {
        if(state_space == null) {
            state_space = calcStateSpace();
        }
        return state_space;
    }

    protected abstract Set<StateCore> calcStateSpace();

    public void setState_space(Set<StateCore> state_space) {
        this.state_space = state_space;
    }

    public boolean isAcceptState(StateCore s){
        boolean isAcceptState =  isAcceptStateFunction.apply(getIn_acc_states(), s);
        if(isAcceptState)
            acc_states.add(s);
        return isAcceptState;
    }

    public abstract boolean isInitialState(StateCore s);

    public abstract Set<StateCore> getInit_states();

    /**
     * a function to expand the automaton forward
     */
    public void expandForward(){
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

    /**
     * a function to expand the automaton backwards
     */
    public void expandBackwards(){
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
    public abstract Queue<StateCore> expandForward(StateCore s);
    public abstract Queue<StateCore> expandBackwards(StateCore s);

    @Override
    public String toString() {
        String output = "\n, alphabet=" + alphabet +
                "\n, acc_states=" + acc_states +
                "\n, trans=\n";
        Iterator<Map.Entry<StateCore, Map<Alphabet, TransitionOutput>>>iter1 = trans.entrySet().iterator();
        while(iter1.hasNext()){
            Map.Entry<StateCore, Map<Alphabet, TransitionOutput>> entry = iter1.next();
            output += "\t\t" + entry;
            if(iter1.hasNext())
                output += "\n";
        }
        output += "\n}";
        return output;
    }

    public Map<InputStateCore, Map<Alphabet, InputTranOutput>> getIn_trans() {
        return in_trans;
    }

    public void setIn_trans(Map<InputStateCore, Map<Alphabet, InputTranOutput>> in_trans) {
        this.in_trans = in_trans;
    }

    public abstract Set<StateCore> get_reachable_states();

    public abstract void complete_aut();

    /**
     * @return create a new state with a unique StateCore and adds it to the automaton
     */
    public StateCore createANewState(){
        Set<StateCore> state_space = getState_space();
        StateCore state = state_space.iterator().next();
        String class_name = state.getClass().getSimpleName();

        //in case the state core is an integer
        if(class_name.equals("Integer")){
            int max = 0;
            for(StateCore s : state_space){
                Integer s2 = (Integer)s;
                max = (s2 > max)? s2 : max;
            }
            StateCore new_value = (StateCore)(Integer)(max + 1);
            state_space.add(new_value);
            return new_value;
        } else if(class_name.equals("Character")){
            int max = 0;
            for(StateCore s : state_space){
                Character s2 = (Character)s;
                int s3 = s2.charValue();
                max = (s2 > max)? s2 : max;
            }
            StateCore new_value = (StateCore)(Character)(char)(max + 1);
            state_space.add(new_value);
            return new_value;
        }
        return null;
    }
}
