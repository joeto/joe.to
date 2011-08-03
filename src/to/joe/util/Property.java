package to.joe.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Properties file handling
 * 
 */
public final class Property {
    private final String name;
    private final Properties property = new Properties();

    public Property(String name) {
        this.name = name;
        final File file = new File(name);
        try {
            if (file.exists()) {
                this.load();
            } else {
                this.save();
            }
        } catch (final IOException ex) {
        }
    }

    public void load() throws IOException {
        this.property.load(new FileInputStream(this.name));
    }

    public void save() {
        try {
            this.property.store(new FileOutputStream(this.name), null);
        } catch (final IOException ex) {
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> returnMap() throws Exception {
        return (Map<String, String>) this.property.clone();
    }

    public boolean containsKey(String var) {
        return this.property.containsKey(var);
    }

    public String getProperty(String var) {
        return this.property.getProperty(var);
    }

    public void removeKey(String var) {
        if (this.property.containsKey(var)) {
            this.property.remove(var);
            this.save();
        }
    }

    public boolean keyExists(String key) {
        return this.containsKey(key);
    }

    public String getString(String key) {
        if (this.containsKey(key)) {
            return this.getProperty(key);
        }

        return "";
    }

    public String getString(String key, String value) {
        if (this.containsKey(key)) {
            return this.getProperty(key);
        }
        this.setString(key, value);
        return value;
    }

    public void setString(String key, String value) {
        this.property.put(key, value);
        this.save();
    }

    public int getInt(String key) {
        if (this.containsKey(key)) {
            return Integer.parseInt(this.getProperty(key));
        }
        return 0;
    }

    public int getInt(String key, int value) {
        if (this.containsKey(key)) {
            return Integer.parseInt(this.getProperty(key));
        }
        this.setInt(key, value);
        return value;

    }

    public void setInt(String key, int value) {
        this.property.put(key, String.valueOf(value));
        this.save();
    }

    public double getDouble(String key) {
        if (this.containsKey(key)) {
            return Double.parseDouble(this.getProperty(key));
        }
        return 0;
    }

    public double getDouble(String key, double value) {
        if (this.containsKey(key)) {
            return Double.parseDouble(this.getProperty(key));
        }
        this.setDouble(key, value);
        return value;
    }

    public void setDouble(String key, double value) {
        this.property.put(key, String.valueOf(value));
        this.save();
    }

    public long getLong(String key) {
        if (this.containsKey(key)) {
            return Long.parseLong(this.getProperty(key));
        }
        return 0;
    }

    public long getLong(String key, long value) {
        if (this.containsKey(key)) {
            return Long.parseLong(this.getProperty(key));
        }
        this.setLong(key, value);
        return value;
    }

    public void setLong(String key, long value) {
        this.property.put(key, String.valueOf(value));
        this.save();
    }

    public boolean getBoolean(String key) {
        if (this.containsKey(key)) {
            return Boolean.parseBoolean(this.getProperty(key));
        }
        return false;
    }

    public boolean getBoolean(String key, boolean value) {
        if (this.containsKey(key)) {
            return Boolean.parseBoolean(this.getProperty(key));
        }
        this.setBoolean(key, value);
        return value;
    }

    public void setBoolean(String key, boolean value) {
        this.property.put(key, String.valueOf(value));
        this.save();
    }
}
