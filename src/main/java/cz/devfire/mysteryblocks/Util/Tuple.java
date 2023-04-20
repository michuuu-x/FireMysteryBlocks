package cz.devfire.mysteryblocks.Util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Tuple<A, B, C> {
    private A first;
    private B second;
    private C third;

    public Tuple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public Object get(int index) {
        switch (index) {
            case 0:
                return first;
            case 1:
                return second;
            case 2:
                return third;
            default:
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: 3");
        }
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ")";
    }
}