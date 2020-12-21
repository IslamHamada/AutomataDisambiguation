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
    public void complete_aut() {
        Set<StateCore> reachable = get_reachable();
        StateCore deadState = generateUniqueStateCore();
        Map<Alphabet, Set<Set<StateCore>>> deadState_map = new HashMap<>();
        for(Alphabet letter : getAlphabet()){
            deadState_map.put(letter, new HashSet<>(Arrays.asList(new HashSet<>(Arrays.asList(deadState)))));
        }
        trans.put(deadState, deadState_map);
        getState_space().add(deadState);

        for(StateCore s : reachable){
            Map<Alphabet, Set<Set<StateCore>>> state_map = trans.get(s);
            if(state_map == null){
                state_map = new HashMap<>();
                trans.put(s, state_map);
            }
            for(Alphabet letter : getAlphabet()){
                Set<Set<StateCore>> state_letter_set = trans.get(s).get(letter);
                if(state_letter_set == null){
                    state_letter_set = new HashSet<>(Arrays.asList(new HashSet<>(Arrays.asList(deadState))));
                    trans.get(s).put(letter, state_letter_set);
                }
            }
        }
    }

    @Override
    public boolean isInitialState(StateCore s) {
        return false;
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

    @Override
    public Set<StateCore> calcStateSpace(){
        Set<StateCore> state_space = new HashSet<>();
        for(StateCore state : init_states){
            state_space.add(state);
        }

        for(StateCore state : trans.keySet()){
            for(Alphabet letter : trans.get(state).keySet()){
                for(Set<StateCore> set : trans.get(state).get(letter)){
                    state_space.addAll(set);
                }
            }
        }

        return state_space;
    }


    public void convertToSingleInitialState(){
        if(getInit_states().size() == 1)
            return;

        Set<StateCore> initial_states = getInit_states();

        Iterator<StateCore> iter = initial_states.iterator();
        StateCore first = iter.next();
        Map<Alphabet, Set<Set<StateCore>>> first_trans = getTrans().get(first);

        StateCore new_state = generateUniqueStateCore();
        Map<Alphabet, Set<Set<StateCore>>> new_state_map = new HashMap<>();

        //copy the transitions of the first state of the disjunction to the new state
        for(Alphabet letter : first_trans.keySet()){
            Set<Set<StateCore>> new_state_letter_map = new HashSet<>();
            for(Set<StateCore> set : first_trans.get(letter)){
                new_state_letter_map.add(new HashSet<>(set));
            }
            new_state_map.put(letter, new_state_letter_map);
        }

        //merge the other states of the disjunction
        while (iter.hasNext()) {
            StateCore state = iter.next();
            Map<Alphabet, Set<Set<StateCore>>> state_trans = getTrans().get(state);

            for (Alphabet letter : state_trans.keySet()) {
                Set<Set<StateCore>> state_letter_tran = state_trans.get(letter);
                Set<Set<StateCore>> new_state_letter_tran = new_state_map.get(letter);
                if (new_state_letter_tran == null) {
                    new_state_letter_tran = new HashSet<>();
                    new_state_map.put(letter, new_state_letter_tran);
                }
                new_state_letter_tran.addAll(state_letter_tran);
            }
        }

        //ToDo: remove the intermediate new states in initial_states2. some of them won't ever be mentioned while expanding

        getState_space().add(new_state);
        getTrans().put(new_state, new_state_map);
        Set<StateCore> new_initial_state = new HashSet<>();
        new_initial_state.add(new_state);
        setInit_states(new_initial_state);
    }

    public AFA complement(){
        convertToSingleInitialState();
        complete_aut();

        Map<StateCore, Map<Alphabet, Set<Set<StateCore>>>> trans_map = new HashMap<>();

        for(StateCore s : trans.keySet()){
            Map<Alphabet, Set<Set<StateCore>>> state_map = new HashMap<>();
            for(Alphabet letter : trans.get(s).keySet()){
                Set<Set<StateCore>> sets = trans.get(s).get(letter);
                List<List<StateCore>> listOfLists = new ArrayList<>();
                for(Set<StateCore> set : sets)
                    listOfLists.add(new ArrayList<>(set));
                Set<Set<StateCore>> state_letter_set = new HashSet<>(allSelections(listOfLists));
                state_map.put(letter, state_letter_set);
            }
            trans_map.put(s, state_map);
        }

        Set<StateCore> acc_states = new HashSet<>(getState_space());
        acc_states.removeAll(getAcc_states());
        AFA output = new AFA(init_states, alphabet, acc_states, trans_map);
        return output;
    }

    public List<Set<StateCore>> allSelections(List<List<StateCore>> listOfLists) {
        int numOfLists = listOfLists.size();
        int[] indices = new int[numOfLists];
        List<Set<StateCore>> output = new ArrayList<>();

        boolean found = true;
        while(found){
            Set<StateCore> set = new HashSet<>();
            for(int i = 0; i < numOfLists; i++){
                set.add(listOfLists.get(i).get(indices[i]));
            }
            output.add(set);

            found = false;

            //update the indices
            for(int i = numOfLists - 1; i >= 0 && !found; i--){
                indices[i]++;
                if(indices[i] >= listOfLists.get(i).size()) {
                    indices[i] = 0;
                } else {
                    found = true;
                }
            }
        }
        return output;
    }
    public static <StateCore, Alphabet> Set<Set<StateCore>> configTranFunction(Set<StateCore> setOfStates, Alphabet letter, Map<StateCore, Map<Alphabet, Set<Set<StateCore>>>> trans) {
        List<List<Set<StateCore>>> listOfLists = new ArrayList<>();
        for(StateCore s: setOfStates){
            Set<Set<StateCore>> state_letter_set = trans.get(s).get(letter);
            if(state_letter_set == null)
                return null;
            List<Set<StateCore>> state_letter_list = new ArrayList<>(state_letter_set);
            listOfLists.add(state_letter_list);
        }

        int numOfLists = listOfLists.size();
        int[] indices = new int[numOfLists];
        Set<Set<StateCore>> output = new HashSet<>();

        boolean found = true;
        while(found){
            Set<StateCore> set = new HashSet<>();
            for(int i = 0; i < numOfLists; i++){
                set.addAll(listOfLists.get(i).get(indices[i]));
            }
            output.add(set);

            found = false;

            //update the indices
            for(int i = numOfLists - 1; i >= 0 && !found; i--){
                indices[i]++;
                if(indices[i] >= listOfLists.get(i).size()) {
                    indices[i] = 0;
                } else {
                    found = true;
                }
            }
        }
        return output;
    }
}
