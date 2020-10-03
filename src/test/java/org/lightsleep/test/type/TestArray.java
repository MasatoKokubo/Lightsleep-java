// ClassArray.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.type;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.lightsleep.helper.Utils;

/**
 * This is a class that implements Array.
 *
 * @author Masato Kokubo
 */
public class TestArray implements Array {
    private Object array;

    public TestArray(Object array) {
        this.array = array;
    }

    @Override
    public String toString() {
        return Utils.toLogString(array);
    }

    @Override
    public String getBaseTypeName() throws SQLException {
        return null;
    }

    @Override
    public int getBaseType() throws SQLException {
        return 0;
    }

    @Override
    public Object getArray() throws SQLException {
        return array;
    }

    @Override
    public Object getArray(Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public Object getArray(long index, int count) throws SQLException {
        return null;
    }

    @Override
    public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    @Override
    public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public ResultSet getResultSet(long index, int count) throws SQLException {
        return null;
    }

    @Override
    public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    @Override
    public void free() throws SQLException {
    }
}
