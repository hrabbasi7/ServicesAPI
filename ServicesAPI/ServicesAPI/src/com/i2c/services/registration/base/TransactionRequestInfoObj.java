package com.i2c.services.registration.base;

/**
 * <p>Title: TransactionRequestInfoObj: This class holds transaction information. </p>
 * <p>Description: This class acts as bean to hold the transaction information</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: I2c Inc</p>
 * @author Agohar
 * @version 1.0
 */

public class TransactionRequestInfoObj {
  private String cardPrgId = null;
  private String cardStartNos = null;
  private String shippingMethod = null;
  private String firstName = null;
  private String middleName = null;
  private String lastName = null;
  private String ssn = null;
  private String emailAddress = null;
  private String address1 = null;
  private String address2 = null;
  private String city = null;
  private String state = null;
  private String zip = null;
  private String dayTimePhone = null;
  private String nightTimePhone = null;
  private String applyDate = null;
  private String dob = null;
  private String drivingLisenseNumber = null;
  private String drivingLisenseState = null;
  private String existingCard = null;
  private String applyFee = null;
  private String transferAmount = null;
  private String initialAmount = null;
  private boolean closeExistingCard = false;
  private boolean fundsFromOldCard = false;
  private String deviceType = null;
  private String cardType = "P";
  private String effectiveDate = null;
  private String reissueType = null;
  private String userId = null;
  private String chId = null;
  private String assignmentNo = null;
  private String stakeHolderId = null;
  private String motherMaidenName = null;
  private String countryCode = null;
  private String homePhone = null;
  private String workPhone = null;
  private String gender = null;
  private String employerId = null;
  private String salesNodeNo = null;
  private String actionType = "01";
  private String ofacStatus = null;
  private String avsStatus = null;
  private String atmStatus = null;
  private String posStatus = null;
  private String deviceId = null;
  private String crdAceptorCode = null;
  private String crdAceptorName = null;
  private String merchantCatCd = null;
  private String retRefNumber = null;
  private String acquirerId = null;
  private String acquirerUserId = null;
  private String acquirerData1 = null;
  private String acquirerData2 = null;
  private String acquirerData3 = null;
  private String hostId = null;
  private String foreignId = null;
  private String foreignIdType = null;
  private String foreignCountryCode = null;
  private String cardGenType = null;
  private String cardLinkType = null;

  private String billingAddress1 = null;
  private String billingAddress2 = null;
  private String billingCity = null;
  private String billingState = null;
  private String billingCountrycode = null;
  private String billingZipCode = null;
  private boolean activateToCard = false;

  private String atmOnlineWithdLimit = null;
  private String atmOflineWithdLimit = null;
  private String posOnlineWithdLimit = null;
  private String posOflineWithdLimit = null;
  private String creditOnlineWithdLimit = null;
  private String creditOflineWithdLimit = null;
  private String dailyAtmDepositMax = null;
  private String fraudInfoFlag = null;
  private String maxDepositLimit = null;
  private String isMainCard = null;
  private String lifeHighAtmAmount = null;
  private String lifeHighPosAmount = null;
  private String lifeHighAvailableBalance = null;
  private String minLoadAmount = null;
  private String maxLoadAmount = null;
  private String minTransAmount = null;
  private String maxTransAmount = null;
  private String sinceZeroBalance = null;
  private String cardNickName = null;
  private String memberId = null;
  private String primaryCardNo = null;
  private String earnedPoints = null;
  private String totalTransAmount = null;
  private String totalCommAmount = null;
  private String totalLostStolen = null;
  private String totalFreeAtmWithds = null;
  private String totalLastDeclines = null;
  private String totalFreeReversals = null;
  private String lastRedeemedDate = null;
  private String confirmedPoints = null;
  private String fortifiedPoints = null;
  private String redeemedPoints = null;
  private String availablePoints = null;
  private String pendingPoints = null;
  private String questionId = null;
  private String questionAnswer = null;
  private boolean doAllCashOutAtActivation = false;



  /**
   * Default Constructor for the bean
   */
  public TransactionRequestInfoObj() {
  }

  /**
   * This method returns the city name
   * @return String
   */
  public String getCity() {
    return city;
  }

  /**
   * This method returns existing card no
   * @return String
   */

  public String getExistingCard() {
    return existingCard;
  }

  /**
   * This method returns the zip code
   * @return String
   */

  public String getZip() {
    return zip;
  }

  /**
   * This method returns state of the card holder
   * @return String
   */

  public String getState() {
    return state;
  }

  /**
   * This method returns the card holder's date of birth
   * @return String
   */
  public String getDob() {
    return dob;
  }

  /**
   * This method returns the apply fee
   * @return String
   */
  public String getApplyFee() {
    return applyFee;
  }

  /**
   * This method returns the amount being transfered
   * @return String
   */
  public String getTransferAmount() {
    return transferAmount;
  }

  /**
   * this method returns the card program
   * @return String
   */
  public String getCardPrgId() {
    return cardPrgId;
  }

  /**
   * This method returns the email address of the card holder
   * @return String
   */
  public String getEmailAddress() {
    return emailAddress;
  }

  /**
   * This method returns the day time phone of the card holder
   * @return String
   */
  public String getDayTimePhone() {
    return dayTimePhone;
  }

  /**
   * This method returns the device type.
   * @return String
   */
  public String getDeviceType() {
    return deviceType;
  }

  /**
   * This method returns the address of the card holder
   * @return String
   */
  public String getAddress2() {
    return address2;
  }

  /**
   * This method returns the last name of the card holder
   * @return String
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * This method returns the first name of the card holder
   * @return String
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * This method returns the address of the card holder
   * @return String
   */
  public String getAddress1() {
    return address1;
  }

  /**
   * This method returns the shipping method of the card
   * @return String
   */
  public String getShippingMethod() {
    return shippingMethod;
  }

  /**
   * This method returns the driving license state of the card holder
   * @return String
   */
  public String getDrivingLisenseState() {
    return drivingLisenseState;
  }

  /**
   * This method returns the driving licencse number of the card holder
   * @return String
   */
  public String getDrivingLisenseNumber() {
    return drivingLisenseNumber;
  }

  /**
   * This method returns the night time phone of the card holder
   * @return String
   */
  public String getNightTimePhone() {
    return nightTimePhone;
  }

  /**
   * This method returns the apply date.
   * @return String
   */

  public String getApplyDate() {
    return applyDate;
  }

  /**
   * This method returns the social security number of the card holder
   * @return String
   */
  public String getSsn() {
    return ssn;
  }

  /**
   * This method sets the existing card no.
   * @param closeExistingCard boolean
   */
  public void closeExistingCard(boolean closeExistingCard) {
    this.closeExistingCard = closeExistingCard;
  }

  /**
   * This methods sets the city of the card holder.
   * @param city String
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * This method sets the existing card no.
   * @param existingCard String
   */
  public void setExistingCard(String existingCard) {
    this.existingCard = existingCard;
  }

  /**
   * This methods sets the ZIP of the card holder.
   * @param zip String
   */
  public void setZip(String zip) {
    this.zip = zip;
  }

  /**
   * This method sets the state of the card holder
   * @param state String
   */
  public void setState(String state) {
    this.state = state;
  }

  /**
   * This method sets the date of birth of the card holder
   * @param dob String
   */
  public void setDob(String dob) {
    this.dob = dob;
  }
  /**
   * this method sets the apply date for the card
   * @param applyFee String
   */
  public void setApplyFee(String applyFee) {
    this.applyFee = applyFee;
  }

  /**
   * This methods sets the amount which is being transfered to the card or from the card.
   * @param transferAmount String
   */
  public void setTransferAmount(String transferAmount) {
    this.transferAmount = transferAmount;
  }

  /**
   * This method sets the card programs id for the card.
   * @param cardPrgId String
   */
  public void setCardPrgId(String cardPrgId) {
    this.cardPrgId = cardPrgId;
  }

  /**
   * This method sets the e-mail address of the card holder
   * @param emailAddress String
   */
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  /**
   * This method sets the day time phone of the card holder
   * @param dayTimePhone String
   */
  public void setDayTimePhone(String dayTimePhone) {
    this.dayTimePhone = dayTimePhone;
  }

  /**
   * This method sets the device type
   * @param deviceType String
   */
  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  /**
   * This method sets the address of the card holder
   * @param address2 String
   */
  public void setAddress2(String address2) {
    this.address2 = address2;
  }

  /**
   * This method sets the last name of the card holder
   * @param lastName String
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * This method sets the first name of the card holder
   * @param firstName String
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * This method sets the address of the card holder
   * @param address1 String
   */
  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  /**
   * This method sets the shipping method for the card
   * @param shippingMethod String
   */
  public void setShippingMethod(String shippingMethod) {
    this.shippingMethod = shippingMethod;
  }

  /**
   * This method sets the driving license state of the card holder
   * @param drivingLisenseState String
   */
  public void setDrivingLisenseState(String drivingLisenseState) {
    this.drivingLisenseState = drivingLisenseState;
  }

  /**
   * This method sets the driving license number
   * @param drivingLisenseNumber String
   */
  public void setDrivingLisenseNumber(String drivingLisenseNumber) {
    this.drivingLisenseNumber = drivingLisenseNumber;
  }

  /**
   * This method sets the night time phone
   * @param nightTimePhone String
   */
  public void setNightTimePhone(String nightTimePhone) {
    this.nightTimePhone = nightTimePhone;
  }

  /**
   * This methods set the apply date
   * @param applyDate String
   */
  public void setApplyDate(String applyDate) {
    this.applyDate = applyDate;
  }

  /**
   * This method sets the social security number of the card holder.
   * @param ssn String
   */
  public void setSsn(String ssn) {
    this.ssn = ssn;
  }
  /**
   * This method sets the middle name of the card holder
   * @param middleName String
   */
  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  /**
   * This method sets the card type
   * @param cardType String
   */
  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  /**
   * This method sets the effective date for the card
   * @param effectiveDate String
   */
  public void setEffectiveDate(String effectiveDate) {
    this.effectiveDate = effectiveDate;
  }

  /**
   * This method sets the reissue type for the card
   * @param reissueType String
   */
  public void setReissueType(String reissueType) {
    this.reissueType = reissueType;
  }

  /**
   * This method sets the home phone of the card holder
   * @param homePhone String
   */
  public void setHomePhone(String homePhone) {
    this.homePhone = homePhone;
  }

  /**
   * This method sets the work phone of the card holder
   * @param workPhone String
   */
  public void setWorkPhone(String workPhone) {
    this.workPhone = workPhone;
  }

  /**
   * This method sets the gender of the card holder
   * @param gender String
   */
  public void setGender(String gender) {
    this.gender = gender;
  }

  /**
   * This method sets the employer id
   * @param employerId String
   */
  public void setEmployerId(String employerId) {
    this.employerId = employerId;
  }

  /**
   * This method sets the country code of the card holder.
   * @param countryCode String
   */
  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  /**
   * This method sets the assignment no.
   * @param assignmentNo String
   */
  public void setAssignmentNo(String assignmentNo) {
    this.assignmentNo = assignmentNo;
  }

  /**
   * This methods sets the user id of the card holder
   * @param userId String
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * This method sets the mother maiden name of the card holder
   * @param motherMaidenName String
   */
  public void setMotherMaidenName(String motherMaidenName) {
    this.motherMaidenName = motherMaidenName;
  }

  /**
   * This method sets the exising close card no.
   * @param closeExistingCard boolean
   */
  public void setCloseExistingCard(boolean closeExistingCard) {
    this.closeExistingCard = closeExistingCard;
  }

  /**
   * This method sets the stake holder of the card
   * @param stakeHolderId String
   */
  public void setStakeHolderId(String stakeHolderId) {
    this.stakeHolderId = stakeHolderId;
  }

  /**
   * This method sets the sales node no
   * @param salesNodeNo String
   */
  public void setSalesNodeNo(String salesNodeNo) {
    this.salesNodeNo = salesNodeNo;
  }

  /**
   * This method sets the action type
   * @param actionType String
   */
  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  /**
   * This methods sets the AVS status of the card
   * @param avsStatus String
   */
  public void setAvsStatus(String avsStatus) {
    this.avsStatus = avsStatus;
  }

  /**
   * This method sets the OFAC status of the card holder
   * @param ofacStatus String
   */
  public void setOfacStatus(String ofacStatus) {
    this.ofacStatus = ofacStatus;
  }

  /**
   * This method sets the POS  status of the card
   * @param posStatus String
   */
  public void setPosStatus(String posStatus) {
    this.posStatus = posStatus;
  }

  /**
   * This method sets the ATM status of the card
   * @param atmStatus String
   */
  public void setAtmStatus(String atmStatus) {
    this.atmStatus = atmStatus;
  }

  public void setInitialAmount(String initialAmount) {
    this.initialAmount = initialAmount;
  }

  public void setAcquirerId(String acquirerId) {
    this.acquirerId = acquirerId;
  }

  public void setAcquirerData3(String acquirerData3) {
    this.acquirerData3 = acquirerData3;
  }

  public void setAcquirerUserId(String acquirerUserId) {
    this.acquirerUserId = acquirerUserId;
  }

  public void setAcquirerData1(String acquirerData1) {
    this.acquirerData1 = acquirerData1;
  }

  public void setCrdAceptorCode(String crdAceptorCode) {
    this.crdAceptorCode = crdAceptorCode;
  }

  public void setRetRefNumber(String retRefNumber) {
    this.retRefNumber = retRefNumber;
  }

  public void setCrdAceptorName(String crdAceptorName) {
    this.crdAceptorName = crdAceptorName;
  }

  public void setMerchantCatCd(String merchantCatCd) {
    this.merchantCatCd = merchantCatCd;
  }

  public void setAcquirerData2(String acquirerData2) {
    this.acquirerData2 = acquirerData2;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public void setForeignIdType(String foreignIdType) {
    this.foreignIdType = foreignIdType;
  }

  public void setForeignId(String foreignId) {
    this.foreignId = foreignId;
  }

  public void setFundsFromOldCard(boolean fundsFromOldCard) {
    this.fundsFromOldCard = fundsFromOldCard;
  }

  public void setCardGenType(String cardGenType) {
    this.cardGenType = cardGenType;
  }

  /**
   * This method returns whether existing card is closed or not
   * @return boolean
   */
  public boolean closeExistingCard() {
    return closeExistingCard;
  }

  /**
   * This method returns the middle name of the card holder
   * @return String
   */
  public String getMiddleName() {
    return middleName;
  }

  /**
   * This method returns the card type
   * @return String
   */
  public String getCardType() {
    return cardType;
  }

  /**
   * This method returns the effective date of the card
   * @return String
   */
  public String getEffectiveDate() {
    return effectiveDate;
  }

  /**
   * This method returns the re-issue type
   * @return String
   */
  public String getReissueType() {
    return reissueType;
  }

  /**
   * This method returns the home phone of the card holder
   * @return String
   */
  public String getHomePhone() {
    return homePhone;
  }

  /**
   * This method returns the work phone of the card holder
   * @return String
   */
  public String getWorkPhone() {
    return workPhone;
  }

  /**
   * This method returns the gender of the card holder
   * @return String
   */
  public String getGender() {
    return gender;
  }

  /**
   * This method returns the employer id
   * @return String
   */
  public String getEmployerId() {
    return employerId;
  }

  /**
   * This method returns the country code of the card holder
   * @return String
   */
  public String getCountryCode() {
    return countryCode;
  }

  /**
   * This method returns the assignment no
   * @return String
   */
  public String getAssignmentNo() {
    return assignmentNo;
  }

  /**
   * This method returns the user id
   * @return String
   */
  public String getUserId() {
    return userId;
  }

  /**
   * This method returns the mother maiden name of the card holder
   * @return String
   */
  public String getMotherMaidenName() {
    return motherMaidenName;
  }

  /**
   * This method returns true or false whetehr card no is closed or not
   * @return boolean
   */
  public boolean isCloseExistingCard() {
    return closeExistingCard;
  }

  /**
   * This method returns the stake holder of the card
   * @return String
   */
  public String getStakeHolderId() {
    return stakeHolderId;
  }
  /**
   * This method returns the sale node no.
   * @return String
   */
  public String getSalesNodeNo() {
    return salesNodeNo;
  }

  /**
   * This method returns the action type
   * @return String
   */
  public String getActionType() {
    return actionType;
  }

  /**
   * This method returns the AVS status of the card
   * @return String
   */
  public String getAvsStatus() {
    return avsStatus;
  }

  /**
   * This method returns the OFAC status of the card
   * @return String
   */
  public String getOfacStatus() {
    return ofacStatus;
  }

  /**
   * This method returns the POS status of the card
   * @return String
   */
  public String getPosStatus() {
    return posStatus;
  }

  /**
   * This method returns the ATM status of the card
   * @return String
   */
  public String getAtmStatus() {
    return atmStatus;
  }

  public String getInitialAmount() {
    return initialAmount;
  }

  public String getAcquirerId() {
    return acquirerId;
  }

  public String getAcquirerData3() {
    return acquirerData3;
  }

  public String getAcquirerUserId() {
    return acquirerUserId;
  }

  public String getAcquirerData1() {
    return acquirerData1;
  }

  public String getCrdAceptorCode() {
    return crdAceptorCode;
  }

  public String getRetRefNumber() {
    return retRefNumber;
  }

  public String getCrdAceptorName() {
    return crdAceptorName;
  }

  public String getMerchantCatCd() {
    return merchantCatCd;
  }

  public String getAcquirerData2() {
    return acquirerData2;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public String getForeignIdType() {
    return foreignIdType;
  }

  public String getForeignId() {
    return foreignId;
  }

  public boolean isFundsFromOldCard() {
    return fundsFromOldCard;
  }

  public String getCardGenType() {
    return cardGenType;
  }

public String getBillingAddress1() {
	return billingAddress1;
}

public void setBillingAddress1(String billingAddress1) {
	this.billingAddress1 = billingAddress1;
}

public String getBillingAddress2() {
	return billingAddress2;
}

public void setBillingAddress2(String billingAddress2) {
	this.billingAddress2 = billingAddress2;
}

public String getBillingCity() {
	return billingCity;
}

public void setBillingCity(String billingCity) {
	this.billingCity = billingCity;
}

public String getBillingCountrycode() {
	return billingCountrycode;
}

public void setBillingCountrycode(String billingCountrycode) {
	this.billingCountrycode = billingCountrycode;
}

public String getBillingState() {
	return billingState;
}

public void setBillingState(String billingState) {
	this.billingState = billingState;
}

public String getBillingZipCode() {
	return billingZipCode;
}

    public boolean isActivateToCard() {
        return activateToCard;
    }

    public String getCardStartNos() {
        return cardStartNos;
    }

    public String getTotalTransAmount() {
        return totalTransAmount;
    }

    public String getTotalLostStolen() {
        return totalLostStolen;
    }

    public String getTotalLastDeclines() {
        return totalLastDeclines;
    }

    public String getTotalFeeReversals() {
        return totalFreeReversals;
    }

    public String getTotalFreeAtmWithds() {
        return totalFreeAtmWithds;
    }

    public String getTotalCommAmount() {
        return totalCommAmount;
    }

    public String getSinceZeroBalance() {
        return sinceZeroBalance;
    }

    public String getRedeemedPoints() {
        return redeemedPoints;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public String getPrimaryCardNo() {
        return primaryCardNo;
    }

    public String getPosOnlineWithdLimit() {
        return posOnlineWithdLimit;
    }

    public String getPosOflineWithdLimit() {
        return posOflineWithdLimit;
    }

    public String getPendingPoints() {
        return pendingPoints;
    }

    public String getMinTransAmount() {
        return minTransAmount;
    }

    public String getMinLoadAmount() {
        return minLoadAmount;
    }

    public String getMaxTransAmount() {
        return maxTransAmount;
    }

    public String getMaxLoadAmount() {
        return maxLoadAmount;
    }

    public String getMaxDepositLimit() {
        return maxDepositLimit;
    }

    public String getLifeHighPosAmount() {
        return lifeHighPosAmount;
    }

    public String getLifeHighAvailableBalance() {
        return lifeHighAvailableBalance;
    }

    public String getLifeHighAtmAmount() {
        return lifeHighAtmAmount;
    }

    public String getLastRedeemedDate() {
        return lastRedeemedDate;
    }

    public String getFraudInfoFlag() {
        return fraudInfoFlag;
    }

    public String getFortifiedPoints() {
        return fortifiedPoints;
    }

    public String getEarnedPoints() {
        return earnedPoints;
    }

    public String getDailyAtmDepositMax() {
        return dailyAtmDepositMax;
    }

    public String getCreditOnlineWithdLimit() {
        return creditOnlineWithdLimit;
    }

    public String getCreditOflineWithdLimit() {
        return creditOflineWithdLimit;
    }

    public String getConfirmedPoints() {
        return confirmedPoints;
    }

    public String getAvailablePoints() {
        return availablePoints;
    }

    public String getAtmOnlineWithdLimit() {
        return atmOnlineWithdLimit;
    }

    public String getAtmOflineWithdLimit() {
        return atmOflineWithdLimit;
    }

    public String getIsMainCard() {
        return isMainCard;
    }

    public String getChId() {
        return chId;
    }

    public String getTotalFreeReversals() {
        return totalFreeReversals;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getHostId() {
        return hostId;
    }

    public String getForeignCountryCode() {
        return foreignCountryCode;
    }

    public String getCardNickName() {
        return cardNickName;
    }

    public String getCardLinkType() {
        return cardLinkType;
    }

    public boolean isDoAllCashOutAtActivation() {
        return doAllCashOutAtActivation;
    }

    public void setBillingZipCode(String billingZipCode) {
	this.billingZipCode = billingZipCode;
}

    public void setActivateToCard(boolean activateToCard) {
        this.activateToCard = activateToCard;
    }

    public void setCardStartNos(String cardStartNos) {
        this.cardStartNos = cardStartNos;
    }

    public void setTotalTransAmount(String totalTransAmount) {
        this.totalTransAmount = totalTransAmount;
    }

    public void setTotalLostStolen(String totalLostStolen) {
        this.totalLostStolen = totalLostStolen;
    }

    public void setTotalLastDeclines(String totalLastDeclines) {
        this.totalLastDeclines = totalLastDeclines;
    }

    public void setTotalFeeReversals(String totalFreeReversals) {
        this.totalFreeReversals = totalFreeReversals;
    }

    public void setTotalFreeAtmWithds(String totalFreeAtmWithds) {
        this.totalFreeAtmWithds = totalFreeAtmWithds;
    }

    public void setTotalCommAmount(String totalCommAmount) {
        this.totalCommAmount = totalCommAmount;
    }

    public void setSinceZeroBalance(String sinceZeroBalance) {
        this.sinceZeroBalance = sinceZeroBalance;
    }

    public void setRedeemedPoints(String redeemedPoints) {
        this.redeemedPoints = redeemedPoints;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public void setPrimaryCardNo(String primaryCardNo) {
        this.primaryCardNo = primaryCardNo;
    }

    public void setPosOnlineWithdLimit(String posOnlineWithdLimit) {
        this.posOnlineWithdLimit = posOnlineWithdLimit;
    }

    public void setPosOflineWithdLimit(String posOflineWithdLimit) {
        this.posOflineWithdLimit = posOflineWithdLimit;
    }

    public void setPendingPoints(String pendingPoints) {
        this.pendingPoints = pendingPoints;
    }

    public void setMinTransAmount(String minTransAmount) {
        this.minTransAmount = minTransAmount;
    }

    public void setMinLoadAmount(String minLoadAmount) {
        this.minLoadAmount = minLoadAmount;
    }

    public void setMaxTransAmount(String maxTransAmount) {
        this.maxTransAmount = maxTransAmount;
    }

    public void setMaxLoadAmount(String maxLoadAmount) {
        this.maxLoadAmount = maxLoadAmount;
    }

    public void setMaxDepositLimit(String maxDepositLimit) {
        this.maxDepositLimit = maxDepositLimit;
    }

    public void setLifeHighPosAmount(String lifeHighPosAmount) {
        this.lifeHighPosAmount = lifeHighPosAmount;
    }

    public void setLifeHighAvailableBalance(String lifeHighAvailableBalance) {
        this.lifeHighAvailableBalance = lifeHighAvailableBalance;
    }

    public void setLifeHighAtmAmount(String lifeHighAtmAmount) {
        this.lifeHighAtmAmount = lifeHighAtmAmount;
    }

    public void setLastRedeemedDate(String lastRedeemedDate) {
        this.lastRedeemedDate = lastRedeemedDate;
    }

    public void setFraudInfoFlag(String fraudInfoFlag) {
        this.fraudInfoFlag = fraudInfoFlag;
    }

    public void setFortifiedPoints(String fortifiedPoints) {
        this.fortifiedPoints = fortifiedPoints;
    }

    public void setEarnedPoints(String earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public void setDailyAtmDepositMax(String dailyAtmDepositMax) {
        this.dailyAtmDepositMax = dailyAtmDepositMax;
    }

    public void setCreditOnlineWithdLimit(String creditOnlineWithdLimit) {
        this.creditOnlineWithdLimit = creditOnlineWithdLimit;
    }

    public void setCreditOflineWithdLimit(String creditOflineWithdLimit) {
        this.creditOflineWithdLimit = creditOflineWithdLimit;
    }

    public void setConfirmedPoints(String confirmedPoints) {
        this.confirmedPoints = confirmedPoints;
    }

    public void setAvailablePoints(String availablePoints) {
        this.availablePoints = availablePoints;
    }

    public void setAtmOnlineWithdLimit(String atmOnlineWithdLimit) {
        this.atmOnlineWithdLimit = atmOnlineWithdLimit;
    }

    public void setAtmOflineWithdLimit(String atmOflineWithdLimit) {
        this.atmOflineWithdLimit = atmOflineWithdLimit;
    }

    public void setIsMainCard(String isMainCard) {
        this.isMainCard = isMainCard;
    }

    public void setChId(String chId) {
        this.chId = chId;
    }

    public void setTotalFreeReversals(String totalFreeReversals) {
        this.totalFreeReversals = totalFreeReversals;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setForeignCountryCode(String foreignCountryCode) {
        this.foreignCountryCode = foreignCountryCode;
    }

    public void setCardNickName(String cardNickName) {
        this.cardNickName = cardNickName;
    }

    public void setCardLinkType(String cardLinkType) {
        this.cardLinkType = cardLinkType;
    }

    public void setDoAllCashOutAtActivation(boolean doAllCashOutAtActivation) {
        this.doAllCashOutAtActivation = doAllCashOutAtActivation;
    }

}
