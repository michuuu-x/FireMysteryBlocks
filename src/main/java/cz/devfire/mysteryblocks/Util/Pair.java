package cz.devfire.mysteryblocks.Util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pair<U, V> {
    private U first;
    private V second;

    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }
}
