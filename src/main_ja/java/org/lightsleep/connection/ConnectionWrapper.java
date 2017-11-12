// Connection.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.lightsleep.database.Database;

/**
 * <b>java.sql.Connection</b>をラップします。
 *
 * <p>
 * コネクションに対応するデータベース･ハンドラの参照を持ちます。
 * </p>
 *
 * @since 2.1.0
 *
 * @author Masato Kokubo
 */
public class ConnectionWrapper implements Connection {
	/**
	 * <b>ConnectionWrapper</b>を構築します。
	 *
	 * @param connection ラップされるコネクション
	 * @param database コネクションに関連するデータベース･ハンドラ
	 */
	public ConnectionWrapper(Connection connection, Database database) {
	}

	/**
	 * コネクションを返します。
	 *
	 * @return コネクション
	 */
	public Connection getConnection() {
		return null;
	}

	/**
	 * コネクションに関連するデータベース･ハンドラを返します。
	 *
	 * @return コネクションに関連するデータベース･ハンドラ
	 */
	public Database getDatabase() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Statement createStatement() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String nativeSQL(String sql) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getAutoCommit() throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commit() throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rollback() throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isReadOnly() throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCatalog(String catalog) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCatalog() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTransactionIsolation(int level) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTransactionIsolation() throws SQLException {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearWarnings() throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHoldability(int holdability) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHoldability() throws SQLException {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Savepoint setSavepoint() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Clob createClob() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Blob createBlob() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NClob createNClob() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(int timeout) throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getClientInfo(String name) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Properties getClientInfo() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSchema(String schema) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSchema() throws SQLException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void abort(Executor executor) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNetworkTimeout() throws SQLException {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return null;
	}
}
