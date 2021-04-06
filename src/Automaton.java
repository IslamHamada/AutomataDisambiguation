import java.util.*;

/**
 *
 * @param <StateCore> the type of all states
 * @param <Alphabet> the type of all letters in the transitions
 * @param <TransitionOutput> the type of all transitions output
 * @param <InputStateCore> the type of the state of an input automaton, if any is needed
 * @param <InputTranOutput> the type of transitions output of the input automaton, if any is needed
 */
public abstract class Automaton<StateCore, Alphabet, TransitionOutput, InputStateCore, InputTranOutput>{
    Set<Alphabet> alphabet;
    Set<StateCore> acc_states;
    Map<StateCore, Map<Alphabet, TransitionOutput>> trans;

    /**
     * a function used to expand the automaton forward given an input automaton, if provided
     */
    ExpandFunction<StateCore, Alphabet, Map<InputStateCore, Map<Alphabet, InputTranOutput>>, TransitionOutput> expandForwardFunction;

    /**
     * a function used to check if a state is an accept state given an input automaton, if provided
     */
    HasPropertyFunction<InputStateCore, StateCore> isAcceptStateFunction;

    /**
     * a function used to expand the automaton backwards given an input automaton, if provided
     */
    ExpandFunction<StateCore, Alphabet, Map<InputStateCore, Map<Alphabet, InputTranOutput>>, StateCore> expandBackwardsFunction;

    /**
     * a function used to check if a state is an initial state given an input automaton, if provided
     */
    HasPropertyFunction<InputStateCore, StateCore> isInitialStateFunction;

    /**
     * the initial states of an input automaton, if provided
     */
    private Set<InputStateCore> in_init_states;

    /**
     * the transitions map of an input automaton, if provided
     */
    private Map<InputStateCore, Map<Alphabet, InputTranOutput>> in_trans;

    /**
     * the acceptance states of an input automaton, if provided
     */
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

    /**
     * a constructor to create an automaton given another automaton as an input by expanding forward
     * @param alphabet
     * @param expandFunction a function to expand the states forward
     * @param isAcceptStateFunction a function to check whether a state is an acceptance state or not
     * @param in_trans the input transitions of the input automaton
     * @param in_acc_states the acceptance states of the input automaton
     */
    public Automaton(Set<Alphabet> alphabet, ExpandFunction<StateCore, Alphabet, Map<InputStateCore, Map<Alphabet, InputTranOutput>>, TransitionOutput> expandFunction, HasPropertyFunction<InputStateCore, StateCore> isAcceptStateFunction, Map<InputStateCore, Map<Alphabet, InputTranOutput>> in_trans, Set<InputStateCore> in_acc_states) {
        trans = new HashMap<>();
        acc_states = new HashSet<>();

        this.alphabet = alphabet;
        this.expandForwardFunction = expandFunction;
        this.isAcceptStateFunction = isAcceptStateFunction;
        this.in_trans = in_trans;
        this.in_acc_states = in_acc_states;
    }

    /**
     * a constructor to create an automaton given another automaton as an input by expanding backward
     * @param alphabet
     * @param acc_states acceptance states
     * @param expandBackwardsFunction a function to expand the states backwards
     * @param isInitialStateFunction a function to check whether a state is an initial state
     * @param in_init_states the initial states of the input automaton
     * @param in_trans the transitions of the input automaton
     */
    public Automaton(Set<Alphabet> alphabet, Set<StateCore> acc_states, ExpandFunction<StateCore, Alphabet, Map<InputStateCore, Map<Alphabet, InputTranOutput>>, StateCore> expandBackwardsFunction, HasPropertyFunction<InputStateCore, StateCore> isInitialStateFunction, Set<InputStateCore> in_init_states, Map<InputStateCore, Map<Alphabet, InputTranOutput>> in_trans) {
        trans = new HashMap<>();

        this.acc_states = acc_states;
        this.alphabet = alphabet;
        this.expandBackwardsFunction = expandBackwardsFunction;
        this.isInitialStateFunction = isInitialStateFunction;
        this.in_trans = in_trans;
        this.in_init_states = in_init_states;
    }

    /**
     * a constructor to create an automaton given the alphabet, acceptance states and the transitions
     * @param alphabet
     * @param acc_states
     * @param trans
     */
    public Automaton(Set<Alphabet> alphabet, Set<StateCore>acc_states, Map<StateCore, Map<Alphabet, TransitionOutput>> trans){
        this.acc_states = acc_states;
        this.alphabet = alphabet;
        this.trans = trans;
    }

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

    /**
     * @return returns the state space of the automaton
     */
    public Set<StateCore> getState_space() {
        if(state_space == null) {
            state_space = calcStateSpace();
        }
        return state_space;
    }

    /**
     * @return the output of calculating the state space
     */
    protected abstract Set<StateCore> calcStateSpace();

    public void setState_space(Set<StateCore> state_space) {
        this.state_space = state_space;
    }

    /**
     * Use only if isAcceptStateFunction is provided
     * @param s a state
     * @return whether the state is an acceptance state
     */
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

    /**
     * a function to expand a state forward
     * @param s a state
     * @return the states resulting from expansion
     */
    public abstract Queue<StateCore> expandForward(StateCore s);

    /**
     * a function to expand a state backwards
     * @param s a state
     * @return the states resulting from expansion
     */
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

    /**
     * @return all reachable states
     */
    public abstract Set<StateCore> get_reachable_states();

    /**
     * completes the automata by adding a dead state, if missing, and proper transitions to it
     */
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

        //in case the state core is of type Character
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
        //ToDo: define how the new state core is generated for other types
        return null;
    }

    public abstract void trim();

    public abstract Set<StateCore> get_states_that_can_lead_to_acceptance();
}
