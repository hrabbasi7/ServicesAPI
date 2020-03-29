package com.i2c.schedulerframework.dao;

import java.sql.Connection;
import java.util.ArrayList;
/**
 * <p>Title: Generic Scheduler Framework</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: i2cinc</p>
 *
 * @author hrabbasi
 * @version 1.0
 */

public interface BaseDAOI {
        /**
         * @return Connection
         */
        public abstract Connection getConnection();

        /**
         *
         * @param object Object
         * @return String
         * @throws Exception
         */
        public abstract String insert(Object object) throws Exception;

        /**
         *
         * @param object Object
         * @return String
         * @throws Exception
         */
        public abstract String update(Object object) throws Exception;

        /**
         *
         * @param object Object
         * @return String
         * @throws Exception
         */
        public abstract String select(Object object) throws Exception;

        /**
         * @param list
         * @throws Exception
         */
        public abstract void executeBatchUpdate(ArrayList list) throws Exception;
}
