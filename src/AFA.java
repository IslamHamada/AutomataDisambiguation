import java.util.*;

/**
 * a class for alternating finite automata
 * @param <StateCore>
 * @param <Alphabet>
 * @param <InputStateCore> the state core of the input automaton, if provided
 * @param <InputTranOutput> the output of the input transition function of the input automaton, if provided
 */
public class AFA<StateCore, Alphabet, InputStateCore, InputTranOutput
        > extends Automaton<StateCore, Alphabet, Set<Set<StateCore>>, InputStateCore, InputTranOutput>{
    Set<StateCore> init_states;
    AFA<StateCore, Alphabet, InputStateCore, InputTranOutput> complement; // a variable to store the complement of the automaton which is another AFA

    /**
     * a constructor to build an AFA given the initial states, alphabet, acceptance states, and the transition function
     * @param init_states
     * @param alphabet
     * @param acc_states
     * @param trans
     */
    public AFA(Set<StateCore> init_states, Set<Alphabet> alphabet, Set<StateCore> acc_states, Map<StateCore, Map<Alphabet, Set<Set<StateCore>>>> trans) {
        super(alphabet, acc_states, trans);
        this.init_states = init_states;
    }

    @Override
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
    public Set<StateCore> get_reachable_states() {
        //ToDo: can be more strict. because the universal branching can cause clashses
        Set<StateCore> reachable = new HashSet<>();

        Queue<StateCore> queue = new LinkedList<>(init_states);

        while(!queue.isEmpty()){
            StateCore state = queue.remove();
            reachable.add(state);
            Map<Alphabet, Set<Set<StateCore>>> state_map = trans.get(state);
            if(state_map != null) {
                for (Alphabet letter : state_map.keySet()) {
                    for (Set<StateCore> set : state_map.get(letter)) {
                        for (StateCore s : set) {
                            if (!reachable.contains(s))
                                queue.add(s);
                        }
                    }
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
        Map<Alphabet, Set<Set<StateCore>>> deadState_map = new HashMap<>();
        for(Alphabet letter : getAlphabet()){
            deadState_map.put(letter, new HashSet<>(Arrays.asList(new HashSet<>(Arrays.asList(deadState)))));
        }

        boolean already_exists = true;

        for(StateCore s : reachable){
            Map<Alphabet, Set<Set<StateCore>>> state_map = trans.get(s);
            if(state_map == null){
                already_exists = false;
                state_map = new HashMap<>();
                trans.put(s, state_map);
            }
            for(Alphabet letter : getAlphabet()){
                Set<Set<StateCore>> state_letter_set = trans.get(s).get(letter);
                if(state_letter_set == null){
                    already_exists = false;
                    state_letter_set = new HashSet<>(Arrays.asList(new HashSet<>(Arrays.asList(deadState))));
                    trans.get(s).put(letter, state_letter_set);
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
    public boolean isInitialState(StateCore s) {
        return false;
    } // this function isn't needed so far

    @Override
    public Queue<StateCore> expandForward(StateCore s) {
        return null;
    } // this function isn't needed so far

    @Override
    public Queue<StateCore> expandBackwards(StateCore s) {
        return null;
    } // this function isn't needed so far

    /**
     * a function that applies the algorithm reverse determinization
     * @return an NFA
     */
    public NFA reverseDeterminize(){
        Set<Set<StateCore>> acc_states = new HashSet<>();
        Set<StateCore> acc_state = new HashSet<>(getAcc_states());
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
            Map<Alphabet, Set<Set<StateCore>>> trans_state_map = trans.get(state);
            if(trans_state_map != null) {
                for (Alphabet letter : trans_state_map.keySet()) {
                    for (Set<StateCore> set : trans.get(state).get(letter)) {
                        state_space.addAll(set);
                    }
                }
            }
        }

        return state_space;
    }

    //Todo: the function elimintates the disjunction in the initial state. Time to eliminate the conjunction
    //Todo: make sure to write a comprehensive test
//    public void convertToSingleInitialState(){
//        if(getInit_states().size() == 1 && getInit_states().iterator().next().size() == 1)
//            return;
//
//        Set<StateCore> initial_states = getInit_states();
//
//        Set<Set<StateCore>> initial_states = getInit_states();
//
//        Set<Set<StateCore>> initial_states2 = new HashSet<>();
////        a loop to merge the states of each conjunction into one single state
//        for (Set<StateCore> conjunction : initial_states) {
//            StateCore new_state = generateUniqueStateCore();
//            if (conjunction.size() > 1) {
//                Iterator<StateCore> iter = conjunction.iterator();
//                StateCore first = iter.next();
//                Map<Alphabet, Set<Set<StateCore>>> first_trans = getTrans().get(first);
//                if(first_trans == null)
//                    continue;
//                Map<Alphabet, Set<Set<StateCore>>> new_state_map = new HashMap<>();
//
////                copy the transitions of the first state of the conjunction to the new state
//                for(Alphabet letter : first_trans.keySet()){
//                    Set<Set<StateCore>> new_state_letter_map = new HashSet<>();
//                    for(Set<StateCore> set : first_trans.get(letter)){
//                        new_state_letter_map.add(new HashSet<>(set));
//                    }
//                    new_state_map.put(letter, new_state_letter_map);
//                }
//
////                merge the other states of the conjunction in the new state
//                while (iter.hasNext()) {
//                    StateCore state = iter.next();
//                    Map<Alphabet, Set<Set<StateCore>>> state_trans = getTrans().get(state);
//
////                    merge for each letter
//                    for (Alphabet letter : state_trans.keySet()) {
//                        Set<Set<StateCore>> state_letter_tran = state_trans.get(letter);
//                        Set<Set<StateCore>> new_state_letter_tran = new_state_map.get(letter);
//
//                        if (new_state_letter_tran == null)
//                            continue;
//
//                        Set<Set<StateCore>> new_tran = new HashSet<>();
//                        for (Set<StateCore> s1 : state_letter_tran) {
//                            for (Set<StateCore> s2 : new_state_letter_tran) {
//                                Set<StateCore> s3 = new HashSet<>(s1);
//                                s3.addAll(s2);
//                                new_tran.add(s3);
//                            }
//                        }
//                        new_state_map.remove(letter);
//                        new_state_map.put(letter, new_tran);
//                    }
//
//                    //remove state map if it has no transitions for any of the letters
//                    boolean remove_state_trans = new_state_map.keySet().size() == 0;
//                    if(!remove_state_trans) {
//                        getState_space().add(new_state);
//                        getTrans().put(new_state, new_state_map);
//                        initial_states2.add(new HashSet<>(Arrays.asList(new_state)));
//
//                    }
//                }
//            } else {
//                initial_states2.add(conjunction);
//            }
//        }
//
//        Iterator<Set<StateCore>> iter = initial_states2.iterator();
//        StateCore first = iter.next().iterator().next();
//        Map<Alphabet, Set<Set<StateCore>>> first_trans = getTrans().get(first);
//
//        StateCore new_state = generateUniqueStateCore();
//        Map<Alphabet, Set<Set<StateCore>>> new_state_map = new HashMap<>();
//
//        //copy the transitions of the first state of the disjunction to the new state
//        for(Alphabet letter : first_trans.keySet()){
//            Set<Set<StateCore>> new_state_letter_map = new HashSet<>();
//            for(Set<StateCore> set : first_trans.get(letter)){
//                new_state_letter_map.add(new HashSet<>(set));
//            }
//            new_state_map.put(letter, new_state_letter_map);
//        }
//
//        //merge the other states of the disjunction
//        while (iter.hasNext()) {
//            StateCore state = iter.next().iterator().next();
//            Map<Alphabet, Set<Set<StateCore>>> state_trans = getTrans().get(state);
//
//            for (Alphabet letter : state_trans.keySet()) {
//                Set<Set<StateCore>> state_letter_tran = state_trans.get(letter);
//                Set<Set<StateCore>> new_state_letter_tran = new_state_map.get(letter);
//                if (new_state_letter_tran == null) {
//                    new_state_letter_tran = new HashSet<>();
//                    new_state_map.put(letter, new_state_letter_tran);
//                }
//                new_state_letter_tran.addAll(state_letter_tran);
//            }
//        }
//
//        //ToDo: remove the intermediate new states in initial_states2. some of them won't ever be mentioned while expanding
//
//        getState_space().add(new_state);
//        getTrans().put(new_state, new_state_map);
//        Set<Set<StateCore>> new_initial_state = new HashSet<>();
//        new_initial_state.add(new HashSet<>(Arrays.asList(new_state)));
//        setInit_states(new_initial_state);
//    }

    /**
     * a function that coverts the automaton to one that has a single initial state
     */
    public void convertToSingleInitialState(){
        if(getInit_states().size() == 1)
            return;

        Set<StateCore> initial_states = getInit_states();

        Iterator<StateCore> iter = initial_states.iterator();
        StateCore first = iter.next();
        Map<Alphabet, Set<Set<StateCore>>> first_trans = getTrans().get(first);

        StateCore new_state = createANewState();
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

        getState_space().add(new_state);
        getTrans().put(new_state, new_state_map);
        Set<StateCore> new_initial_state = new HashSet<>();
        new_initial_state.add(new_state);
        setInit_states(new_initial_state);
    }

    /**
     * a function that calculates the complement of an automaton
     * @return an AFA that represents the complement automaton
     */
    public AFA complement(){
        convertToSingleInitialState();
        complete_aut();
//        System.out.println(this);

        Map<StateCore, Map<Alphabet, Set<Set<StateCore>>>> comp_aut_trans_map = new HashMap<>();

        for(StateCore s : trans.keySet()){
            Map<Alphabet, Set<Set<StateCore>>> comp_state_map = new HashMap<>();
            for(Alphabet letter : trans.get(s).keySet()){
                Set<Set<StateCore>> sets = trans.get(s).get(letter);
                List<List<StateCore>> listOfLists = new ArrayList<>();
                for(Set<StateCore> set : sets)
                    listOfLists.add(new ArrayList<>(set));
                Set<Set<StateCore>> state_letter_set = new HashSet<>(allSelections(listOfLists));
                comp_state_map.put(letter, state_letter_set);
            }
            comp_aut_trans_map.put(s, comp_state_map);
        }

        Set<StateCore> acc_states = new HashSet<>(getState_space());
        acc_states.removeAll(getAcc_states());
        AFA output = new AFA(init_states, alphabet, acc_states, comp_aut_trans_map);

//        Map<StateCore, StateCore> replace_state_cores;
        this.complement = output;
        return output;
    }

    /**
     * a function that returns a list of all the possible selections done over a list of lists, where a selection of a list is just a single member of it
     * @param listOfLists a list of lists
     * @return a list of all the possible selections
     */
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

    /**
     * a function that disambiguates the automaton using its complement automaton
     */
    public void disambiguateByComplement(){
        Map<StateCore, StateCore> comAut_to_Aut = new HashMap<>();
        if(this.complement == null)
            this.complement = this.complement();

        //merge the automata and its complement
        Set<StateCore> comStateSpace = this.complement.getState_space();
        for(StateCore s : comStateSpace) {
            StateCore new_state = createANewState();
            comAut_to_Aut.put(s, new_state);
            comAut_to_Aut.put(s, new_state);
        }

        for(StateCore s : comStateSpace){
            StateCore new_state = comAut_to_Aut.get(s);
            comAut_to_Aut.put(s, new_state);
            Map<Alphabet, Set<Set<StateCore>>> state_map = new HashMap<>();
            for(Alphabet letter: this.complement.getTrans().get(s).keySet()){
                Set<Set<StateCore>> state_letter_sets = new HashSet<>();
                for(Set<StateCore> set : this.complement.getTrans().get(s).get(letter)){
                    Set<StateCore> state_letter_set = new HashSet<>();
                    for(StateCore s2 : set)
                        state_letter_set.add(comAut_to_Aut.get(s2));
                    state_letter_sets.add(state_letter_set);
                }
                state_map.put(letter, state_letter_sets);
            }
            this.getTrans().put(new_state, state_map);
        }

        for(StateCore s : this.complement.getAcc_states()){
            this.getAcc_states().add(comAut_to_Aut.get(s));
        }

        boolean ambiguous = true;
        boolean state_found = false;
        while(ambiguous){
//            System.out.println("================================================");
//            System.out.println(comAut_to_Aut);
//            System.out.println(this);

            state_found = false;
            ambiguous = false;
            NFA<Set<StateCore>, Alphabet, StateCore, Set<StateCore>> nfa = forwardAlternationRemoval();
//            System.out.println(nfa);
            NFA<Pair<Set<StateCore>>, Alphabet, Set<StateCore>, Set<Set<StateCore>>> self_product = nfa.self_product();
//            System.out.println(self_product);
            Set<Pair<Set<StateCore>>> states_that_lead_to_accpetance = self_product.get_states_that_can_lead_to_acceptance();

            //remove states that accept the empty language (Trim)
            Iterator<Pair<Set<StateCore>>> iter = self_product.getInit_states().iterator();
            while(iter.hasNext()){
                if(!states_that_lead_to_accpetance.contains(iter.next()))
                    iter.remove();
            }

            Iterator<Map.Entry<Pair<Set<StateCore>>, Map<Alphabet, Set<Pair<Set<StateCore>>>>>> iter2 = self_product.getTrans().entrySet().iterator();
            while(iter2.hasNext()){
                Map.Entry<Pair<Set<StateCore>>, Map<Alphabet, Set<Pair<Set<StateCore>>>>> entry = iter2.next();
                Pair<Set<StateCore>> state = entry.getKey();
                if(!states_that_lead_to_accpetance.contains(state)) {
                    iter2.remove();
                    continue;
                }

                Iterator<Map.Entry<Alphabet, Set<Pair<Set<StateCore>>>>> iter3 = entry.getValue().entrySet().iterator();
                while(iter3.hasNext()){
                    Map.Entry<Alphabet, Set<Pair<Set<StateCore>>>> entry2 = iter3.next();
                    Set<Pair<Set<StateCore>>> tran_output = entry2.getValue();
                    tran_output.retainAll(states_that_lead_to_accpetance);
                    if(tran_output.size() == 0)
                        iter3.remove();
                }
                if(entry.getValue().size() == 0)
                    iter2.remove();
            }

//            System.out.println(self_product);
            for(Pair<Set<StateCore>> state : self_product.getTrans().keySet()){
                if(state.identical()) {
                    Map<Alphabet, Set<Pair<Set<StateCore>>>> state_map = self_product.getTrans().get(state);
                    for (Alphabet letter : state_map.keySet()) {
                        Set<Pair<Set<StateCore>>> state_letter_set = state_map.get(letter);
                        for (Pair<Set<StateCore>> state2 : state_letter_set) {
                            if (!state2.identical()) {
                                ambiguous = true;
                                Set<StateCore> l = state.left;
                                for (StateCore AFAState : l) {
                                    Set<Set<StateCore>> afa_state_letter_set = this.getTrans().get(AFAState).get(letter);
                                    for (Set<StateCore> x : afa_state_letter_set) {
                                        for (Set<StateCore> y : afa_state_letter_set) {
                                            if (x != y && state2.left.containsAll(x) && state2.right.containsAll(y)) {
                                                state_found = true;
                                                afa_state_letter_set.remove(y);
                                                for(StateCore p : x){
                                                    Set<StateCore> new_states_set = new HashSet<>();
                                                    new_states_set.addAll(y);
                                                    new_states_set.add(comAut_to_Aut.get(p));
                                                    afa_state_letter_set.add(new_states_set);
                                                }
                                                break;
                                            }
                                        }
                                        if(state_found) break;
                                    }
                                    if(state_found) break;
                                }
                                break;
                            }
                        }
                        if (ambiguous) break;
                    }
                    if (ambiguous) break;
                }
            }
        }
//        System.out.println(comAut_to_Aut);
    }

    /**
     * a function that applies the forward alternation removal algorithm
     * @return an NFA
     */
    public NFA forwardAlternationRemoval(){

        Set<Set<StateCore>> init_states = new HashSet<>(Arrays.asList(getInit_states()));
        Set<Alphabet> alphabet = getAlphabet();

        ExpandFunction<Set<StateCore>, Alphabet, Map<StateCore, Map<Alphabet, Set<Set<StateCore>>>>, Set<Set<StateCore>>> AFAtoNFAExpand = (state, letter, in_tran) -> {
            Set<Set<StateCore>> conTranFunc = configTranFunction(state, letter, in_tran);
            if(conTranFunc == null)
                return null;
            Set<Set<StateCore>> outputState = new HashSet<>(conTranFunc);

            for(Set<StateCore> set : conTranFunc){
                for(Set<StateCore> set2: conTranFunc){
                    if(set.containsAll(set2) && set.size() > set2.size())
                        outputState.remove(set);
                }
            }

            if(!outputState.isEmpty())
                return outputState;
            else
                return null;
        };

        HasPropertyFunction<StateCore, Set<StateCore>> AFAToNFAIsAcceptStateFunction = (inAccStates, state) -> {
            if(inAccStates.containsAll(state))
                    return true;
            return false;
        };

        NFA A_out = new NFA(init_states, alphabet, AFAtoNFAExpand, AFAToNFAIsAcceptStateFunction, getTrans(), getAcc_states());
        A_out.expandForward();

        return A_out;
    }

    /**
     * it represents the configuration transition function
     * @param setOfStates
     * @param letter a letter of the alphabet
     * @param trans the transition function
     * @param <StateCore>
     * @param <Alphabet>
     * @return the output that the configuration transition function would return given a set of states and a letter and regular transition function
     */
    public static <StateCore, Alphabet> Set<Set<StateCore>> configTranFunction(Set<StateCore> setOfStates, Alphabet letter, Map<StateCore, Map<Alphabet, Set<Set<StateCore>>>> trans) {
        List<List<Set<StateCore>>> listOfLists = new ArrayList<>();
        for(StateCore s: setOfStates){
            Map<Alphabet, Set<Set<StateCore>>> state_map = trans.get(s);
            if(state_map == null)
                return null;
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

    /**
     * the function returns the (state space - some states that obviously can't lead to an acceptance state)
     * @return a set of states. That set may contain states that can still lead to an acceptance but are included because the function does a simplistic test.
     */
    @Override
    public Set<StateCore> get_states_that_can_lead_to_acceptance(){
        Set<StateCore> states_accessible_from_initial_states = new HashSet<>(get_reachable_states());

        Map<StateCore, Map<Alphabet, Set<StateCore>>> trans_reverse = new HashMap<>();
        for(StateCore s : getTrans().keySet()){
            for(Alphabet letter : getTrans().get(s).keySet()){
                    Set<Set<StateCore>> set_of_sets_of_states = getTrans().get(s).get(letter);
                for(Set<StateCore> s2 : set_of_sets_of_states){
                    for(StateCore s3 : s2){
                        Map<Alphabet, Set<StateCore>> current_state_map = trans_reverse.get(s3);
                        if(current_state_map == null){
                            current_state_map = new HashMap<>();
                            trans_reverse.put(s3, current_state_map);
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

    /**
     * removes some states that obviously can never lead an acceptance.
     */
    @Override
    public void trim(){
        Set<StateCore> states_that_lead_to_accpetance = this.get_states_that_can_lead_to_acceptance();
        Iterator<StateCore> iter = this.getInit_states().iterator();
        while(iter.hasNext()){
            if(!states_that_lead_to_accpetance.contains(iter.next()))
                iter.remove();
        }

        Iterator<Map.Entry<StateCore, Map<Alphabet, Set<Set<StateCore>>>>> iter2 = this.getTrans().entrySet().iterator();
        while(iter2.hasNext()){
            Map.Entry<StateCore, Map<Alphabet, Set<Set<StateCore>>>> entry = iter2.next();
            StateCore state = entry.getKey();
            if(!states_that_lead_to_accpetance.contains(state)) {
                iter2.remove();
                continue;
            }

            Iterator<Map.Entry<Alphabet, Set<Set<StateCore>>>> iter3 = entry.getValue().entrySet().iterator();
            while(iter3.hasNext()){
                Map.Entry<Alphabet, Set<Set<StateCore>>> entry2 = iter3.next();
                Set<Set<StateCore>> tran_output = entry2.getValue();
                Iterator<Set<StateCore>> iter4 = tran_output.iterator();
                while(iter4.hasNext()){
                    Set<StateCore> set = iter4.next();
                    set.retainAll(states_that_lead_to_accpetance);
                    if(set.size() == 0)
                        iter4.remove();
                }
                if(entry2.getValue().size() == 0)
                    iter3.remove();
            }
            if(entry.getValue().size() == 0) {
                iter2.remove();
            }
        }
    }
}
