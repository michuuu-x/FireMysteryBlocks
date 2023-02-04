package cz.devfire.mysteryblocks.Database.Object;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;

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

    public boolean next() {
        return resultList.size() > index++;
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

        return ((Number) o).intValue();
    }

    public double getDouble(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        return ((Number) o).doubleValue();
    }

    public byte getByte(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        return ((Number) o).byteValue();
    }

    public short getShort(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        return ((Number) o).shortValue();
    }

    public long getLong(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        return ((Number) o).longValue();
    }

    public float getFloat(String columnLabel) {
        Object o = resultList.get(index).get(columnLabel);

        return ((Number) o).floatValue();
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
