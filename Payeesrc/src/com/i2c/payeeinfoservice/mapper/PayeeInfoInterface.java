package com.i2c.payeeinfoservice.mapper;

import com.i2c.payeeinfoservice.vo.PayeeVO;
import java.util.ArrayList;

/**
 * <p>Title: Payee Information Service</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Innovative Pvt. Ltd.</p>
 *
 * @author Haroon ur Rashid Abbasi
 * @version 1.0
 */
public interface PayeeInfoInterface {
    public void updatePayeeData(PayeeVO payeeVo) throws Exception;
    public void updatePayeeData(PayeeVO[] payeeVoArray, int arraySize) throws Exception;
    public void updatePayeeData(ArrayList payeeVoList) throws Exception;
}
