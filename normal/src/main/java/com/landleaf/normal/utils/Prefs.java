//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.landleaf.normal.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * 本地xml文件保存工具类
 * <p>
 * 2017-11-15 新增互联网属性，可从服务器获取配置
 */
public class Prefs {
    private static SharedPreferences sharedPreferences;

    private static Prefs prefsInstance;

    private static final String LENGTH = "_length";
    public static final String DEFAULT_STRING_VALUE = "";
    private static final int DEFAULT_INT_VALUE = -1;
    private static final double DEFAULT_DOUBLE_VALUE = -1.0D;
    private static final float DEFAULT_FLOAT_VALUE = -1.0F;
    private static final long DEFAULT_LONG_VALUE = -1L;
    private static final boolean DEFAULT_BOOLEAN_VALUE = false;

    public Prefs(@NonNull Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Prefs(@NonNull Context context, @NonNull String preferencesName) {
        sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_MULTI_PROCESS);
    }

    public static void register(@NonNull Context context, SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        Prefs.with(context);
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public static void register(@NonNull Context context, String name, SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        Prefs.with(context, name);
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public static void unregister(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public static Prefs with(@NonNull Context context) {
        if (prefsInstance == null) {
            prefsInstance = new Prefs(context);
        }
        return prefsInstance;
    }

    public static Prefs with(@NonNull Context context, boolean forceInstantiation) {
        if (forceInstantiation) {
            prefsInstance = new Prefs(context);
        }
        return prefsInstance;
    }

    public static Prefs with(@NonNull Context context, String preferencesName) {
        if (prefsInstance == null) {
            prefsInstance = new Prefs(context, preferencesName);
        }
        return prefsInstance;
    }

    public static Prefs with(@NonNull Context context, @NonNull String preferencesName, boolean forceInstantiation) {
        if (forceInstantiation) {
            prefsInstance = new Prefs(context, preferencesName);
        }
        return prefsInstance;
    }

    public String read(String what) {
        return sharedPreferences.getString(what, DEFAULT_STRING_VALUE);
    }

    public String read(String what, String defaultString) {
        return sharedPreferences.getString(what, defaultString);
    }

    public void write(String where, String what) {
        sharedPreferences.edit().putString(where, what).apply();
    }

    public int readInt(String what) {
        return sharedPreferences.getInt(what, DEFAULT_INT_VALUE);
    }

    public int readInt(String what, int defaultInt) {
        return sharedPreferences.getInt(what, defaultInt);
    }

    public void writeInt(String where, int what) {
        sharedPreferences.edit().putInt(where, what).apply();
    }

    public double readDouble(String what) {
        return !this.contains(what) ? DEFAULT_DOUBLE_VALUE : Double.longBitsToDouble(this.readLong(what));
    }

    public double readDouble(String what, double defaultDouble) {
        return !this.contains(what) ? defaultDouble : Double.longBitsToDouble(this.readLong(what));
    }

    public void writeDouble(String where, double what) {
        this.writeLong(where, Double.doubleToRawLongBits(what));
    }

    public float readFloat(String what) {
        return sharedPreferences.getFloat(what, DEFAULT_FLOAT_VALUE);
    }

    public float readFloat(String what, float defaultFloat) {
        return sharedPreferences.getFloat(what, defaultFloat);
    }

    public void writeFloat(String where, float what) {
        sharedPreferences.edit().putFloat(where, what).apply();
    }

    private long readLong(String what) {
        return sharedPreferences.getLong(what, DEFAULT_LONG_VALUE);
    }

    public long readLong(String what, long defaultLong) {
        return sharedPreferences.getLong(what, defaultLong);
    }

    private void writeLong(String where, long what) {
        sharedPreferences.edit().putLong(where, what).apply();
    }

    public boolean readBoolean(String what) {
        return sharedPreferences.getBoolean(what, DEFAULT_BOOLEAN_VALUE);
    }

    public boolean readBoolean(String what, boolean defaultBoolean) {
        return sharedPreferences.getBoolean(what, defaultBoolean);
    }

    public void writeBoolean(String where, boolean what) {
        sharedPreferences.edit().putBoolean(where, what).apply();
    }

    @TargetApi(11)
    public void putStringSet(String key, Set<String> value) {
        sharedPreferences.edit().putStringSet(key, value).apply();
    }

    private void putOrderedStringSet(String key, Set<String> value) {
        int stringSetLength = 0;
        if (sharedPreferences.contains(key + LENGTH)) {
            stringSetLength = this.readInt(key + LENGTH);
        }

        this.writeInt(key + LENGTH, value.size());
        int i = 0;

        for (Iterator var5 = value.iterator(); var5.hasNext(); ++i) {
            String aValue = (String) var5.next();
            this.write(key + "[" + i + "]", aValue);
        }

        while (i < stringSetLength) {
            this.remove(key + "[" + i + "]");
            ++i;
        }

    }

    @TargetApi(11)
    public Set<String> getStringSet(String key, Set<String> defValue) {
        return sharedPreferences.getStringSet(key, defValue);
    }

    private Set<String> getOrderedStringSet(String key, Set<String> defValue) {
        if (!this.contains(key + LENGTH)) {
            return defValue;
        } else {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            int stringSetLength = this.readInt(key + LENGTH);
            if (stringSetLength >= 0) {
                for (int i = 0; i < stringSetLength; ++i) {
                    set.add(this.read(key + "[" + i + "]"));
                }
            }
            return set;
        }
    }

    public void remove(String key) {
        if (this.contains(key + LENGTH)) {
            int stringSetLength = this.readInt(key + LENGTH);
            if (stringSetLength >= 0) {
                sharedPreferences.edit().remove(key + LENGTH).apply();
                for (int i = 0; i < stringSetLength; ++i) {
                    sharedPreferences.edit().remove(key + "[" + i + "]").apply();
                }
            }
        }
        sharedPreferences.edit().remove(key).apply();
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}