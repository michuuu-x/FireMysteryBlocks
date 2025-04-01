package cz.devfire.mysteryblocks.Block.Handler.MiningEffects;

import lombok.Data;

@Data
public class MiningEffect {
    private final String type;
    private final double chance;
    private final int amplifier;
    private final int duration;
}
