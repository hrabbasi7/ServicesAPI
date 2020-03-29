/*
 * Created on Oct 3, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.i2c.component.framework.vo;

/**
 * @author milyas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Screen {

	private Record[] records;
	private int count;

	/**
	 * intialize
	 * intializes the number of Record 's in Screen
	 * @param num
	 */
	public void intilaize(int num) {
		records = new Record[num];
		count = 0;
	} //end initialize

	/**
	 * addRecord
	 * @param record
	 */
	public void addRecord(Record record) {
		records[count++] = record;
	} //end addRecord

	/**
	 * getRecordsCount
	 * Returns the Nuber of Records in the Screen
	 * @return
	 */
	public int getRecordsCount() {
		return count;
	} //end getRecordsCount

	/**
	 * getRecord
	 * Returns Record from a specified location of records array
	 */
	public Record getRecord(int loc) {
		if (loc < 0 || loc > count) {
			return null;
		} else {
			return records[loc];
		}
	} //end getRecord
}