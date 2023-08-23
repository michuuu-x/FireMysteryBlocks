package cz.devfire.mysteryblocks.Database.Object;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Results {
    List<Map<String, Object>> resultList = Lists.newArrayList();
    int index = -1;

    public Results(List<Map<String, Object>> resultList) {
        this.resultList = resultList;
    }

    public Results(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            HashMap<String, Object> row = Maps.newHashMap();

            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnName(i), resultSet.getObject(i));
            }

            resultList.add(row);
        }
    }

    public boolean hasNext() {
        return resultList.size() != 0;
    }

    public boolean next() {
        index++;
        return resultList.size() > index;
    }

    public boolean isNull() {
        return resultList.get(index) == null;
    }

    public String getString(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        if (o instanceof String) {
            return (String) o;
        } else {
            return null;
        }
    }

    public int getInt(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        if (o instanceof Integer) {
            return (int) o;
        } else if (o instanceof String) {
            return Integer.parseInt((String) o);
        }

        throw new NumberFormatException("Cannot convert " + o.getClass().getName() + " to int");
    }

    public double getDouble(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        if (o instanceof Double) {
            return (double) o;
        } else if (o instanceof String) {
            return Double.parseDouble((String) o);
        }

        throw new NumberFormatException("Cannot convert " + o.getClass().getName() + " to double");
    }

    public byte getByte(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        if (o instanceof Byte) {
            return (byte) o;
        } else if (o instanceof String) {
            return Byte.parseByte((String) o);
        }

        throw new NumberFormatException("Cannot convert " + o.getClass().getName() + " to byte");
    }

    public short getShort(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        if (o instanceof Short) {
            return (short) o;
        } else if (o instanceof String) {
            return Short.parseShort((String) o);
        }

        throw new NumberFormatException("Cannot convert " + o.getClass().getName() + " to short");
    }

    public long getLong(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        if (o instanceof Long) {
            return (long) o;
        } else if (o instanceof String) {
            return Long.parseLong((String) o);
        }

        throw new NumberFormatException("Cannot convert " + o.getClass().getName() + " to long");
    }

    public float getFloat(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        if (o instanceof Float) {
            return (long) o;
        } else if (o instanceof String) {
            return Float.parseFloat((String) o);
        }

        throw new NumberFormatException("Cannot convert " + o.getClass().getName() + " to float");
    }

    public boolean getBoolean(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        if (o instanceof Boolean) {
            return (boolean) o;
        } else {
            return false;
        }
    }

    public Date getDate(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        if (o instanceof Date) {
            return (Date) o;
        } else {
            return null;
        }
    }

    public Time getTime(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        if (o instanceof Time) {
            return (Time) o;
        } else {
            return null;
        }
    }

    public Timestamp getTimestamp(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        if (o instanceof Timestamp) {
            return (Timestamp) o;
        } else {
            return null;
        }
    }
}
