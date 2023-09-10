package cz.devfire.mysteryblocks.Database.Object;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.math.BigInteger;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryResult {
    List<Map<String, Object>> resultList = Lists.newArrayList();
    int index = -1;

    public QueryResult(ResultSet resultSet) throws SQLException {
        if (resultSet == null) return;

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            HashMap<String, Object> row = Maps.newHashMap();

            for (int i = 1; i <= columnCount; i++) {

                row.put(metaData.getColumnName(i), resultSet.getObject(i));
            }

            resultList.add(row);
        }

        resultSet.close();
    }

    public Integer getInt(String columnLabel) {
        return getInt(columnLabel, 0);
    }

    public Integer getInt(String columnLabel, int defaultValue) {
        Object o = getObject(columnLabel);
        if (o == null) return defaultValue;

        if (o instanceof Integer)
            return (Integer) o;

        if (o instanceof BigInteger)
            return ((BigInteger) o).intValue();

        if (o instanceof Number)
            return ((Number) o).intValue();

        try {
            return Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Column " + columnLabel + " is not an integer. Type: " + o.getClass().getSimpleName());
        }
    }

    public String getString(String columnLabel) {
        return getString(columnLabel, null);
    }

    public String getString(String columnLabel, String defaultValue) {
        Object o = getObject(columnLabel);
        if (o == null) return defaultValue;

        try {
            return o.toString();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Column " + columnLabel + " is not a string. Type: " + o.getClass().getSimpleName());
        }
    }

    public Double getDouble(String columnLabel) {
        return getDouble(columnLabel, null);
    }

    public Double getDouble(String columnLabel, Double defaultValue) {
        Object o = getObject(columnLabel);
        if (o == null) return defaultValue;

        if (o instanceof Double)
            return (Double) o;

        if (o instanceof Number)
            return ((Number) o).doubleValue();

        try {
            return Double.parseDouble(o.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Column " + columnLabel + " is not a double. Type: " + o.getClass().getSimpleName());
        }
    }

    public Long getLong(String columnLabel) {
        return getLong(columnLabel, null);
    }

    public Long getLong(String columnLabel, Long defaultValue) {
        Object o = getObject(columnLabel);
        if (o == null) return defaultValue;

        if (o instanceof Long)
            return (Long) o;

        if (o instanceof Number)
            return ((Number) o).longValue();

        try {
            return Long.parseLong(o.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Column " + columnLabel + " is not a long. Type: " + o.getClass().getSimpleName());
        }
    }

    public Boolean getBoolean(String columnLabel) {
        return getBoolean(columnLabel, null);
    }

    public Boolean getBoolean(String columnLabel, Boolean defaultValue) {
        Object o = getObject(columnLabel);
        if (o == null) return defaultValue;

        if (o instanceof Boolean)
            return (Boolean) o;

        if (o instanceof Number)
            return ((Number) o).intValue() == 1;

        try {
            return Boolean.parseBoolean(o.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Column " + columnLabel + " is not a boolean. Type: " + o.getClass().getSimpleName());
        }
    }

    public byte getByte(String columnLabel) {
        return getByte(columnLabel, null);
    }

    public Byte getByte(String columnLabel, Byte defaultValue) {
        Object o = getObject(columnLabel);
        if (o == null) return defaultValue;

        if (o instanceof Byte)
            return (Byte) o;

        if (o instanceof Number)
            return ((Number) o).byteValue();

        try {
            return Byte.parseByte(o.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Column " + columnLabel + " is not a byte. Type: " + o.getClass().getSimpleName());
        }
    }

    public Float getFloat(String columnLabel) {
        return getFloat(columnLabel, null);
    }

    public Float getFloat(String columnLabel, Float defaultValue) {
        Object o = getObject(columnLabel);
        if (o == null) return defaultValue;

        if (o instanceof Float)
            return (Float) o;

        if (o instanceof Number)
            return ((Number) o).floatValue();

        try {
            return Float.parseFloat(o.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Column " + columnLabel + " is not a float. Type: " + o.getClass().getSimpleName());
        }
    }

    public Date getDate(String columnLabel) {
        return getDate(columnLabel, null);
    }

    public Date getDate(String columnLabel, Date defaultValue) {
        Object o = getObject(columnLabel);
        if (o == null) return defaultValue;

        if (o instanceof Date)
            return (Date) o;

        if (o instanceof Timestamp)
            return new Date(((Timestamp) o).getTime());

        try {
            return Date.valueOf(o.toString());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Column " + columnLabel + " is not a date. Type: " + o.getClass().getSimpleName());
        }
    }

    public Time getTime(String columnLabel) {
        return getTime(columnLabel, null);
    }

    public Time getTime(String columnLabel, Time defaultValue) {
        Object o = getObject(columnLabel);
        if (o == null) return defaultValue;

        if (o instanceof Time)
            return (Time) o;

        if (o instanceof Timestamp)
            return new Time(((Timestamp) o).getTime());

        try {
            return Time.valueOf(o.toString());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Column " + columnLabel + " is not a time. Type: " + o.getClass().getSimpleName());
        }
    }

    public Timestamp getTimestamp(String columnLabel) {
        return getTimestamp(columnLabel, null);
    }

    public Timestamp getTimestamp(String columnLabel, Timestamp defaultValue) {
        Object o = getObject(columnLabel);
        if (o == null) return defaultValue;

        if (o instanceof Timestamp)
            return (Timestamp) o;

        try {
            return Timestamp.valueOf(o.toString());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Column " + columnLabel + " is not a timestamp. Type: " + o.getClass().getSimpleName());
        }
    }

    public Object getObject(String columnLabel) {
        return getObject(columnLabel, null);
    }

    public Object getObject(String columnLabel, Object defaultValue) {
        return resultList.get(index).getOrDefault(columnLabel, defaultValue);
    }

    public boolean hasNext() {
        return resultList.size() > index + 1;
    }

    public boolean next() {
        if (hasNext()) {
            index++;
            return true;
        }

        return false;
    }

    public boolean isEmpty() {
        return resultList.isEmpty();
    }
}