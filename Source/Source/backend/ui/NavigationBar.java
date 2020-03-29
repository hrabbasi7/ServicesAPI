/*
 * Created on Mar 7, 2006
 * Author: Muhammad ILYAS
 * Company: IPL
 * Project|Module: I2C -
 * Description: This class deals with the Navigation Bar
 * A Single object will be in session|request to handle the Navigation States
 */
package com.i2c.component.backend.ui;

import java.io.Serializable;

import com.i2c.cards.security.PermissionInfoObj;

public class NavigationBar implements Serializable {

	private boolean commit = false;
	private boolean insert = true;
	private boolean delete = true;
	private boolean copy = true;
	private boolean update = true;
	private boolean search = true;
	private boolean first = true;
	private boolean next = true;
	private boolean last = true;
	private boolean previous = true;
	private boolean goTo = true;
	private boolean browse = true;
	private boolean cancel = false;
	private boolean history = false;
	private boolean unlock = false;
	private boolean lock = false;

	/**
	 * @return
	 */
	public boolean isBrowse() {
		return browse;
	}

	/**
	 * @return
	 */
	public boolean isCommit() {
		return commit;
	}

	/**
	 * @return
	 */
	public boolean isCopy() {
		return copy;
	}

	/**
	 * @return
	 */
	public boolean isDelete() {
		return delete;
	}

	/**
	 * @return
	 */
	public boolean isFirst() {
		return first;
	}

	/**
	 * @return
	 */
	public boolean isGoTo() {
		return goTo;
	}

	/**
	 * @return
	 */
	public boolean isInsert() {
		return insert;
	}

	/**
	 * @return
	 */
	public boolean isLast() {
		return last;
	}

	/**
	 * @return
	 */
	public boolean isNext() {
		return next;
	}

	/**
	 * @return
	 */
	public boolean isPrevious() {
		return previous;
	}

	/**
	 * @return
	 */
	public boolean isSearch() {
		return search;
	}

	/**
	 * @return
	 */
	public boolean isUpdate() {
		return update;
	}

	/**
	 * @param b
	 */
	public void setBrowse(boolean b) {
		browse = b;
	}

	/**
	 * @param b
	 */
	public void setCommit(boolean b) {
		commit = b;
	}

	/**
	 * @param b
	 */
	public void setCopy(boolean b) {
		copy = b;
	}

	/**
	 * @param b
	 */
	public void setDelete(boolean b) {
		delete = b;
	}

	/**
	 * @param b
	 */
	public void setFirst(boolean b) {
		first = b;
	}

	/**
	 * @param b
	 */
	public void setGoTo(boolean b) {
		goTo = b;
	}

	/**
	 * @param b
	 */
	public void setInsert(boolean b) {
		insert = b;
	}

	/**
	 * @param b
	 */
	public void setLast(boolean b) {
		last = b;
	}

	/**
	 * @param b
	 */
	public void setNext(boolean b) {
		next = b;
	}

	/**
	 * @param b
	 */
	public void setPrevious(boolean b) {
		previous = b;
	}

	/**
	 * @param b
	 */
	public void setSearch(boolean b) {
		search = b;
	}

	/**
	 * @param b
	 */
	public void setUpdate(boolean b) {
		update = b;
	}
	/**
	 * @return
	 */
	public boolean isCancel() {
		return cancel;
	}

	/**
	 * @param b
	 */
	public void setCancel(boolean b) {
		cancel = b;
	}

	/**
	 * isEnabled
	 * returns the specified button state
	 * @param name
	 * @return
	 */
	public boolean isEnabled(String name) {

		if (name.equalsIgnoreCase("COMMIT")) {
			return isCommit();
		} else if (name.equalsIgnoreCase("INSERT")) {
			return isInsert();
		} else if (name.equalsIgnoreCase("DELETE")) {
			return isDelete();
		} else if (name.equalsIgnoreCase("COPY")) {
			return isCopy();
		} else if (name.equalsIgnoreCase("UPDATE")) {
			return isUpdate();
		} else if (name.equalsIgnoreCase("SEARCH")) {
			return isSearch();
		} else if (name.equalsIgnoreCase("FIRST")) {
			return isFirst();
		} else if (name.equalsIgnoreCase("LAST")) {
			return isLast();
		} else if (name.equalsIgnoreCase("PREVIOUS")) {
			return isPrevious();
		} else if (name.equalsIgnoreCase("GOTO")) {
			return isGoTo();
		} else if (name.equalsIgnoreCase("BROWSE")) {
			return isBrowse();
		} else if (name.equalsIgnoreCase("CANCEL")) {
			return isCancel();
		} else if (name.equalsIgnoreCase("NEXT")) {
			return isNext();
		} else if (name.equalsIgnoreCase("HISTORY")) {
			return isHistory();
		} else if (name.equalsIgnoreCase("UNLOCK")) {
			return isUnlock();
		} else if (name.equalsIgnoreCase("LOCK")) {
			return isLock();
		}

		return false;
	}

	/**
	 * @param string
	 * setting the whole navigation bar state
	 * sequence is very important
	 * 1- commit. 2- cancel. 3- insert. 4- copy. 5- delete. 6- search.
	 * 7- browse. 8- first. 9- previous. 10- next. 11- last. 12- goTo. 13- update
	 * 0- false. 1- true. 2- no change.
	 */
	public void setNavigationState(String string) {
		char ch[] = string.toCharArray();
		int index = 0;
		if (ch.length == 13) {
			if (ch[index] == '1')
				commit = true;
			else if (ch[index] == '0')
				commit = false;
			index++;
			if (ch[index] == '1')
				cancel = true;
			else if (ch[index] == '0')
				cancel = false;
			index++;
			if (ch[index] == '1')
				insert = true;
			else if (ch[index] == '0')
				insert = false;
			index++;
			if (ch[index] == '1')
				copy = true;
			else if (ch[index] == '0')
				copy = false;
			index++;
			if (ch[index] == '1')
				delete = true;
			else if (ch[index] == '0')
				delete = false;
			index++;
			if (ch[index] == '1')
				search = true;
			else if (ch[index] == '0')
				search = false;
			index++;
			if (ch[index] == '1')
				browse = true;
			else if (ch[index] == '0')
				browse = false;
			index++;
			if (ch[index] == '1')
				first = true;
			else if (ch[index] == '0')
				first = false;
			index++;
			if (ch[index] == '1')
				previous = true;
			else if (ch[index] == '0')
				previous = false;
			index++;
			if (ch[index] == '1')
				next = true;
			else if (ch[index] == '0')
				next = false;
			index++;
			if (ch[index] == '1')
				last = true;
			else if (ch[index] == '0')
				last = false;
			index++;
			if (ch[index] == '1')
				goTo = true;
			else if (ch[index] == '0')
				goTo = false;
			index++;
			if (ch[index] == '1')
				update = true;
			else if (ch[index] == '0')
				update = false;
		}
	} // end method

	/**
	 * Getting same new Object
	 */
	public Object clone() {

		NavigationBar navBar = new NavigationBar();
		navBar.setCommit(this.isCommit());
		navBar.setCancel(this.isCancel());
		navBar.setSearch(this.isSearch());
		navBar.setInsert(this.isInsert());
		navBar.setCopy(this.isCopy());
		navBar.setBrowse(this.isBrowse());
		navBar.setDelete(this.isDelete());
		navBar.setFirst(this.isFirst());
		navBar.setLast(this.isLast());
		navBar.setPrevious(this.isPrevious());
		navBar.setNext(this.isNext());
		navBar.setGoTo(this.isGoTo());
		navBar.setUpdate(this.isUpdate());

		return navBar;
	}

	/**
	 * copy object params to the other
	 * @param navBar_Prev
	 */
	public void copy(NavigationBar navBar_Prev) {

		this.setCommit(navBar_Prev.isCommit());
		this.setCancel(navBar_Prev.isCancel());
		this.setSearch(navBar_Prev.isSearch());
		this.setInsert(navBar_Prev.isInsert());
		this.setCopy(navBar_Prev.isCopy());
		this.setBrowse(navBar_Prev.isBrowse());
		this.setDelete(navBar_Prev.isDelete());
		this.setFirst(navBar_Prev.isFirst());
		this.setLast(navBar_Prev.isLast());
		this.setPrevious(navBar_Prev.isPrevious());
		this.setNext(navBar_Prev.isNext());
		this.setGoTo(navBar_Prev.isGoTo());
		this.setUpdate(navBar_Prev.isUpdate());

	}// end method

	/**
	 * Set Permission Dependencies according to PermissionInfoObj
	 * @param obj
	 */
	public void setPermissionDependencies(PermissionInfoObj permissionInfoObj) {

		if(permissionInfoObj!=null && !permissionInfoObj.getIsDeleteAllowed())
		{
			setDelete(false);
		}
		if(permissionInfoObj!=null && !permissionInfoObj.getIsEditAllowed())
		{
			setUpdate(false);
		}
		if(permissionInfoObj!=null && !permissionInfoObj.getIsInsertAllowed())
		{
			setInsert(false);
			setCopy(false);
		}
		if(permissionInfoObj!=null && !permissionInfoObj.getIsViewAllowed())
		{
			setSearch(false);
			setFirst(false);
			setNext(false);
			setPrevious(false);
			setLast(false);
			setGoTo(false);
			setBrowse(false);
		}
	}// end method

	/**
	 * @return
	 */
	public boolean isHistory() {
		return history;
	}

	/**
	 * @param b
	 */
	public void setHistory(boolean b) {
		history = b;
	}

	/**
	 * @return
	 */
	public boolean isUnlock() {
		return unlock;
	}

	/**
	 * @param b
	 */
	public void setUnlock(boolean b) {
		unlock = b;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

}
