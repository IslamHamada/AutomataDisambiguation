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
        state_space =  calcStateSpace();
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

    public boolean isInitialState(StateCore s){
        boolean isInitialState =  isInitialStateFunction.apply(getIn_init_states(), s);
        if(isInitialState)
            init_states.add(s);
        return isInitialState;
    }


    // TODO: might implement these two functions here instead of implementing them in the subclasses
    public abstract void expandForward();
    public abstract void expandBackwards();
    public abstract Queue<TransitionOutput> expandForward(StateCore s);
    public abstract Queue<StateCore> expandBackwards(StateCore s);

    @Override
    public String toString() {
         return "\n, alphabet=" + alphabet +
                "\n, acc_states=" + acc_states +
                "\n, trans=" + trans +
                "\n}";
    }

    public Map<InputStateCore, Map<Alphabet, InputTranOutput>> getIn_trans() {
        return in_trans;
    }

    public void setIn_trans(Map<InputStateCore, Map<Alphabet, InputTranOutput>> in_trans) {
        this.in_trans = in_trans;
    }

    public abstract Set<StateCore> get_reachable();

    public abstract void complete_aut();

    public StateCore generateUniqueStateCore(){
        Set<StateCore> state_space = getState_space();
        StateCore state = state_space.iterator().next();
        String class_name = state.getClass().getSimpleName();
        if(class_name.equals("Integer")){
            int max = 0;
            for(StateCore s : state_space){
                Integer s2 = (Integer)s;
                max = (s2 > max)? s2 : max;
            }
            StateCore new_value = (StateCore)(Integer)(max + 1);
            return new_value;
        } else if(class_name.equals("Character")){
            int max = 0;
            for(StateCore s : state_space){
                Character s2 = (Character)s;
                int s3 = s2.charValue();
                max = (s2 > max)? s2 : max;
            }
            StateCore new_value = (StateCore)(Character)(char)(max + 1);
            return new_value;
        }
        return null;
    }
}
