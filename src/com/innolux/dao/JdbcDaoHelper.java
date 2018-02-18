package com.innolux.dao;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.innolux.common.ToolUtility;

/**
 * 提供獲取數據庫連接、釋放資源的接口
 */
public class JdbcDaoHelper {
	private int max; // 連接池中最大Connection數目
	private Vector<ConnectionInfo> connections;
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 數據庫用戶名
	 */
	private String USER = "";

	/**
	 * 數據庫密碼
	 */
	private String PASSWORD = "";

	/**
	 * 連接數據庫的地址
	 */
	private String URL = "";

	public JdbcDaoHelper(String connectionStr, String User, String PWD, int maxConn) {
		URL = connectionStr;
		USER = User;
		PASSWORD = PWD;
		max = maxConn;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (Exception e) {
			logger.error(URL);
			logger.error(ToolUtility.StackTrace2String(e));
		}

		connections = new Vector<ConnectionInfo>();
	}

	/**
	 * 獲得一個數據庫連接對像
	 * 
	 * @return java.sql.Connection實例
	 * @throws SQLException
	 */
	public synchronized ConnectionInfo getConnection() throws SQLException {
		ConnectionInfo con = null;

		if (connections.size() == 0) {
			con = new ConnectionInfo();
			con.conn = DriverManager.getConnection(URL, USER, PASSWORD);
			con.CreateTime = System.currentTimeMillis();
			return con;
		} else {
			int lastIndex = connections.size() - 1;
			con = connections.remove(lastIndex);

			if (System.currentTimeMillis() - con.CreateTime > 60000) {//fix unknown connection fail
				try {

					con.conn.close();
					
				} catch (Exception e) {
					logger.debug("getConnection() con.conn.close() has error, exception:"
							+ ToolUtility.StackTrace2String(e));
				}
				con.conn = DriverManager.getConnection(URL, USER, PASSWORD);
				con.CreateTime = System.currentTimeMillis();
			}
		}

		return con;
	}

	/**
	 * 釋放數據庫資源
	 */
	public synchronized void release(ConnectionInfo con, PreparedStatement ps, ResultSet rs) {
		try {
			if (con != null) {

				if (connections.size() == max) {
					con.conn.close();
					con = null;
				} else {

					connections.add(con);
				}

			}
			if (ps != null) {
				ps.close();
				ps = null;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			logger.error(ToolUtility.StackTrace2String(e));
		}
	}

}