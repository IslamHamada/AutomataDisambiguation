@FunctionalInterface
public interface ExpandFunction<StateCore, Alphabet, InputTransition, TransitionOutput> {
    TransitionOutput apply(StateCore state, Alphabet letter, InputTransition inputTrans);
}
