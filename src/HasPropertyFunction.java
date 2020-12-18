import java.util.Set;

@FunctionalInterface
public interface HasPropertyFunction<InputStateCore, OutputStateCore> {
    boolean apply(Set<InputStateCore> input_states, OutputStateCore state);
}
