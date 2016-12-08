package codegen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.CastColNameToPropNameUtil;

public class GetConfigInfoUtil {
	
	public static String getCurrentDate(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		return sdf.format(new Date());
	}
	public static Map<String,String> getConfigMap(){
		Map<String,String> rtVal = new HashMap<String,String>();
		try{
			String packagePrefix = ConfigHelper.getValue("packagePrefix");
			String packagePrefix4core = ConfigHelper.getValue("packagePrefix4core");
			String controllerPackageName = packagePrefix + ConfigHelper.getValue("controllerPackageName");
			String servicePackageName = packagePrefix4core + ConfigHelper.getValue("servicePackageName");
			String entityPackageName = packagePrefix + ConfigHelper.getValue("entityPackageName");
			String serviceImplPackageName =  packagePrefix4core + ConfigHelper.getValue("serviceImplPackageName");
			String entityName = ConfigHelper.getValue("entityName");
			String entityFullName = entityPackageName +  "."+entityName;
			String entityVar = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
			String outputPath = ConfigHelper.getValue("outputPath");
			String author = ConfigHelper.getValue("author");
			String mapperPackageName = ConfigHelper.getValue("mapperPackageName");
			
			String dbTablePKColumnName = ConfigHelper.getValue("dbTablePKColumnName");
			String pkPropertyName = CastColNameToPropNameUtil.cast(dbTablePKColumnName);
			String createDateInComment = getCurrentDate();
			
			String objectCN = ConfigHelper.getValue("objectCN");
			
			rtVal.put("controllerPackageName", controllerPackageName);
			rtVal.put("servicePackageName", servicePackageName);
			rtVal.put("packagePrefix4core", packagePrefix4core);
			rtVal.put("entityFullName", entityFullName);
			rtVal.put("entityPackageName", entityPackageName);
			rtVal.put("entityName", entityName);
			rtVal.put("entityVar", entityVar);
			rtVal.put("outputPath", outputPath);
			rtVal.put("author", author);
			rtVal.put("mapperPackageName", mapperPackageName);
			rtVal.put("serviceImplPackageName", serviceImplPackageName);
			rtVal.put("createDateInComment", createDateInComment);
			rtVal.put("dbTablePKColumnName", dbTablePKColumnName);
			rtVal.put("pkPropertyName", pkPropertyName);
			rtVal.put("objectCN", objectCN);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return rtVal;
	}

}
