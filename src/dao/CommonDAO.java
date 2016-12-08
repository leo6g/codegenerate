package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import pojo.ColumnInfo;
import util.CastColNameToPropNameUtil;
import codegen.ConfigHelper;

public class CommonDAO {

	private static DataSource springDataSource;

	public static final String driverName = ConfigHelper.prop
			.getProperty("jdbc.driverClassName"),
			connectionURL = ConfigHelper.prop.getProperty("jdbc.url"),
			dbUser = ConfigHelper.prop.getProperty("jdbc.username"),
			databaseType = ConfigHelper.prop.getProperty("databaseType"),
			jndiName = ConfigHelper.prop.getProperty("jndi"),
			dbPwd = ConfigHelper.prop.getProperty("jdbc.password");

	public static Connection getConnectionByJNDI() throws Exception {
		Connection rtVal = null;
		try {
			Context ic = new InitialContext();
			DataSource source = (DataSource) ic.lookup(jndiName);
			rtVal = source.getConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return rtVal;
	}

	public static Connection getConnection() {
		Connection rtVal = null;

		try {
			Class.forName(driverName);
			// rtVal = getConnectionByJNDI();
			rtVal = getConnectionByJdbc();

		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				rtVal = getConnectionByJdbc();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rtVal;
	}

	public static Connection getConnectionByJdbc() {
		Connection rtVal = null;

		try {
			Class.forName(driverName);
			rtVal = DriverManager.getConnection(connectionURL, dbUser, dbPwd);
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return rtVal;
	}

	public static Integer executeUpdate(String sql) {
		Integer effectiveLines = 0;
		Connection con = null;
		try {
			con = getConnection();
			Statement st = con.createStatement();
			effectiveLines = st.executeUpdate(sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
		return effectiveLines;
	}

	public static Integer getInsertedId(String insertSql) {
		Integer id = 0;
		Connection con = null;
		try {
			con = getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(insertSql);
			if (rs.next()) {
				id = rs.getInt("ID");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
		return id;
	}

	private static Connection getConnectionByJNDI(String jndi) {
		Connection rtVal = null;
		try {
			Context ic = new InitialContext(); // "java:comp/env/jdbc/books"
			DataSource source = (DataSource) ic.lookup(jndi);
			rtVal = source.getConnection();
		} catch (NamingException e) {
			System.out.println("数据源没找到！");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("获取数连接对象失败！");
			e.printStackTrace();
		}
		return rtVal;
	}

	private static Connection getConnectionWithSpringDataSource(
			String dataSourceId) {
		Connection rtVal = null;
		try {

			rtVal = springDataSource.getConnection();
		} catch (SQLException e) {
			System.out.println("获取数连接对象失败！");
			e.printStackTrace();
		}
		return rtVal;
	}

	/**
	 * 根据传入的SQL， 获得一个列表， 列表里面是 map 对象， map 的key 是列名称， value 是值 NOTE:
	 * 该函数不支持参数化查询。
	 * 
	 * @param sql
	 * @return
	 */
	public static List<Map> getResultSetMap(String sql) {
		List<Map> rtVal = new ArrayList<Map>();
		try {
			Connection con = getConnection();
			// Connection con = getConnectionByJdbc();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCnt = rsmd.getColumnCount();
			// rsmd.getColumnName(column)
			while (rs.next()) {
				Map map = new HashMap();
				for (int i = 1; i <= columnCnt; i++) {
					String columnName = rsmd.getColumnName(i);
					Object columnVal = rs.getString(columnName);
					if (null != columnName && !columnName.trim().equals("")) {
						map.put(columnName, columnVal);
					}
				}
				rtVal.add(map);
			}

			rs.close();
			stmt.close();
			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rtVal;
	}

	public static List<Map> getResultSetMap(String sql, List params) {
		List<Map> rtVal = new ArrayList<Map>();
		try {
			Connection con = getConnection();
			// Connection con = getConnectionByJdbc();
			PreparedStatement stmt = con.prepareStatement(sql);
			if (params != null) {
				int parameterIndex = 1;
				for (Iterator it = params.iterator(); it.hasNext();) {
					Object objVal = (Object) it.next();
					stmt.setObject(parameterIndex, objVal);
					parameterIndex++;
				}
			}
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCnt = rsmd.getColumnCount();
			// rsmd.getColumnName(column)
			while (rs.next()) {
				Map map = new HashMap();
				for (int i = 1; i <= columnCnt; i++) {
					String columnName = rsmd.getColumnName(i);
					Object columnVal = rs.getString(columnName);
					if (null != columnName && !columnName.trim().equals("")) {
						map.put(columnName, columnVal);
					}
				}
				rtVal.add(map);
			}

			rs.close();
			stmt.close();
			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rtVal;
	}

	public static DataSource getSpringDataSource() {
		return springDataSource;
	}

	public static void setSpringDataSource(DataSource springDataSource) {
		CommonDAO.springDataSource = springDataSource;
	}
	
	
	public static Map<String,ColumnInfo> getTableColumnInfo(String tableName){
		Map<String,ColumnInfo> rtVal = null;
		try {
			Connection con = getConnection();
			String sql = "SELECT column_name "+// --字段名
			      		",data_type "+     //--字段类型
			      		",CHARACTER_MAXIMUM_LENGTH data_length"+             //--字段长度
			      		",IS_NULLABLE nullable"+				//nullable
			      		",COLUMN_COMMENT comments "+       //--字段注释
						"FROM COLUMNS "+
						"WHERE table_name = '"+tableName+"'";
			PreparedStatement stmt = con.prepareStatement(sql);
			
			System.out.println(sql);
			
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCnt = rsmd.getColumnCount();

			rtVal = new HashMap<String,ColumnInfo>();
			int index = 1;
			while (rs.next()) {
				String columnName = rs.getString("column_name");
				String columnType = rs.getString("data_type");
				String comments = rs.getString("comments");
				String nullAble = rs.getString("nullable");
				String javaPropName = CastColNameToPropNameUtil.cast(columnName);
				ColumnInfo ci = new ColumnInfo();
				ci.setColumnName(columnName);
				ci.setColumnType(columnType);
				ci.setComment(comments);
				ci.setNullAble(nullAble);
				ci.setJavaPropName(javaPropName);
				Integer originalLength = rs.getInt("data_length");
				Integer dataLength = ((Double)(originalLength * 0.8)).intValue();
				ci.setDataLength(dataLength);
				
				rtVal.put(index+"", ci);
				index++;
				
			}
			

			rs.close();
			stmt.close();
			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rtVal;
	}

	public static void main(String[] ar) throws Exception {

		String sql = "select * from IncomeQuery where inc_year=2013";
		List<Map> incomes = CommonDAO.getResultSetMap(sql);

	}

}
