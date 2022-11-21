package cz.devfire.mysteryblocks.api.Other;

import org.bukkit.configuration.Configuration;

import java.util.Collection;

public interface Config extends Configuration {
    Collection<String> getKeys(String path);
}
