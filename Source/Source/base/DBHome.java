/*
 * Created on Jan 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.base;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface DBHome {
	public abstract int delete(String whereClause, Connection con)
		throws SQLException;
	public abstract int update(
		String whereClause,
		BaseForm form,
		Connection con)
		throws SQLException;
	public abstract int insert(BaseForm form, Connection con)
		throws SQLException;
	public abstract BaseHome getInstance(java.util.Hashtable cRow);
	public abstract String getSqlStart();
	public abstract String getSqlEnd();
	public abstract String getSqlGroupby();
	public abstract Hashtable getData(
		int rows,
		int navigationnum,
		Connection conn,
		String qry,
		ArrayList uniqueIdList,
		HttpServletRequest request)
		throws Exception;
	public abstract int getTotalRows();
	public abstract ArrayList populateList(
		Connection conn,
		String dbQuery,
		String stName,
		String stValue)
		throws Exception;
	
		
}