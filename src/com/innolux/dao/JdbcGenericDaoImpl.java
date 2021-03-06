package com.innolux.dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.innolux.annotation.Column;
import com.innolux.annotation.Entity;
import com.innolux.annotation.Id;
import com.innolux.common.ToolUtility;

/**
 * 泛型DAO的JDBC實現
 * 
 */
public class JdbcGenericDaoImpl<T> implements GenericDao<T> {

	private Logger logger = Logger.getLogger(JdbcGenericDaoImpl.class);
	// 表的別名
	private static final String TABLE_ALIAS = "t";

	private JdbcDaoHelper DBConn;

	public JdbcGenericDaoImpl(JdbcDaoHelper _DBConn) {
		DBConn = _DBConn;
	}

	@Override
	public void save(T t) throws Exception {
		PreparedStatement ps = null;
		ConnectionInfo conn = null;
		try {
			long StartFuncTime = System.currentTimeMillis();
			Class<?> clazz = t.getClass();
			// 獲得表名
			String tableName = getTableName(clazz);
			// 獲得字段
			StringBuilder fieldNames = new StringBuilder(); // 字段名
			List<Object> fieldValues = new ArrayList<Object>(); // 字段值
			StringBuilder placeholders = new StringBuilder(); // 佔位符
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), t.getClass());
				if (field.isAnnotationPresent(Id.class)) {
					if (!field.getAnnotation(Id.class).value().equals("id")) {
						fieldNames.append(field.getAnnotation(Id.class).value()).append(",");
						fieldValues.add(pd.getReadMethod().invoke(t));
						placeholders.append("?").append(",");
					}
				} else if (field.isAnnotationPresent(Column.class)) {
					fieldNames.append(field.getAnnotation(Column.class).value()).append(",");
					fieldValues.add(pd.getReadMethod().invoke(t));
					placeholders.append("?").append(",");
				}

			}
			// 刪除最後一個逗號
			fieldNames.deleteCharAt(fieldNames.length() - 1);
			placeholders.deleteCharAt(placeholders.length() - 1);

			// 拼接sql
			StringBuilder sql = new StringBuilder("");
			sql.append("insert into ").append(tableName).append(" (").append(fieldNames.toString()).append(") values (")
					.append(placeholders).append(")");
			conn = DBConn.getConnection();

			ps = conn.conn.prepareStatement(sql.toString());
			// 設置SQL參數佔位符的值
			setParameter(fieldValues, ps, false);
			// 執行SQL
			long StartTime = System.currentTimeMillis();

			ps.execute();
			logger.debug(sql + " " + fieldValues + "\n" + clazz.getSimpleName() + "添加成功!sqlTime:"
					+ (System.currentTimeMillis() - StartTime));

			DBConn.release(conn, ps, null);

			// System.out.println( clazz.getSimpleName() + "添加成功!");
			logger.debug("save procss time:" + (System.currentTimeMillis() - StartFuncTime));
		} catch (Exception e) {
			if (conn != null) {

				conn.conn.close();
				conn = null;

			}
			if (ps != null) {
				ps.close();
				ps = null;
			}
			this.save(t);
		}
	}

	@Override
	public void delete(Object id, Class<T> clazz) throws Exception {
		ConnectionInfo conn = null;
		PreparedStatement ps = null;
		try {
			long StartFuncTime = System.currentTimeMillis();
			// 獲得表名
			String tableName = getTableName(clazz);
			// 獲得ID字段名和值
			String idFieldName = "";
			boolean flag = false;
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Id.class)) {
					idFieldName = field.getAnnotation(Id.class).value();
					flag = true;
					break;
				}
			}
			if (!flag) {
				throw new Exception(clazz.getName() + " object not found id property.");
			}

			// 拼裝sql
			String sql = "delete from " + tableName + " where " + idFieldName + "=?";
			conn = DBConn.getConnection();
			ps = conn.conn.prepareStatement(sql);
			ps.setObject(1, id);
			// 執行SQL
			long StartTime = System.currentTimeMillis();

			ps.execute();
			logger.debug(sql + " " + id + "\n" + clazz.getSimpleName() + "刪除成功!sqlTime:"
					+ (System.currentTimeMillis() - StartTime));

			DBConn.release(conn, ps, null);

			// System.out.println(sql + "\n" + clazz.getSimpleName() + "刪除成功!");

			logger.debug("delete procss time:" + (System.currentTimeMillis() - StartFuncTime));
		} catch (Exception e) {
			try {
				if (conn != null) {

					conn.conn.close();
					conn = null;

				}
				if (ps != null) {
					ps.close();
					ps = null;
				}
			} catch (Exception e1) {
				logger.error(ToolUtility.StackTrace2String(e1));
			}
			this.delete(id, clazz);
		}
	}

	@Override
	public void update(T t) throws Exception {
		ConnectionInfo conn = null;
		PreparedStatement ps = null;
		try {
			long StartFuncTime = System.currentTimeMillis();
			Class<?> clazz = t.getClass();
			// 獲得表名
			String tableName = getTableName(clazz);
			// 獲得字段
			List<Object> fieldNames = new ArrayList<Object>(); // 字段名
			List<Object> fieldValues = new ArrayList<Object>(); // 字段值
			List<String> placeholders = new ArrayList<String>();// 佔位符
			String idFieldName = "";
			Object idFieldValue = "";
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), t.getClass());
				if (field.isAnnotationPresent(Id.class)) {
					idFieldName = field.getAnnotation(Id.class).value();
					idFieldValue = pd.getReadMethod().invoke(t);
				} else if (field.isAnnotationPresent(Column.class)) {
					if (pd.getReadMethod().invoke(t) != null) {
						fieldNames.add(field.getAnnotation(Column.class).value());
						fieldValues.add(pd.getReadMethod().invoke(t));
						placeholders.add("?");
					}
				}
			}
			// ID作為更新條件，放在集合中的最後一個元素
			fieldNames.add(idFieldName);
			fieldValues.add(idFieldValue);
			placeholders.add("?");

			// 拼接sql
			StringBuilder sql = new StringBuilder("");
			sql.append("update ").append(tableName).append(" set ");
			int index = fieldNames.size() - 1;
			for (int i = 0; i < index; i++) {
				sql.append(fieldNames.get(i)).append("=").append(placeholders.get(i)).append(",");
			}
			sql.deleteCharAt(sql.length() - 1).append(" where ").append(fieldNames.get(index)).append("=").append("?");

			// 設置SQL參數佔位符的值
			conn = DBConn.getConnection();
			ps = conn.conn.prepareStatement(sql.toString());
			setParameter(fieldValues, ps, false);

			// 執行SQL
			long StartTime = System.currentTimeMillis();

			ps.execute();
			logger.debug(sql + " " + fieldValues + "\n" + clazz.getSimpleName() + "修改成功.sqlTime:"
					+ (System.currentTimeMillis() - StartTime));

			DBConn.release(conn, ps, null);

			// System.out.println(sql + "\n" + clazz.getSimpleName() + "修改成功.");

			logger.debug("update procss time:" + (System.currentTimeMillis() - StartFuncTime));
		} catch (Exception e) {
			try {
				if (conn != null) {

					conn.conn.close();
					conn = null;

				}
				if (ps != null) {
					ps.close();
					ps = null;
				}
			} catch (Exception e1) {
				logger.error(ToolUtility.StackTrace2String(e1));
			}
			this.update(t);
		}
	}

	@Override
	public T get(Object id, Class<T> clazz) throws Exception {
		String idFieldName = "";
		Field[] fields = clazz.getDeclaredFields();
		boolean flag = false;
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
				idFieldName = field.getAnnotation(Id.class).value();
				flag = true;
				break;
			}
		}

		if (!flag) {
			throw new Exception(clazz.getName() + " object not found id property.");
		}

		// 拼裝SQL
		Map<String, Object> sqlWhereMap = new HashMap<String, Object>();
		sqlWhereMap.put(TABLE_ALIAS + "." + idFieldName, id);

		List<T> list = findAllByConditions(sqlWhereMap, clazz);
		return list.size() > 0 ? list.get(0) : null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAllByConditions(Map<String, Object> sqlWhereMap, Class<T> clazz) throws Exception {
		ResultSet rs = null;
		PreparedStatement ps = null;
		ConnectionInfo conn = null;
		try {
			long StartFuncTime = System.currentTimeMillis();
			List<T> list = new ArrayList<T>();
			String tableName = getTableName(clazz);
			String idFieldName = "";
			// 存儲所有字段的信息
			// 通過反射獲得要查詢的字段
			StringBuffer fieldNames = new StringBuffer();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				String propertyName = field.getName();
				if (field.isAnnotationPresent(Id.class)) {
					idFieldName = field.getAnnotation(Id.class).value();
					fieldNames.append(TABLE_ALIAS + "." + idFieldName).append(" as ").append(propertyName).append(",");
				} else if (field.isAnnotationPresent(Column.class)) {
					fieldNames.append(TABLE_ALIAS + "." + field.getAnnotation(Column.class).value()).append(" as ")
							.append(propertyName).append(",");
				}
			}
			fieldNames.deleteCharAt(fieldNames.length() - 1);

			// 拼裝SQL
			String sql = "select " + fieldNames + " from " + tableName + " " + TABLE_ALIAS;

			List<Object> values = null;
			if (sqlWhereMap != null) {
				List<Object> sqlWhereWithValues = getSqlWhereWithValues(sqlWhereMap);
				if (sqlWhereWithValues != null) {
					// 拼接SQL條件
					String sqlWhere = (String) sqlWhereWithValues.get(0);
					sql += sqlWhere;
					// 得到SQL條件中佔位符的值
					values = (List<Object>) sqlWhereWithValues.get(1);
				}
			}

			// 設置參數佔位符的值
			conn = DBConn.getConnection();
			if (values != null) {
				ps = conn.conn.prepareStatement(sql);
				setParameter(values, ps, true);
			} else {
				ps = conn.conn.prepareStatement(sql);
			}

			// 執行SQL

			long StartTime = System.currentTimeMillis();

			rs = ps.executeQuery();
			logger.debug(sql + " " + sqlWhereMap + " sqlTime:" + (System.currentTimeMillis() - StartTime));

			while (rs.next()) {
				T t = clazz.newInstance();
				initObject(t, fields, rs);
				list.add(t);

			}
			// 釋放資源
			DBConn.release(conn, ps, rs);

			// System.out.println(sql);

			logger.debug(list.toString());
			logger.debug("findAllByConditions procss time:" + (System.currentTimeMillis() - StartFuncTime));
			return list;
		} catch (Exception e) {
			try {
				if (conn != null) {

					conn.conn.close();
					conn = null;

				}
				if (ps != null) {
					ps.close();
					ps = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (Exception e1) {
				logger.error(ToolUtility.StackTrace2String(e1));
			}
			return this.findAllByConditions(sqlWhereMap, clazz);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> deleteAllByConditions(Map<String, Object> sqlWhereMap, Class<T> clazz) throws Exception {
		ConnectionInfo conn = null;
		PreparedStatement ps = null;
		try {
			List<T> list = new ArrayList<T>();
			String tableName = getTableName(clazz);
			long StartFuncTime = System.currentTimeMillis();
			// 拼裝SQL
			String sql = "delete " + tableName + " " + TABLE_ALIAS;

			List<Object> values = null;
			if (sqlWhereMap != null) {
				List<Object> sqlWhereWithValues = getSqlWhereWithValues(sqlWhereMap);
				if (sqlWhereWithValues != null) {
					// 拼接SQL條件
					String sqlWhere = (String) sqlWhereWithValues.get(0);
					sql += sqlWhere;
					// 得到SQL條件中佔位符的值
					values = (List<Object>) sqlWhereWithValues.get(1);
				}
			}

			// 設置參數佔位符的值
			conn = DBConn.getConnection();
			if (values != null) {
				ps = conn.conn.prepareStatement(sql);
				setParameter(values, ps, true);
			} else {
				ps = conn.conn.prepareStatement(sql);
			}

			// 執行SQL
			long StartTime = System.currentTimeMillis();

			ps.executeUpdate();
			logger.debug(sql + " " + sqlWhereMap + " sqlTime:" + (System.currentTimeMillis() - StartTime));

			// 釋放資源
			DBConn.release(conn, ps, null);

			// System.out.println(sql);

			logger.debug("deleteAllByConditions procss time:" + (System.currentTimeMillis() - StartFuncTime));
			return list;
		} catch (Exception e) {
			try {
				if (conn != null) {

					conn.conn.close();
					conn = null;

				}
				if (ps != null) {
					ps.close();
					ps = null;
				}
			} catch (Exception e1) {
				logger.error(ToolUtility.StackTrace2String(e1));
			}
			return this.deleteAllByConditions(sqlWhereMap, clazz);
		}
	}

	/**
	 * 根據結果集初始化對像
	 */
	private void initObject(T t, Field[] fields, ResultSet rs)
			throws SQLException, IntrospectionException, IllegalAccessException, InvocationTargetException {
		for (Field field : fields) {
			try {
				String propertyName = field.getName();
				Object paramVal = null;
				Class<?> clazzField = field.getType();
				if (clazzField == String.class) {
					paramVal = rs.getString(propertyName);
				} else if (clazzField == short.class || clazzField == Short.class) {
					paramVal = rs.getShort(propertyName);
				} else if (clazzField == int.class || clazzField == Integer.class) {
					paramVal = rs.getInt(propertyName);
				} else if (clazzField == long.class || clazzField == Long.class) {
					paramVal = rs.getLong(propertyName);
				} else if (clazzField == float.class || clazzField == Float.class) {
					paramVal = rs.getFloat(propertyName);
				} else if (clazzField == double.class || clazzField == Double.class) {
					paramVal = rs.getDouble(propertyName);
				} else if (clazzField == boolean.class || clazzField == Boolean.class) {
					paramVal = rs.getBoolean(propertyName);
				} else if (clazzField == byte.class || clazzField == Byte.class) {
					paramVal = rs.getByte(propertyName);
				} else if (clazzField == char.class || clazzField == Character.class) {
					paramVal = rs.getCharacterStream(propertyName);
				} else if (clazzField == Date.class) {
					paramVal = rs.getTimestamp(propertyName);
				} else if (clazzField.isArray()) {
					paramVal = rs.getString(propertyName).split(","); // 以逗號分隔的字符串
				}
				PropertyDescriptor pd = new PropertyDescriptor(propertyName, t.getClass());
				pd.getWriteMethod().invoke(t, paramVal);
			} catch (Exception e) {
				logger.debug(ToolUtility.StackTrace2String(e));
			}
		}
	}

	/**
	 * 根據條件，返回sql條件和條件中佔位符的值
	 * 
	 * @param sqlWhereMap
	 *            key：字段名 value：字段值
	 * @return 第一個元素為SQL條件，第二個元素為SQL條件中佔位符的值
	 */
	private List<Object> getSqlWhereWithValues(Map<String, Object> sqlWhereMap) {
		if (sqlWhereMap.size() < 1)
			return null;
		List<Object> list = new ArrayList<Object>();
		List<Object> fieldValues = new ArrayList<Object>();

		StringBuffer sqlWhere = new StringBuffer(" where ");
		Set<Entry<String, Object>> entrySets = sqlWhereMap.entrySet();
		for (Iterator<Entry<String, Object>> iteraotr = entrySets.iterator(); iteraotr.hasNext();) {
			Entry<String, Object> entrySet = iteraotr.next();
			fieldValues.add(entrySet.getValue());
			Object value = entrySet.getValue();
			if (value.getClass() == String.class) {
				if (entrySet.getKey().indexOf(",") != -1) {
					String[] tmpKeyAry = entrySet.getKey().split(",");
					if (tmpKeyAry.length < 2) {
						continue;
					}
					sqlWhere.append(tmpKeyAry[0]).append(tmpKeyAry[1]).append("?").append(" and ");
				} else {
					sqlWhere.append(entrySet.getKey()).append("=").append("?").append(" and ");
				}
			} else {
				if (entrySet.getKey().indexOf(",") != -1) {
					String[] tmpKeyAry = entrySet.getKey().split(",");
					if (tmpKeyAry.length < 2) {
						continue;
					}
					sqlWhere.append(tmpKeyAry[0]).append(tmpKeyAry[1]).append("?").append(" and ");
				} else {
					sqlWhere.append(entrySet.getKey()).append("=").append("?").append(" and ");
				}
			}
		}
		sqlWhere.delete(sqlWhere.lastIndexOf("and"), sqlWhere.length());
		list.add(sqlWhere.toString());
		list.add(fieldValues);
		return list;
	}

	/**
	 * 獲得表名
	 */
	private String getTableName(Class<?> clazz) throws Exception {
		if (clazz.isAnnotationPresent(Entity.class)) {
			Entity entity = clazz.getAnnotation(Entity.class);
			return entity.value();
		} else {
			throw new Exception(clazz.getName() + " is not Entity Annotation.");
		}
	}

	/**
	 * 設置SQL參數佔位符的值
	 */
	private void setParameter(List<Object> values, PreparedStatement ps, boolean isSearch) throws SQLException {

		for (int i = 1; i <= values.size(); i++) {
			Object fieldValue = values.get(i - 1);
			Class<?> clazzValue = fieldValue.getClass();
			if (clazzValue == String.class) {
				if (isSearch)
					ps.setString(i, (String) fieldValue);
				else
					ps.setString(i, (String) fieldValue);

			} else if (clazzValue == boolean.class || clazzValue == Boolean.class) {
				ps.setBoolean(i, (Boolean) fieldValue);
			} else if (clazzValue == byte.class || clazzValue == Byte.class) {
				ps.setByte(i, (Byte) fieldValue);
			} else if (clazzValue == char.class || clazzValue == Character.class) {
				ps.setObject(i, fieldValue, Types.CHAR);
			} else if (clazzValue == int.class || clazzValue == Character.class) {
				ps.setInt(i, (int) fieldValue);
			} else if (clazzValue == Date.class) {
				ps.setTimestamp(i, new Timestamp(((Date) fieldValue).getTime()));
			} else if (clazzValue.isArray()) {
				Object[] arrayValue = (Object[]) fieldValue;
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < arrayValue.length; j++) {
					sb.append(arrayValue[j]).append("、");
				}
				ps.setString(i, sb.deleteCharAt(sb.length() - 1).toString());
			} else {
				ps.setObject(i, fieldValue, Types.NUMERIC);

			}
		}
	}
}