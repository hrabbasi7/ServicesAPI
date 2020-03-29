/*
 * Created on Jan 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.base;

import java.sql.*;
import java.util.*;
import com.i2c.cards.util.*;
import com.i2c.component.backend.ui.*;
import com.i2c.component.util.ComponentConstants;
import com.i2c.component.util.ComponentsUtil;
import com.i2c.util.CommonUtilities;
import com.ibm.db.beans.DBTableModel;

import javax.servlet.http.HttpServletRequest;
/**
 * @author iahmad
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class BaseHome implements DBHome {

	/**
	 *
	 */
	protected int totalRows;
	protected Navigation nvHandler;
	private Hashtable uniqueIdTable = new Hashtable();
	private Hashtable columnMappingTable = null;
	private Hashtable formatMappingTable = null;
	private ArrayList messageKeys = new ArrayList();
	private ArrayList errorKeys = new ArrayList();
	private ArrayList objectErrorKeys = new ArrayList();
	private ArrayList objectMessageKeys = new ArrayList();
	private ArrayList errors = new ArrayList();
	private ArrayList messages = new ArrayList();
	ArrayList serial = null;
	public BaseHome() {
		super();
		// TODO Auto-generated constructor stub
	}
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
	public String getCountQuery(){return "";}

	// For Grid Active Components
	public Hashtable getActiveComponents(String key_col_name, int rows, int navigationnum, Connection conn){return null;}

	public String getSqlGroupby(){return null;}


	public int getSqlCount(String qry, Connection conn) {
		int i = 0;
		Statement stmt=null;
		ResultSet rSet=null;
		try {
			stmt = conn.createStatement();
			rSet = stmt.executeQuery(qry);
			//System.out.println("QRY FOR COUNT"+qry);
			if (rSet.next()) {
				String s = rSet.getString(1);
				totalRows = Integer.parseInt(s.trim());
				return Integer.parseInt(s.trim());
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			System.out.println(qry);
			totalRows = 0;
			return 0;
		} finally {
			ComponentsUtil.releaseResources(rSet, stmt);
		}
		return i;
	}


	/**
	 * Overloaded getData Method That takes one extra parameter of BaseForm to set
	 * some necessary DB Related Parameters.
	 * @param rows
	 * @param navigationnum
	 * @param conn
	 * @param qry
	 * @param uniqueIdList
	 * @param request
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public Hashtable getData(
		int rows,
		int navigationnum,
		Connection conn,
		String qry,
		ArrayList uniqueIdList,
		HttpServletRequest request,
		BaseForm form
		)
		throws Exception {

		Hashtable dataTable = this.getData(rows,navigationnum,conn,qry,uniqueIdList,request);
		// For Rendering Table Name and dbRecNo on Screen
		Object dbRecNoObj = dataTable.get("rec_no");
		String dbRecNo = null;
		if(dbRecNoObj != null)
		dbRecNo = dbRecNoObj.toString();
		form.setDbRecNo(dbRecNo);
		String tableName = ComponentsUtil.getTableNameFromQuery(qry);
		form.setDbTableName(tableName);
		// End
		return dataTable;
		}


	public Hashtable getData(
		int rows,
		int navigationnum,
		Connection conn,
		String qry,
		ArrayList uniqueIdList,
		HttpServletRequest request
		)
		throws Exception {

		Hashtable dataTable = null;

		ArrayList uniqueIdList_column = new ArrayList();
		ArrayList uniqueIdList_value = new ArrayList();
		Statement stmt =
			conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = null;
		System.out.println("Danish Printing qry = " + qry);
		try{
			rs = stmt.executeQuery(qry);
		System.out.println("After Executing Query ");
		//rs.last();
		//totalRows = rs.getRow();
		//rs.beforeFirst();
		if (rows == 1 || rows == 0) {
			dataTable = getOneRow(rs, navigationnum, request);

			if (uniqueIdList != null) {
				String s[] = null;
				for (int i = 0; i < uniqueIdList.size(); i++) {
					s = (String[]) uniqueIdList.get(i);
					if (s != null && s.length == 2) {
						if (s[0] != null
							&& s[1] != null
							&& dataTable != null) {
							//System.out.println("ISTIKHAR AHMAD "+s[0]+" "+Constants.getValue(dataTable.get(s[0]),s[1]));
							if (dataTable.get(s[0]) != null)
								uniqueIdTable.put(
									s[0],
									Constants.getValue(
										dataTable.get(s[0]),
										s[1]));

						}
					}
				}
				if (this.columnMappingTable != null
					&& this.columnMappingTable.size() > 0)
					dataTable = applyDBFieldMapping(dataTable);
				if (this.formatMappingTable != null
					&& this.formatMappingTable.size() > 0)
					dataTable = applyFormat(dataTable);
				dataTable.put(ComponentConstants.UNIQUEIDLIST, uniqueIdTable);
			}
		} else {

			//System.out.println(" navigationnum [" + navigationnum + "]");
			//System.out.println(
			//				" (navigationnum+rows) [" + (navigationnum + rows) + "]");
			//			System.out.println(" rows [" + rows + "]");
			//			System.out.println(" TotalRows [" + totalRows + "]");
			rows =
				(navigationnum + rows) <= totalRows
					? (navigationnum + rows)
					: totalRows;
			//			System.out.println(" rows [" + rows + "]");
			//			System.out.println(" TotalRows [" + totalRows + "]");
			dataTable = new Hashtable();
			ArrayList dataList = new ArrayList();
			serial = new ArrayList();
			ArrayList uniqueIdList3 = new ArrayList();
			StringBuffer buf = new StringBuffer();
			for (int iRows = navigationnum; iRows <= rows; iRows++) {
				buf = new StringBuffer();
				if (uniqueIdList != null) {
					String s[] = null;
					for (int i = 0; i < uniqueIdList.size(); i++) {
						s = (String[]) uniqueIdList.get(i);
						//						if(s!=null&&s.length==3) {
						//							//--We may need to hide the actual Name of columns from the scene.
						//							buf.append("{"+s[2]+"="+Constants.getBrowseValue(getOneRow(rs, iRows).get(s[0]),s[1])+"}");
						//						}else{
						if (s != null && s.length == 2) {
							if (s[0] != null && s[1] != null)
								buf.append(
									"{"
										+ s[0]
										+ "="
										+ Constants.getBrowseValue(
											getOneRow(rs, iRows, request).get(s[0]),
											s[1])
										+ "}");
						}
						//}
					}
				}
				System.out.println("Buffer String = " + buf.toString());
				uniqueIdList_value.add(buf.toString());
				buf.setLength(0);
				buf = null;
				dataList.add(getMultipleRows(rs, iRows));
				if (serial != null)
					this.serial.add(String.valueOf(iRows));
			}
			if (dataList.size() > 0)
				dataTable.put(Constants.GRID, dataList);
			if (serial != null && serial.size() > 0)
				dataTable.put(Constants.SERIAL, serial);

			//			System.out.println(dataList.size() +" = "+ uniqueIdList_value.size());
			uniqueIdTable.put(Constants.UNIQUEID_COLUMN, uniqueIdList_column);
			uniqueIdTable.put(Constants.UNIQUEID_VALUE, uniqueIdList_value);

			if (uniqueIdList != null) {
				dataTable.put(Constants.UNIQUEIDLIST, uniqueIdTable);
			}
		}
//		rs.close();
//		stmt.close();
		}catch(Exception ex){
			ex.printStackTrace();
//			rs = null;
		}
		finally {
			ComponentsUtil.releaseResources(rs,stmt);
		}
		return dataTable;
	}
			
	private Hashtable getOneRow(ResultSet rs, int rowNum, HttpServletRequest request) throws Exception {
		Hashtable htable = null;
		Vector colms = null;
		if (rs != null) {
			htable = new Hashtable();
			colms = new Vector();
			ResultSetMetaData rmdata = rs.getMetaData();
			int colc = rmdata.getColumnCount();
			for (int col = 1; col <= colc; col++) {
				colms.add(rmdata.getColumnName(col));
			}
			if (rowNum > 0) {
				if (rs.absolute(rowNum)) {
					for (int data = 0; data < colms.size(); data++) {
						htable.put(
							colms.get(data),
								CommonUtilities.decryptValue(
								request,
								getValue(rs.getString(data + 1)
								)
							)
						);
					}
				}
			} else {
				if (rs.next()) {
					for (int data = 0; data < colms.size(); data++) {
						htable.put(
							colms.get(data),
							CommonUtilities.decryptValue(
							request,
							getValue(rs.getString(data + 1)
							)
						)
					  );
					}
				}
			}
		}
		//System.out.println(htable);
		return htable;
	}
	private ArrayList getMultipleRows(ResultSet rs, int rowNum)
		throws Exception {
		ArrayList list = null;
		Vector colms = null;
		if (rs != null) {
			list = new ArrayList();
			colms = new Vector();
			ResultSetMetaData rmdata = rs.getMetaData();
			//System.out.println(rmdata.getTableName(1));
			int colc = rmdata.getColumnCount();
			for (int col = 1; col <= colc; col++) {
				colms.add(rmdata.getColumnName(col));
			}

			//System.out.println("[ colms ]" + colms);

			if (rowNum > 0) {
				if (rs.absolute(rowNum)) {
					for (int data = 0; data < colms.size(); data++) {
						list.add(applyFormat4Grid(colms.get(data),applyDBFieldMapping4GRID(colms.get(data),getValue(rs.getString(data + 1)))));
					}
				}
			} else {
				if (rs.next()) {
					for (int data = 0; data < colms.size(); data++) {
						list.add(getValue(rs.getString(data + 1)));
					}
				}
			}
		}
		//System.out.println("List Size" +list.size() );
		//System.out.println("List Data [ " + list + "]");
		 return list;
	}
	private String getValue(String value) {
		//System.out.print("  "+value);
		if (value == null)
			return "";
		// Commented by M. ILYAS... 17/01/2006
		// Because of this any string containing NULL in it is lost on screen
		/*
		if (value.toUpperCase().indexOf("NULL") != -1) {
			return "";
		}
		*/
//		return value; // changed by Danish Lodhi	18-09-2005
		return ComponentsUtil.rightTrim(value); // changed by M. ILYAS... 17/01/2006
//		return value.trim();
	}
	public int getTotalRows() {
		return totalRows;
	}
	public ArrayList populateList(
		Connection conn,
		String dbQuery,
		String stName,
		String stValue)
		throws Exception {

		ArrayList list = new ArrayList();
		Statement stmt = null;
		ResultSet rs =  null;
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(dbQuery);
		
			String data[] = new String[2];
			if (stValue != null) {
				data[0] = stValue;
			} else {
				data[0] = " ";
			}
			if (stName != null) {
				data[1] = stName;
			} else {
				data[1] = " Select value ";
			}
			list.add(data);
			
			while (rs.next()) {
				data = new String[2];
				data[0] = getValue(rs.getString(1));
				data[1] = getValue(rs.getString(2));
				if (data[0] != null
						&& data[1] != null
						&& !data[1].trim().equals("")
						&& !data[0].trim().equals(""))
					list.add(data);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ComponentsUtil.releaseResources(rs,stmt);
		}
		return list;

	}
	protected int executeUpdate(String qry, Connection con)
		throws SQLException {
		//System.out.println(" QUERY : [" + qry + "]");
		Statement stmt = null;
		int rows = -1;
		try {
		stmt = con.createStatement();
		rows = stmt.executeUpdate(qry);
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			ComponentsUtil.releaseResources(stmt);
		}
		return rows;
	}
	

	
	/**
	 * @return
	 */
	public Hashtable getUniqueIdTable() {
		return uniqueIdTable;
	}

	/**
	 * @return
	 */
	public ArrayList getMessageKeys() {
		return messageKeys;
	}

	/**
	 * @param vector
	 */
	public void addMessageKey(String key) {
		if (key != null)
			messageKeys.add(key);

	}
	public void addMessageKey(String key, Object[] obj) {
		if (key != null)
			messages.add(key);
		if (obj != null)
			this.objectMessageKeys.add(obj);

	}

	public void setMessageKeys(ArrayList list) {
		if (list != null)
			messageKeys = list;
	}

	public void setNVHandler(Navigation nvHandler) {
		this.nvHandler = nvHandler;
	}
	/**
	 * @return
	 */
	public ArrayList getErrorKeys() {
		return errorKeys;
	}
	/**
	 * @param vector
	 */
	public void addErrorKey(String key) {
		if (key != null)
			errorKeys.add(key);
	}
	public void addErrorKey(String key, Object[] obj) {
		if (key != null)
			errors.add(key);
		if (obj != null)
			this.objectErrorKeys.add(obj);
	}

	public void setErrorKeys(ArrayList list) {
		if (list != null)
			errorKeys = list;
	}

	public ArrayList getObjectErrorKeys() {
		return this.objectErrorKeys;
	}

	public void setObjectErrorKeys(ArrayList list) {
		this.objectErrorKeys = list;
	}

	public ArrayList getObjectMessageKeys() {
		return this.objectMessageKeys;
	}

	public void setObjectMessageKeys(ArrayList list) {
		this.objectMessageKeys = list;
	}

	/**
	 * @return
	 */
	public ArrayList getErrors() {
		return errors;
	}

	/**
	 * @return
	 */
	public ArrayList getMessages() {
		return messages;
	}

	/**
	 * @param list
	 */
	public void setErrors(ArrayList list) {
		errors = list;
	}

	/**
	 * @param list
	 */
	public void setMessages(ArrayList list) {
		messages = list;
	}

	/**
	 * @return
	 */
	public Navigation getNvHandler() {
		return nvHandler;
	}

	/**
	 * @param navigation
	 */
	public void setNvHandler(Navigation navigation) {
		nvHandler = navigation;
	}

	/**
	 * @param i
	 */
	public void setTotalRows(int i) {
		totalRows = i;
	}
	public int mUpdate(Connection con, Hashtable paramTable, int totalRec) {
		return 0;
	}
	public void setMappingTable(Hashtable table) {
		this.columnMappingTable = table;
	}
	private Hashtable applyDBFieldMapping(Hashtable table) {
		if (this.columnMappingTable != null) {
			if (columnMappingTable.size() > 0) {
				List list = getKeysList(columnMappingTable);
				for (int i = 0; i < list.size(); i++) {
					if (table.get(list.get(i)) != null) {
						List mappingList =
							(List) columnMappingTable.get(list.get(i));
						String[] mapp = null;
						for (int k = 0; k < mappingList.size(); k++) {
							mapp = (String[]) mappingList.get(k);
							if (((String) table.get(list.get(i)))
								.equals(mapp[0])) {
								table.put(list.get(i), mapp[1]);
							}
						}
					}
				}
			}
		}
		return table;
	}
	private Object applyDBFieldMapping4GRID(Object col, Object value) {
		if (this.columnMappingTable != null) {
			if (columnMappingTable.size() > 0) {
				//List list = getKeysList(columnMappingTable);
				if (columnMappingTable.get(col) != null) {
					List mappingList = (List) columnMappingTable.get(col);
					String[] mapp = null;
					for (int k = 0; k < mappingList.size(); k++) {
						mapp = (String[]) mappingList.get(k);
						if (((String) value).equals(mapp[0])) {
							value = mapp[1];
						}
					}
				}
			}
		}
		return value;
	}
	private Hashtable applyFormat(Hashtable table) {
		if (this.formatMappingTable != null) {
			if (formatMappingTable.size() > 0) {
				List list = getKeysList(formatMappingTable);
				for (int i = 0; i < list.size(); i++) {
					if (table.get(list.get(i)) != null) {
						List mappingList = (List) formatMappingTable.get(list.get(i));
						if (table.get(list.get(i)) != null) {
								table.put(list.get(i), Constants.getFormatedValue((String) table.get(list.get(i)), (Object[]) formatMappingTable.get(list.get(i))));
						}
					}
				}
			}
		}
		return table;
	}
	private Object applyFormat4Grid(Object col, Object value) {
		if (this.formatMappingTable != null) {
			if (formatMappingTable.size() > 0) {
				if (formatMappingTable.get(col) != null) {
					value = Constants.getFormatedValue((String)value, (Object[])formatMappingTable.get(col));
				}
			}
		}
		return value;
	}
	private List getKeysList(Hashtable table) {
		List list = new ArrayList();
		Enumeration enumeration = table.keys();
		String key = null;
		while (enumeration.hasMoreElements()) {
			key = (String) enumeration.nextElement();
			//System.out.println("Key :" + key);
			list.add(key);
			//	System.out.println("value :" + table.get(key));
			//  this.getSession().setAttribute(key,);
		}
		return list;
	}
	/**
	 * @param hashtable
	 */
	public void setFormatMappingTable(Hashtable hashtable) {
		formatMappingTable = hashtable;
	}
}
