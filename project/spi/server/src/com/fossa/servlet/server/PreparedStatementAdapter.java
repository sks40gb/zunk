/* $Header: /home/common/cvsarea/ibase/dia/src/server/PreparedStatementAdapter.java,v 1.1.10.1 2006/03/22 20:27:15 nancy Exp $ */
package com.fossa.servlet.server;

import java.io.InputStream;
import java.io.Reader;
import java.sql.*;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * An adapter for PreparedStatement.  All methods of PreparedStatement
 * throw UnsupportedOperationException.  Subclasses should override
 * methods which they need to implement.
 * @see Connection#prepareStatement
 */

public class PreparedStatementAdapter implements PreparedStatement {

    protected PreparedStatementAdapter() {}

    /** unsupported operation */
    public ResultSet executeQuery() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int executeUpdate() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setByte(int parameterIndex, byte x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setShort(int parameterIndex, short x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setInt(int parameterIndex, int x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setLong(int parameterIndex, long x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setFloat(int parameterIndex, float x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setDouble(int parameterIndex, double x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException();    }

    /** unsupported operation */
    public void setString(int parameterIndex, String x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setBytes(int parameterIndex, byte x[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setDate(int parameterIndex, java.sql.Date x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setTime(int parameterIndex, java.sql.Time x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setTimestamp(int parameterIndex, java.sql.Timestamp x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setUnicodeStream(int parameterIndex, java.io.InputStream x, 
			  int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setBinaryStream(int parameterIndex, java.io.InputStream x, 
			 int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void clearParameters() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
    throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** setObject(parameterIndex,x,targetSqlType,0) */
    public void setObject(int parameterIndex, Object x, int targetSqlType) 
       throws SQLException {
        setObject(parameterIndex,x,targetSqlType,0);
    }

    /** unsupported operation */
    public void setObject(int parameterIndex, Object x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public boolean execute() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void addBatch() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setCharacterStream(int parameterIndex,
       			  java.io.Reader reader,
			  int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setRef (int i, Ref x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setBlob (int i, Blob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setClob (int i, Clob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setArray (int i, Array x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setTime(int parameterIndex, java.sql.Time x, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setNull (int paramIndex, int sqlType, String typeName) 
    throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setURL(int parameterIndex, java.net.URL x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new UnsupportedOperationException();
    }

    ////////// methods from interface Statement

    /** unsupported operation */
    public ResultSet executeQuery(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int executeUpdate(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }
     
    /** unsupported operation */
    public void close() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int getMaxFieldSize() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    /** unsupported operation */
    public void setMaxFieldSize(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int getMaxRows() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setMaxRows(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setEscapeProcessing(boolean enable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int getQueryTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setQueryTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void cancel() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setCursorName(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }
	
    /** unsupported operation */
    public boolean execute(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }
	
    /** unsupported operation */
    public ResultSet getResultSet() throws SQLException {
        throw new UnsupportedOperationException();
    } 

    /** unsupported operation */
    public int getUpdateCount() throws SQLException {
        throw new UnsupportedOperationException();
    } 

    /** unsupported operation */
    public boolean getMoreResults() throws SQLException {
        throw new UnsupportedOperationException();
    } 

    /** unsupported operation */
    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }
  
    /** unsupported operation */
    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int getResultSetConcurrency() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int getResultSetType()  throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void addBatch( String sql ) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public void clearBatch() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int[] executeBatch() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public Connection getConnection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public boolean getMoreResults(int current) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int executeUpdate(String sql, int columnIndexes[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int executeUpdate(String sql, String columnNames[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public boolean execute(String sql, int columnIndexes[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public boolean execute(String sql, String columnNames[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** unsupported operation */
    public int getResultSetHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {
    }

    public void setNString(int parameterIndex, String value) throws SQLException {
    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
    }

    public void setNClob(int parameterIndex,  Reader reader) throws SQLException {
    }
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    }
    

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    }

    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    }

    public boolean isClosed() throws SQLException {
        return true;
    }

    public void setPoolable(boolean poolable) throws SQLException {
    }

    public boolean isPoolable() throws SQLException {
         return true;
    }

    public Object unwrap(Class iface) throws SQLException {
         Object obj=null;
        return obj;
    }
    
    public boolean isWrapperFor(Class iface) throws SQLException {
         return true;
    }
}
