package util;

import java.util.HashMap;
import java.util.Map;

public class TypeUtil {
	/**
	 * oracleType 转 jdbcType,javaType
	 * @param type
	 * @return
	 */
	private static Map<String,String> jdbcTypeMap = new HashMap<String,String>();
	private static Map<String,String> javaTypeMap = new HashMap<String,String>();
	static{
		jdbcTypeMap.put("RAW","BINARY");
		jdbcTypeMap.put("BIT","BIT");
		jdbcTypeMap.put("BLOB","BLOB");
		jdbcTypeMap.put("CHAR","CHAR");
		jdbcTypeMap.put("CLOB","CLOB");
		jdbcTypeMap.put("DATE","DATE");
		jdbcTypeMap.put("DOUBLE ","DOUBLE");
		jdbcTypeMap.put("FLOAT","FLOAT");
		jdbcTypeMap.put("INTEGER","INTEGER");
		jdbcTypeMap.put("JAVA_OBJECT","JAVA_OBJECT");
		jdbcTypeMap.put("LONG RAW","LONGVARBINARY");
		jdbcTypeMap.put("LONG","LONGVARCHAR");
		jdbcTypeMap.put("NUMBER","INTEGER");
		jdbcTypeMap.put("REAL","REAL");
		jdbcTypeMap.put("RAW","VARBINARY");
		jdbcTypeMap.put("VARCHAR","VARCHAR");
		javaTypeMap.put("RAW","byte[]");
		javaTypeMap.put("BIT","Boolean");
		javaTypeMap.put("BLOB","byte[]");
		javaTypeMap.put("CHAR","String");
		javaTypeMap.put("CLOB","String");
		javaTypeMap.put("DATE","Date");
		javaTypeMap.put("DOUBLE ","Double");
		javaTypeMap.put("FLOAT","Double");
		javaTypeMap.put("INTEGER","Integer");
		javaTypeMap.put("JAVA_OBJECT","Object");
		javaTypeMap.put("LONG RAW","byte[]");
		javaTypeMap.put("LONG","String");
		javaTypeMap.put("NUMBER","Integer");
		javaTypeMap.put("REAL","Float");
		javaTypeMap.put("RAW","byte[]");
		javaTypeMap.put("VARCHAR","String");
	}
	public static String getJdbcType(String oracleType){
		return jdbcTypeMap.get(oracleType.toUpperCase());
	}
	public static String getJavaType(String oracleType){
		return javaTypeMap.get(oracleType.toUpperCase());
	}
}
