package cz.devfire.mysteryblocks.Files;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cz.devfire.mysteryblocks.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Data {
    private final String config;
    private final String folder;
    public Plugin plugin;
    private String header;
    private String end = "yml";
    private FileConfiguration a;
    private File f, fold;
    private Map<String, Object> c = Maps.newHashMap();

    private Data(Plugin plugin, String configName) {
        config = configName;
        folder = plugin.getName();
        this.plugin = plugin;

        if (fold == null) {
            fold = new File("plugins/" + folder);
        }

        if (!fold.exists()) {
            fold.mkdir();
        }

        create();
    }

    public static Data loadFile(Plugin plugin, String pluginPath, String serverPath) {
        String configName = serverPath.split("\\.")[0];

        File file = new File(plugin.getDataFolder(), serverPath);

        if (!file.exists()) {
            try {
                InputStream in = plugin.getResource(pluginPath);
                Files.copy(in, file.toPath());
            } catch (IOException e) { /* */ }
        }

        return new Data(plugin, configName);
    }

    public String getName() {
        return config;
    }

    public String getFolderName() {
        return folder;
    }

    public File getFolder() {
        return fold;
    }

    public boolean existsPath(String string) {
        return getString(string) != null;
    }

    public boolean exists(String string) {
        return existsPath(string);
    }

    public boolean exist(String string) {
        return existsPath(string);
    }

    public boolean isNull(String string) {
        return !existsPath(string);
    }

    public boolean existPath(String string) {
        return existsPath(string);
    }

    public void addDefault(String path, Object value) {
        c.put(path, value);

        if (a != null) {
            a.addDefault(path, value);
        }
    }

    public void addDefaults(Map<String, Object> defaults) {
        c = defaults;

        if (a != null) {
            a.addDefaults(defaults);
        }
    }

    public void addDefaults(HashMap<String, Object> defaults) {
        c = defaults;

        if (a != null) {
            a.addDefaults(defaults);
        }
    }

    public boolean existFile() {
        new File(getFolder(), config + "." + end);
        return true;
    }

    public File getFile() {
        Boolean success = true;

        if (f == null) {
            File ff = new File(getFolder(), config + "." + end);

            if (!ff.exists()) {
                try {
                    if (this.plugin.getResource(ff.getName()) == null) {
                        ff.createNewFile();
                    } else {
                        this.plugin.saveResource(ff.getName(), false);
                    }

                    f = ff;
                } catch (Exception e) {
                    success = false;
                    e.printStackTrace();
                }

            }

            if (success) {
                Bukkit.getConsoleSender().sendMessage(Utils.mm("<yellow> - Loading " + ff.getName() + "... <color:#05fa11>Successful!"));
            } else {
                Bukkit.getConsoleSender().sendMessage(Utils.mm("<yellow> - Loading " + ff.getName() + "... <color:#f01f1f>Failed!"));
            }

            f = ff;
        }

        return f;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public FileConfiguration getConfig() {
        return a;
    }

    public void setCustomEnd(String customEnd) {
        if (customEnd != null) {
            end = customEnd;
        }
    }

    public boolean remove(String path) {
        return removePath(path);
    }

    public boolean removePath(String path) {
        if (getString(path) != null) {
            set(path, null);
            return true;
        }

        return false;
    }

    public boolean createPath(String path) {
        if (getString(path) == null) {
            createSection(path);
            return true;
        }

        return false;
    }

    public void createSection(String path) {
        if (!existFile()) {
            return;
        }

        if (a == null) {
            YamlConfiguration.loadConfiguration(getFile()).createSection(path);
        }

        a.createSection(path);
    }

    private boolean check() {
        if (a == null) {
            plugin.getLogger().info("");
            return false;
        } else if (!existFile()) {
            plugin.getLogger().info("");
            return false;
        } else {
            return true;
        }
    }

    public boolean save() {
        try {
            if (check()) {
                a.save(f);
                return true;
            }

            return false;
        } catch (Exception e) {
            plugin.getLogger().info("");
            return false;
        }
    }

    public boolean reload() {
        try {
            f = null;
            a = null;
            f = getFile();
            Reader reader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
            a = YamlConfiguration.loadConfiguration(reader);

            if (header != null) {
                a.options().header(header);
            }

            if (c != null && !c.isEmpty()) {
                a.addDefaults(c);
            }

            a.options().copyDefaults(true).copyHeader(true);
            save();
            return true;
        } catch (Exception e) {
            plugin.getLogger().info("");
            Bukkit.getConsoleSender().sendMessage(Utils.mm(" <color:#f01f1f>- Reload failed!"));
            return false;
        }
    }

    public int getInt(String path) {
        if (!existFile()) {
            return 0;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getInt(path);
        }

        return a.getInt(path);
    }

    public double getDouble(String path) {
        if (!existFile()) {
            return 0;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getDouble(path);
        }

        return a.getDouble(path);
    }

    public String getString(String path) {
        if (!existFile()) {
            return null;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getString(path);
        }

        return a.getString(path);
    }

    public long getLong(String path) {
        if (!existFile()) {
            return 0;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getLong(path);
        }

        return a.getLong(path);
    }

    public boolean getBoolean(String path) {
        if (!existFile()) {
            return false;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getBoolean(path);
        }

        return a.getBoolean(path);
    }

    public ConfigurationSection getConfigurationSection(String path) {
        if (!existFile()) {
            return null;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getConfigurationSection(path);
        }

        return a.getConfigurationSection(path);
    }

    public Set<String> getKeys(boolean keys) {
        if (!existFile()) {
            return null;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getKeys(keys);
        }

        return a.getKeys(keys);
    }

    public Set<String> getKeys(String key) {
        return getConfigurationSection(key, false);
    }

    public Set<String> getConfigurationSection(String path, boolean keys) {
        if (!existFile()) {
            return null;
        }

        if (!existPath(path)) {
            return Sets.newHashSet();
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getConfigurationSection(path).getKeys(keys);
        }

        return a.getConfigurationSection(path).getKeys(keys);
    }

    public void set(String path, Object value) {
        if (!existFile()) {
            return;
        }

        if (a == null) {
            YamlConfiguration.loadConfiguration(getFile()).set(path, value);
        }

        a.set(path, value);
    }

    public boolean isString(String path) {
        if (!existFile()) {
            return false;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).isString(path);
        }

        return a.isString(path);
    }

    public boolean isLong(String path) {
        if (!existFile()) {
            return false;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).isLong(path);
        }

        return a.isLong(path);
    }

    public boolean isList(String path) {
        if (!existFile()) {
            return false;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).isList(path);
        }

        return a.isList(path);
    }

    public boolean isDouble(String path) {
        if (!existFile()) {
            return false;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).isDouble(path);
        }

        return a.isDouble(path);
    }

    public boolean isInt(String path) {
        if (!existFile()) {
            return false;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).isBoolean(path);
        }

        return a.isInt(path);
    }

    public boolean isBoolean(String path) {
        if (!existFile()) {
            return false;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).isBoolean(path);
        }

        return a.isBoolean(path);
    }

    public boolean isConfigurationSection(String path) {
        if (!existFile()) {
            return false;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).isConfigurationSection(path);
        }

        return a.isConfigurationSection(path);
    }

    public Object get(String path) {
        if (!existFile()) {
            return null;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).get(path);
        }

        return a.get(path);
    }

    public List<String> getStringList(String path) {
        if (!existFile()) {
            return null;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getStringList(path);
        }

        return a.getStringList(path);
    }

    public List<?> getList(String path) {
        if (!existFile()) {
            return null;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getList(path);
        }

        return a.getList(path);
    }

    public Color getColor(String path) {
        if (!existFile()) {
            return null;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getColor(path);
        }

        return a.getColor(path);
    }

    public ItemStack getItemStack(String path) {
        if (!existFile()) {
            return null;
        }

        if (a == null) {
            return YamlConfiguration.loadConfiguration(getFile()).getItemStack(path);
        }

        return a.getItemStack(path);
    }

    public boolean create() {
        if (f == null) {
            f = getFile();
        }

        try {
            if (a == null) {
                Reader reader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
                a = YamlConfiguration.loadConfiguration(reader);
            }
        } catch (Exception repeat) {
            a = YamlConfiguration.loadConfiguration(f);
            return false;
        }

        return true;
    }

    public Map<String, Object> getDefaults() {
        return c;
    }

    public boolean delete() {
        if (f == null) {
            f = getFile();
        }

        if (f.exists()) {
            f.delete();
            c.clear();
            return true;
        }

        return false;
    }
}