package cz.devfire.mysteryblocks.Hologram.Interface;

public interface HologramProvider {
    void create();
    void recreate();
    void update();
    void destroy();
    boolean checkStatic();
}
