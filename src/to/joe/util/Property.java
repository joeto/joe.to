package to.joe.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

public final class Property {
    private String name;
    private Properties property = new Properties();

    public Property(String name) {
        this.name = name;

        File file = new File(name);

        try {
            if (file.exists()) {
                load();
            } else {
                save();
            }
        } catch (IOException ex) {
        }
    }

    public void load() throws IOException {
        property.load(new FileInputStream(name));
    }

    public void save() {
        try {
        property.store(new FileOutputStream(name), null);
        }catch(IOException ex) {
        }
    }

    @SuppressWarnings("unchecked")
	public Map<String, String> returnMap() throws Exception {
        return (Map<String, String>) property.clone();
    }

    public boolean containsKey(String var) {
        return property.containsKey(var);
    }

    public String getProperty(String var) {
        return (String)property.getProperty(var);
    }

    public void removeKey(String var) {
        if (this.property.containsKey(var)) {
            this.property.remove(var);
            save();
        }
    }

    public boolean keyExists(String key) {
        return containsKey(key);
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

        setString(key, value);
        return value;
    }

    public void setString(String key, String value) {
        property.put(key, value);
        save();
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

        setInt(key, value);
        return value;

    }

    public void setInt(String key, int value) {
        property.put(key, String.valueOf(value));

        save();
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

        setDouble(key, value);
        return value;
    }

    public void setDouble(String key, double value) {
        property.put(key, String.valueOf(value));

        save();
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

        setLong(key, value);
        return value;
    }

    public void setLong(String key, long value) {
        property.put(key, String.valueOf(value));

        save();
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

        setBoolean(key, value);
        return value;
    }

    public void setBoolean(String key, boolean value) {
        property.put(key, String.valueOf(value));

        save();
    }
}

