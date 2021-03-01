import java.util.Objects;

/**
 * a class for pairs
 * @param <Type> the type of the elements of the pair
 */
public class Pair<Type> {
    Type left;
    Type right;

    public Type getLeft() {
        return left;
    }

    public void setLeft(Type left) {
        this.left = left;
    }

    public Type getRight() {
        return right;
    }

    public void setRight(Type right) {
        this.right = right;
    }

    Pair(Type left, Type right){
        this.left = left;
        this.right = right;
    }

    public boolean identical(){
        if(left == null)
            return right == null;
        if(right == null)
            return false;
        return left.equals(right);
    }

    @Override
    public String toString(){
        return "(" + left + ", " + right + ")";
    }

    @Override
    public boolean equals(Object o){
        if(o == null)
            return false;

        if(o.getClass() != this.getClass())
            return false;

        boolean left_equal = false;
        boolean right_equal = false;

        Pair<Type> pair = (Pair) o;

        if(this.left == null)
            left_equal = pair.right == null;
        else
            if(pair.left != null)
                left_equal = pair.left.equals(this.left);

        if(this.right == null)
            right_equal = pair.right == null;
        else
            if(pair.right != null)
                right_equal = pair.right.equals(this.right);

        return left_equal && right_equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
