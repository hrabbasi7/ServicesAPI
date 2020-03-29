package com.i2c.service.solsparkservice;
import java.util.*;

/**
 * Form bean for a Struts application.
 * Users may access 37 fields on this form:
 * <ul>
 * <li>sponsorSsn - [your comment here]
 * <li>sponsorMailCity - [your comment here]
 * <li>mailZip - [your comment here]
 * <li>billingCity - [your comment here]
 * <li>cardStatus - [your comment here]
 * <li>instProgID - [your comment here]
 * <li>dob - [your comment here]
 * <li>sponsorDob - [your comment here]
 * <li>mailStreet2 - [your comment here]
 * <li>pan - [your comment here]
 * <li>mailStreet1 - [your comment here]
 * <li>dateIssued - [your comment here]
 * <li>name - [your comment here]
 * <li>sponsorMailZip - [your comment here]
 * <li>expDate - [your comment here]
 * <li>billingZip - [your comment here]
 * <li>sponsorMailState - [your comment here]
 * <li>employerID - [your comment here]
 * <li>billingStreet2 - [your comment here]
 * <li>billingStreet1 - [your comment here]
 * <li>sex - [your comment here]
 * <li>email - [your comment here]
 * <li>billingState - [your comment here]
 * <li>mailCity - [your comment here]
 * <li>dateClosed - [your comment here]
 * <li>instProgName - [your comment here]
 * <li>promoCode - [your comment here]
 * <li>dateRegistered - [your comment here]
 * <li>mailState - [your comment here]
 * <li>accountNum - [your comment here]
 * <li>sponsorPhone - [your comment here]
 * <li>sponsorName - [your comment here]
 * <li>sponsorMailStreet2 - [your comment here]
 * <li>memberID - [your comment here]
 * <li>sponsorMailStreet1 - [your comment here]
 * <li>dateActivated - [your comment here]
 * <li>ssn - [your comment here]
 * </ul>
 * @version 	1.0
 * @author
 */
public class CardInfoObj {

	private String sponsorSsn = null;
	private String sponsorMailCity = null;
	private String mailZip = null;
	private String billingCity = null;
	private String cardStatus = null;
	private String instProgID = null;
	private String dob = null;
	private String mailStreet2 = null;
	private String sponsorDob = null;
	private String pan = null;
	private String mailStreet1 = null;
	private String dateIssued = null;
	private String name = null;
	private String firstName = null;
	private String lastName = null;
	private String middleName = null;
	private String sponsorMailZip = null;
	private String expDate = null;
	private String billingZip = null;
	private String sponsorMailState = null;
	private String employerID = null;
	private String billingStreet2 = null;
	private String billingStreet1 = null;
	private String sex = null;
	private String email = null;
	private String billingState = null;
	private String mailCity = null;
	private String dateClosed = null;
	private String instProgName = null;
	private String promoCode = null;
	private String dateRegistered = null;
	private String mailState = null;
	private String accountNum = null;
	private String sponsorPhone = null;
	private String sponsorName = null;
	private String sponsorMailStreet2 = null;
	private String memberID = null;
	private String sponsorMailStreet1 = null;
	private String dateActivated = null;
	private String ssn = null;
	private String batchNo = null;
	private boolean isCardNumExist = false;
	/**
	 * Get BatchNo
	 * @return String
	 */
	public String getBatchNo() {
		return batchNo;
	}

	/**
	 * Set BatchNo
	 * @param <code>String</code>
	 */
	public void setBatchNo(String s) {
		this.batchNo = s;
	}

	/**
	 * Get FirstName
	 * @return String
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Set FirstName
	 * @param <code>String</code>
	 */
	public void setFirstName(String s) {
		this.firstName = s;
	}

	/**
	 * Get LastName
	 * @return String
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Set LastName
	 * @param <code>String</code>
	 */
	public void setLastName(String s) {
		this.lastName = s;
	}

	/**
	 * Get MiddleName
	 * @return String
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * Set MiddleName
	 * @param <code>String</code>
	 */
	public void setMiddleName(String s) {
		this.middleName = s;
	}


	/**
	 * Get sponsorSsn
	 * @return String
	 */
	public String getSponsorSsn() {
		return sponsorSsn;
	}

	/**
	 * Set sponsorSsn
	 * @param <code>String</code>
	 */
	public void setSponsorSsn(String s) {
		this.sponsorSsn = s;
	}

	/**
	 * Get sponsorMailCity
	 * @return String
	 */
	public String getSponsorMailCity() {
		return sponsorMailCity;
	}

	/**
	 * Set sponsorMailCity
	 * @param <code>String</code>
	 */
	public void setSponsorMailCity(String s) {
		this.sponsorMailCity = s;
	}

	/**
	 * Get mailZip
	 * @return String
	 */
	public String getMailZip() {
		return mailZip;
	}

	/**
	 * Set mailZip
	 * @param <code>String</code>
	 */
	public void setMailZip(String m) {
		this.mailZip = m;
	}

	/**
	 * Get billingCity
	 * @return String
	 */
	public String getBillingCity() {
		return billingCity;
	}

	/**
	 * Set billingCity
	 * @param <code>String</code>
	 */
	public void setBillingCity(String b) {
		this.billingCity = b;
	}

	/**
	 * Get cardStatus
	 * @return String
	 */
	public String getCardStatus() {
		return cardStatus;
	}

	/**
	 * Set cardStatus
	 * @param <code>String</code>
	 */
	public void setCardStatus(String c) {
		this.cardStatus = c;
	}

	/**
	 * Get instProgID
	 * @return String
	 */
	public String getInstProgID() {
		return instProgID;
	}

	/**
	 * Set instProgID
	 * @param <code>String</code>
	 */
	public void setInstProgID(String i) {
		this.instProgID = i;
	}

	/**
	 * Get dob
	 * @return String
	 */
	public String getDob() {
		return dob;
	}

	/**
	 * Set dob
	 * @param <code>String</code>
	 */
	public void setDob(String d) {
		this.dob = d;
	}

	/**
	 * Get mailStreet2
	 * @return String
	 */
	public String getMailStreet2() {
		return mailStreet2;
	}

	/**
	 * Set mailStreet2
	 * @param <code>String</code>
	 */
	public void setMailStreet2(String m) {
		this.mailStreet2 = m;
	}

	/**
	 * Get sponsorDob
	 * @return String
	 */
	public String getSponsorDob() {
		return sponsorDob;
	}

	/**
	 * Set sponsorDob
	 * @param <code>String</code>
	 */
	public void setSponsorDob(String s) {
		this.sponsorDob = s;
	}

	/**
	 * Get pan
	 * @return String
	 */
	public String getPan() {
		return pan;
	}

	/**
	 * Set pan
	 * @param <code>String</code>
	 */
	public void setPan(String p) {
		this.pan = p;
	}

	/**
	 * Get mailStreet1
	 * @return String
	 */
	public String getMailStreet1() {
		return mailStreet1;
	}

	/**
	 * Set mailStreet1
	 * @param <code>String</code>
	 */
	public void setMailStreet1(String m) {
		this.mailStreet1 = m;
	}

	/**
	 * Get dateIssued
	 * @return String
	 */
	public String getDateIssued() {
		return dateIssued;
	}

	/**
	 * Set dateIssued
	 * @param <code>String</code>
	 */
	public void setDateIssued(String d) {
		this.dateIssued = d;
	}

	/**
	 * Get name
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name
	 * @param <code>String</code>
	 */
	public void setName(String n) {
		this.name = n;
		getTokenizeName(this.name);
	}

	/**
	 * Get sponsorMailZip
	 * @return String
	 */
	public String getSponsorMailZip() {
		return sponsorMailZip;
	}

	/**
	 * Set sponsorMailZip
	 * @param <code>String</code>
	 */
	public void setSponsorMailZip(String s) {
		this.sponsorMailZip = s;
	}

	/**
	 * Get expDate
	 * @return String
	 */
	public String getExpDate() {
		return expDate;
	}

	/**
	 * Set expDate
	 * @param <code>String</code>
	 */
	public void setExpDate(String e) {
		this.expDate = e;
	}

	/**
	 * Get billingZip
	 * @return String
	 */
	public String getBillingZip() {
		return billingZip;
	}

	/**
	 * Set billingZip
	 * @param <code>String</code>
	 */
	public void setBillingZip(String b) {
		this.billingZip = b;
	}

	/**
	 * Get sponsorMailState
	 * @return String
	 */
	public String getSponsorMailState() {
		return sponsorMailState;
	}

	/**
	 * Set sponsorMailState
	 * @param <code>String</code>
	 */
	public void setSponsorMailState(String s) {
		this.sponsorMailState = s;
	}

	/**
	 * Get employerID
	 * @return String
	 */
	public String getEmployerID() {
		return employerID;
	}

	/**
	 * Set employerID
	 * @param <code>String</code>
	 */
	public void setEmployerID(String e) {
		this.employerID = e;
	}

	/**
	 * Get billingStreet2
	 * @return String
	 */
	public String getBillingStreet2() {
		return billingStreet2;
	}

	/**
	 * Set billingStreet2
	 * @param <code>String</code>
	 */
	public void setBillingStreet2(String b) {
		this.billingStreet2 = b;
	}

	/**
	 * Get billingStreet1
	 * @return String
	 */
	public String getBillingStreet1() {
		return billingStreet1;
	}

	/**
	 * Set billingStreet1
	 * @param <code>String</code>
	 */
	public void setBillingStreet1(String b) {
		this.billingStreet1 = b;
	}

	/**
	 * Get sex
	 * @return String
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * Set sex
	 * @param <code>String</code>
	 */
	public void setSex(String s) {
		this.sex = s;
	}

	/**
	 * Get email
	 * @return String
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Set email
	 * @param <code>String</code>
	 */
	public void setEmail(String e) {
		this.email = e;
	}

	/**
	 * Get billingState
	 * @return String
	 */
	public String getBillingState() {
		return billingState;
	}

	/**
	 * Set billingState
	 * @param <code>String</code>
	 */
	public void setBillingState(String b) {
		this.billingState = b;
	}

	/**
	 * Get mailCity
	 * @return String
	 */
	public String getMailCity() {
		return mailCity;
	}

	/**
	 * Set mailCity
	 * @param <code>String</code>
	 */
	public void setMailCity(String m) {
		this.mailCity = m;
	}

	/**
	 * Get dateClosed
	 * @return String
	 */
	public String getDateClosed() {
		return dateClosed;
	}

	/**
	 * Set dateClosed
	 * @param <code>String</code>
	 */
	public void setDateClosed(String d) {
		this.dateClosed = d;
	}

	/**
	 * Get instProgName
	 * @return String
	 */
	public String getInstProgName() {
		return instProgName;
	}

	/**
	 * Set instProgName
	 * @param <code>String</code>
	 */
	public void setInstProgName(String i) {
		this.instProgName = i;
	}

	/**
	 * Get promoCode
	 * @return String
	 */
	public String getPromoCode() {
		return promoCode;
	}

	/**
	 * Set promoCode
	 * @param <code>String</code>
	 */
	public void setPromoCode(String p) {
		this.promoCode = p;
	}

	/**
	 * Get dateRegistered
	 * @return String
	 */
	public String getDateRegistered() {
		return dateRegistered;
	}

	/**
	 * Set dateRegistered
	 * @param <code>String</code>
	 */
	public void setDateRegistered(String d) {
		this.dateRegistered = d;
	}

	/**
	 * Get mailState
	 * @return String
	 */
	public String getMailState() {
		return mailState;
	}

	/**
	 * Set mailState
	 * @param <code>String</code>
	 */
	public void setMailState(String m) {
		this.mailState = m;
	}

	/**
	 * Get accountNum
	 * @return String
	 */
	public String getAccountNum() {
		return accountNum;
	}

	/**
	 * Set accountNum
	 * @param <code>String</code>
	 */
	public void setAccountNum(String a) {
		this.accountNum = a;
	}

	/**
	 * Get sponsorPhone
	 * @return String
	 */
	public String getSponsorPhone() {
		return sponsorPhone;
	}

	/**
	 * Set sponsorPhone
	 * @param <code>String</code>
	 */
	public void setSponsorPhone(String s) {
		this.sponsorPhone = s;
	}

	/**
	 * Get sponsorName
	 * @return String
	 */
	public String getSponsorName() {
		return sponsorName;
	}

	/**
	 * Set sponsorName
	 * @param <code>String</code>
	 */
	public void setSponsorName(String s) {
		this.sponsorName = s;
	}

	/**
	 * Get sponsorMailStreet2
	 * @return String
	 */
	public String getSponsorMailStreet2() {
		return sponsorMailStreet2;
	}

	/**
	 * Set sponsorMailStreet2
	 * @param <code>String</code>
	 */
	public void setSponsorMailStreet2(String s) {
		this.sponsorMailStreet2 = s;
	}

	/**
	 * Get memberID
	 * @return String
	 */
	public String getMemberID() {
		return memberID;
	}

	/**
	 * Set memberID
	 * @param <code>String</code>
	 */
	public void setMemberID(String m) {
		this.memberID = m;
	}

	/**
	 * Get sponsorMailStreet1
	 * @return String
	 */
	public String getSponsorMailStreet1() {
		return sponsorMailStreet1;
	}

	/**
	 * Set sponsorMailStreet1
	 * @param <code>String</code>
	 */
	public void setSponsorMailStreet1(String s) {
		this.sponsorMailStreet1 = s;
	}

	/**
	 * Get dateActivated
	 * @return String
	 */
	public String getDateActivated() {
		return dateActivated;
	}

	/**
	 * Set dateActivated
	 * @param <code>String</code>
	 */
	public void setDateActivated(String d) {
		this.dateActivated = d;
	}

	/**
	 * Get ssn
	 * @return String
	 */
	public String getSsn() {
		return ssn;
	}

	/**
	 * Set ssn
	 * @param <code>String</code>
	 */
	public void setSsn(String s) {
		this.ssn = s;
	}

	private void getTokenizeName(String name) {
		String middleName = "";
		boolean firstFlag = false;
		String value = null;
		try {
			StringTokenizer stk = new StringTokenizer(name," ",true);
			while (stk.hasMoreTokens()) {
			 value = stk.nextToken();
			 //Setting the First Token
			 if (!firstFlag && value.trim().length() > 0)
				firstFlag = true;
			//Setting the First Name
			if (firstFlag && getFirstName() == null) {
				setFirstName(value);
			} else if (stk.countTokens() == 0) { //Setting the Last Name
				setLastName(value);
			} else {
				middleName += value;
			  }//end else
			}//end while
			//Setting Middle Name
			if (firstFlag) {
				setMiddleName(middleName.trim());
			}//end oif
		} catch (Exception e) {
		  System.out.println(" Exception -->"+e);
		}
	}//end method
	/**
	 * @return
	 */
	public boolean isCardNumExist() {
		return isCardNumExist;
	}

	/**
	 * @param b
	 */
	public void setCardNumExist(boolean b) {
		isCardNumExist = b;
	}

}
