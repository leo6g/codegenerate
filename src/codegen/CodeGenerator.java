package codegen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import pojo.ColumnInfo;
import util.TypeUtil;
import dao.CommonDAO;
public class CodeGenerator {
	
	
	
	/**
	 * 创建sqlMapper.xml文件
	 * @param tableName
	 */
	public static void createSqlMapperFile(){
		String tableName = ConfigHelper.getValue("dbTableName").trim();
		String dbTableName = tableName.toUpperCase();
		Map<String,ColumnInfo> mapInfo =  CommonDAO.getTableColumnInfo(dbTableName);
		String resultMap="",conditionLoop="", columnList="",columnValueList="";
		String updateByPkCond="" , insertSelectiveCond="", insertSelectiveValCond="",updateByPkSelective="";
		String updateByExampleSelectiveCond = "";
		Iterator<String> it = mapInfo.keySet().iterator();
		Map<String,String> replacedMap = GetConfigInfoUtil.getConfigMap();
		String dbTablePKColumnName = replacedMap.get("dbTablePKColumnName");
		String columnType = "";
		String PKColumnJdbcType = "";
		String codeColumnName = "";
		String codeJavaPropName = "";
		String codeJdbcType = "";
		while(it.hasNext()){
			ColumnInfo ci = mapInfo.get(it.next().toString());
			System.out.println(ci);
			
			//oracleType转jdbcType
			columnType = TypeUtil.getJdbcType(ci.getColumnType());
			//识别code字段
			if(ci.getColumnName().endsWith("CODE")){
				codeColumnName = ci.getColumnName();codeJavaPropName = ci.getJavaPropName();codeJdbcType = columnType;
			}
			if("DATE".equals(ci.getColumnType())){
				conditionLoop+= "<if test=\""+ci.getJavaPropName()+" != null and "+ci.getJavaPropName()+" !=\'\'\" >\r\n\t\t"+
						" AND "+ci.getColumnName()+" = to_date(\'\\${"+ci.getJavaPropName()+"}\',\'yyyy-mm-dd hh24:mi:ss\')"+"\r\n\t"+
						" </if>\r\n\t";
			}else{
				conditionLoop+= "<if test=\""+ci.getJavaPropName()+" != null\" >\r\n\t\t"+
						" AND "+ci.getColumnName()+" = #{"+ci.getJavaPropName()+"}"+"\r\n\t"+
						" </if>\r\n\t";
			}
			columnList+= ci.getColumnName()+",";
			
			columnValueList+= "#{"+ci.getJavaPropName()+"},";
			
			//Selective移除主键
			if(!dbTablePKColumnName.equals(ci.getColumnName())){
				resultMap+= "<result column=\""+ci.getColumnName()+"\" property=\""+ci.getJavaPropName()+"\" jdbcType=\""+columnType+"\"/>\r\n\t";
				updateByPkCond+= ci.getColumnName()+" = #{"+ci.getJavaPropName()+"},\r\n\t";
				if("DATE".equals(ci.getColumnType())){
					if("CREATE_TIME".equals(ci.getColumnName())||"UPDATE_TIME".equals(ci.getColumnName())){
						insertSelectiveCond+= "<if test=\""+ci.getJavaPropName()+" != null and "+ci.getJavaPropName()+" !=\'\'\"> "+ ci.getColumnName()+", </if>\r\n\t";
						insertSelectiveValCond+="<if test=\""+ci.getJavaPropName()+" != null and "+ci.getJavaPropName()+" !=\'\'\"> #{"+ ci.getJavaPropName()+"}, </if>\r\n\t";
						updateByPkSelective+="<if test=\""+ci.getJavaPropName()+" != null and "+ci.getJavaPropName()+" !=\'\'\">"+ci.getColumnName()+" = #{"+ci.getJavaPropName()+"}, "+"</if>\r\n\t";
						updateByExampleSelectiveCond+="<if test=\""+ci.getJavaPropName()+" != null and "+ci.getJavaPropName()+" !=\'\'\" > "+
								ci.getColumnName()+" = #{"+ci.getJavaPropName()+"}, </if>\r\n\t";
					}else{
						insertSelectiveCond+= "<if test=\""+ci.getJavaPropName()+" != null and "+ci.getJavaPropName()+" !=\'\'\"> "+ ci.getColumnName()+", </if>\r\n\t";
						insertSelectiveValCond+="<if test=\""+ci.getJavaPropName()+" != null and "+ci.getJavaPropName()+" !=\'\'\"> to_date(\'\\${"+ci.getJavaPropName()+"}\',\'yyyy-mm-dd hh24:mi:ss\'), </if>\r\n\t";
						updateByPkSelective+="<if test=\""+ci.getJavaPropName()+" != null and "+ci.getJavaPropName()+" !=\'\'\">"+ci.getColumnName()+" = #{"+ci.getJavaPropName()+"}, "+"</if>\r\n\t";
						updateByExampleSelectiveCond+="<if test=\""+ci.getJavaPropName()+" != null and "+ci.getJavaPropName()+" !=\'\'\" > "+
								ci.getColumnName()+" = to_date(\'\\${"+ci.getJavaPropName()+"}\',\'yyyy-mm-dd hh24:mi:ss\'), </if>\r\n\t";
					}
				}else{
					insertSelectiveCond+= "<if test=\""+ci.getJavaPropName()+" != null\"> "+ ci.getColumnName()+", </if>\r\n\t";
					insertSelectiveValCond+="<if test=\""+ci.getJavaPropName()+" != null\"> #{"+ ci.getJavaPropName()+"}, </if>\r\n\t";
					updateByPkSelective+="<if test=\""+ci.getJavaPropName()+" != null\">"+ci.getColumnName()+" = #{"+ci.getJavaPropName()+"}, "+"</if>\r\n\t";
					updateByExampleSelectiveCond+="<if test=\""+ci.getJavaPropName()+" != null\" > "+
							ci.getColumnName()+" = #{"+ci.getJavaPropName()+"}, </if>\r\n\t";
				}
			}else{
				PKColumnJdbcType = TypeUtil.getJdbcType(ci.getColumnType());
			}

		}
		
		columnList = columnList.substring(0,columnList.length()-1);
		columnValueList = columnValueList.substring(0,columnValueList.length()-1);
		
		updateByPkCond = updateByPkCond.substring(0,updateByPkCond.length()-3);
		insertSelectiveCond = insertSelectiveCond.substring(0,insertSelectiveCond.length()-3);
		insertSelectiveValCond = insertSelectiveValCond.substring(0,insertSelectiveValCond.length()-3);
		updateByPkSelective = updateByPkSelective.substring(0,updateByPkSelective.length()-3);
		updateByExampleSelectiveCond = updateByExampleSelectiveCond.substring(0,updateByExampleSelectiveCond.length()-3);
		String fileContent = ReadTempUtil.readFileContent("SqlMapperTemp.txt");
		
		fileContent = ReadTempUtil.replaceVar(fileContent,replacedMap);
		fileContent = fileContent.replaceAll("#PKColumnJdbcType#",PKColumnJdbcType);
		fileContent = fileContent.replaceAll("#resultMap#",resultMap);
		fileContent = fileContent.replaceAll("#codeColumnName#",codeColumnName);
		fileContent = fileContent.replaceAll("#codeJavaPropName#",codeJavaPropName);
		fileContent = fileContent.replaceAll("#codeJdbcType#",codeJdbcType);
		fileContent = fileContent.replaceAll("#conditionLoop#",conditionLoop);
		fileContent = fileContent.replaceAll("#columnList#",columnList);
		fileContent = fileContent.replaceAll("#columnValueList#",columnValueList);
		fileContent = fileContent.replaceAll("#updateByPkCond#",updateByPkCond);
		fileContent = fileContent.replaceAll("#insertSelectiveCond#",insertSelectiveCond);
		fileContent = fileContent.replaceAll("#insertSelectiveValCond#",insertSelectiveValCond);
		fileContent = fileContent.replaceAll("#updateByPkSelective#",updateByPkSelective);
		fileContent = fileContent.replaceAll("#updateByExampleSelectiveCond#",updateByExampleSelectiveCond);
		fileContent = fileContent.replaceAll("#dbTableName#",dbTableName);
		
		
		
		String filePath = replacedMap.get("mapperPackageName");
		String entityName = replacedMap.get("entityName");
		filePath = "wxcore."+ filePath;
		filePath = filePath.replaceAll("\\.", "\\\\");
		
		CreateFileUtil.createFile(fileContent, filePath,entityName+"Mapper.xml");
		System.out.println("map.xml-->ok");
	}
	
	/**
	 * 创建实体对象
	 * @param tableName
	 */
	public static void createEntity(){
		String tableName = ConfigHelper.getValue("dbTableName").trim();
		Map<String,String> replacedMap = GetConfigInfoUtil.getConfigMap();
		Map<String,ColumnInfo> mapInfo =  CommonDAO.getTableColumnInfo(tableName.toUpperCase());
		String dbTablePKColumnName = replacedMap.get("dbTablePKColumnName");
		String propNameDefined="",getterAndSetter="";
		Iterator<String> it = mapInfo.keySet().iterator();
		while(it.hasNext()){
			ColumnInfo ci = mapInfo.get(it.next().toString());
			System.out.println(ci);
			String capitalPropName = ci.getJavaPropName();
			capitalPropName = capitalPropName.substring(0, 1).toUpperCase()+capitalPropName.substring(1);
			if("N".equals(ci.getNullAble())&&!dbTablePKColumnName.equals(ci.getColumnName())){
				propNameDefined+="/*\n\t字段注释："+ci.getComment()+"\n\t列名称:"
					+ci.getColumnName()+"\n\t字段类型:"+ci.getColumnType()+"*/\n\t"+
					"@NotEmpty(message = \""+ci.getComment()+"不能为空\")\n\t" +
					"private "+ TypeUtil.getJavaType(ci.getColumnType())+" "+ci.getJavaPropName()+";\n\t";
			
			}else{
				propNameDefined+="/*\n\t字段注释："+ci.getComment()+"\n\t列名称:"
					+ci.getColumnName()+"\n\t字段类型:"+ci.getColumnType()+"*/\n\t"+
					"private "+ TypeUtil.getJavaType(ci.getColumnType())+" "+ci.getJavaPropName()+";\n\t";
			}
			getterAndSetter +="public void set"+capitalPropName+"("+
				TypeUtil.getJavaType(ci.getColumnType())+" "+ ci.getJavaPropName()+"){\n\t\t"+
				"this."+ci.getJavaPropName()+" = "+ci.getJavaPropName()+";\n\t"+
				"}\n\n\t"+
				"public "+TypeUtil.getJavaType(ci.getColumnType())+" get"+capitalPropName+"(){\n\t\t"+
				"return "+ci.getJavaPropName()+";\n\t"+
				"}\n\n\t";
				
			
		}
		
		String fileContent = ReadTempUtil.readFileContent("EntityTemp.txt");
		
		
		fileContent = ReadTempUtil.replaceVar(fileContent,replacedMap);
		
		fileContent = fileContent.replaceAll("#propNameDefined#",propNameDefined);
		fileContent = fileContent.replaceAll("#getterAndSetter#",getterAndSetter);
		
		
		String filePath = replacedMap.get("entityPackageName");
		String entityName = replacedMap.get("entityName");
		filePath = "wxweb."+ filePath;
		filePath = filePath.replaceAll("\\.", "\\\\");
		
		CreateFileUtil.createFile(fileContent, filePath,entityName+"Form.java");
		System.out.println("entity-->ok");
	}
	/**
	 * 创建controller
	 */
	public static void createController(){
		String tableName = ConfigHelper.getValue("dbTableName").toUpperCase().trim();
		String fileContent = ReadTempUtil.readFileContent("ControllerTemp.txt");
		Map<String,String> replacedMap = GetConfigInfoUtil.getConfigMap();
		fileContent = ReadTempUtil.replaceVar(fileContent,replacedMap);
		String filePath = replacedMap.get("controllerPackageName");
		String entityName = replacedMap.get("entityName");
		String ctrCriteriaLoop = "", entityVar = GetConfigInfoUtil.getConfigMap().get("entityVar");
		Map<String,ColumnInfo> mapInfo =  CommonDAO.getTableColumnInfo(tableName);
		Iterator<String> it = mapInfo.keySet().iterator();
		StringBuffer sbInitBinder = new StringBuffer();
		StringBuffer sbImport = new StringBuffer();
		String subEntityName = "";
		String subModifier = "";
		boolean flag = false;
		boolean flag1 = false;
		while(it.hasNext()){
			ColumnInfo ci = mapInfo.get(it.next().toString());
			String capProp = ci.getJavaPropName().substring(0, 1).toUpperCase()+ci.getJavaPropName().substring(1);
			if("CREATE_USER".equals(ci.getColumnName())) flag1 = true;
			if("DATE".equals(ci.getColumnType())&&!"CREATE_TIME".equals(ci.getColumnName())&&!"UPDATE_TIME".equals(ci.getColumnName())) flag = true;
			if(ci.getColumnType().contains("VARCHAR")){
				ctrCriteriaLoop+="if(StringUtils.isNotBlank(#entityVar#.get"+capProp+"())){\r\n\t\t\t\t"+
							"criteria.put(\""+ci.getJavaPropName()+"\",#entityVar#.get"+capProp+"());\r\n\t\t\t"+
							"}\r\n\t\t\t";
			}else{
				ctrCriteriaLoop+="if(#entityVar#.get"+capProp+"() != null){\r\n\t\t\t\t"+
				"criteria.put(\""+ci.getJavaPropName()+"\",#entityVar#.get"+capProp+"());\r\n\t\t\t"+
				"}\r\n\t\t\t";
			}
		}
		if(flag){
			sbInitBinder.append("/**\r\n")
					.append("\t* 校验前台传入的date格式，格式必须一样\r\n")
					.append("\t*/\r\n")
					.append("\t@InitBinder\r\n")
					.append("\tprotected void initBinder(WebDataBinder binder) {\r\n")
					.append("\t\tSimpleDateFormat dateFormat = new SimpleDateFormat(\"yyyy-MM-dd\");\r\n")
					.append("\t\tbinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));\r\n")
					.append("\t}\r\n");
			sbImport.append("import org.springframework.web.bind.annotation.InitBinder;\r\n")
				.append("import org.springframework.web.bind.WebDataBinder;\r\n")
				.append("import java.text.SimpleDateFormat;\r\n")
				.append("import java.util.Date;\r\n")
				.append("import org.springframework.beans.propertyeditors.CustomDateEditor;\r\n");
		}
		ctrCriteriaLoop = ctrCriteriaLoop.substring(0, ctrCriteriaLoop.length()-3);
		
		ctrCriteriaLoop = ctrCriteriaLoop.replaceAll("#entityVar#", entityVar);
		if(entityName.startsWith("WX")){
			subEntityName = "/"+entityName.substring(2).toLowerCase();
			subModifier = "/wx";
		}else if(entityName.startsWith("QY")){
			subEntityName = "/"+entityName.substring(2).toLowerCase();
			subModifier = "/qywx";
		}
		String addCreateUser = "map.put(\"createUser\", ((HashMap<String,Object>)getSession().getAttribute(Constants.USER_SESSION.USER)).get(\"userName\"));";
		if(flag1) fileContent = fileContent.replaceAll("#addCreateUser#",addCreateUser);
		fileContent = fileContent.replaceAll("#subModifier#",subModifier);
		fileContent = fileContent.replaceAll("#subEntityName#",subEntityName);
		fileContent = fileContent.replaceAll("#subEntityNameToLow#",entityName.substring(2).toLowerCase());
		fileContent = fileContent.replaceAll("#ctrCriteriaLoop#",ctrCriteriaLoop);
		fileContent = fileContent.replaceAll("#sbInitBinder#",sbInitBinder.toString());
		fileContent = fileContent.replaceAll("#entityVarToLow#",entityVar.toLowerCase());
		fileContent = fileContent.replaceAll("#sbImport#",sbImport.toString());
		filePath = "wxweb."+ filePath;
		
		filePath = filePath.replaceAll("\\.", "\\\\");
		CreateFileUtil.createFile(fileContent, filePath,entityName+"Controller.java");
		System.out.println("controller-->ok");
	}
	

	/**
	 * 创建servie接口类
	 */
	public static void createService(){
		String fileContent = ReadTempUtil.readFileContent("ServiceTemp.txt");
		
		Map<String,String> replacedMap = GetConfigInfoUtil.getConfigMap();
		fileContent = ReadTempUtil.replaceVar(fileContent,replacedMap);
		
		String filePath = replacedMap.get("servicePackageName");
		String entityName = replacedMap.get("entityName");
		
		filePath = "wxcore."+ filePath;
		filePath = filePath.replaceAll("\\.", "\\\\");
		
		CreateFileUtil.createFile(fileContent, filePath,"I"+entityName+"Service.java");
		System.out.println("Iservice-->ok");
	}
	
	/**
	 * 创建servie接口实现类
	 */
	public static void createServiceImpl(){
		String fileContent = ReadTempUtil.readFileContent("ServiceImplTemp.txt");
		
		Map<String,String> replacedMap = GetConfigInfoUtil.getConfigMap();
		fileContent = ReadTempUtil.replaceVar(fileContent,replacedMap);
		
		String filePath = replacedMap.get("serviceImplPackageName");
		String entityName = replacedMap.get("entityName");
		
		filePath = "wxcore."+ filePath;
		filePath = filePath.replaceAll("\\.", "\\\\");
		
		CreateFileUtil.createFile(fileContent, filePath,entityName+"ServiceImpl.java");
		System.out.println("serviceImpl-->ok");
	}
	
	/**
	 * 创建html 文件
	 */
	public static void createHtml(){
		String tableName = ConfigHelper.getValue("dbTableName").toUpperCase().trim();
		String fileContent = ReadTempUtil.readFileContent("HtmlTemp.txt");
		Map<String,String> replacedMap = GetConfigInfoUtil.getConfigMap();
		String editInfoContent = "",addInfoContent="", tableHeader="",tableBodyJS="";
		String contentEnter="\r\n\t\t\t\t\t\t";
		String dbTablePKColumnName = replacedMap.get("dbTablePKColumnName");
		Map<String,ColumnInfo> mapInfo =  CommonDAO.getTableColumnInfo(tableName);
		Iterator<String> it = mapInfo.keySet().iterator();
		String nullAbleMark = "";
		String requiredMark = "";
		while(it.hasNext()){
			ColumnInfo ci = mapInfo.get(it.next().toString());
			if("N".equals(ci.getNullAble())){
				nullAbleMark = "<span class=\"red\">* </span>";
				requiredMark = "required=\"true\"";
			}else{
				nullAbleMark = "";
				requiredMark = "";
			}
			
			if(!(dbTablePKColumnName.equals(ci.getColumnName())||"DELETE_FLAG".equals(ci.getColumnName())||"CREATE_TIME".equals(ci.getColumnName())||"UPDATE_TIME".equals(ci.getColumnName()))){
				
				editInfoContent+="<tr>"+contentEnter;
		        editInfoContent+="\t<td style=\"border: none;\">"+contentEnter;
		        editInfoContent+="\t\t"+nullAbleMark+ci.getComment()+"："+contentEnter;
		        editInfoContent+="\t\t<div class=\"input-wrap\">"+contentEnter;
		        editInfoContent+="\t\t\t<input type=\"text\" id=\"edit-"+ci.getJavaPropName()+"\" name=\""+ci.getJavaPropName()+"\" value=\"\" class=\"fluid-input\""+requiredMark+" />"+contentEnter;
		        editInfoContent+="\t\t</div>"+contentEnter;
		        editInfoContent+="\t</td>"+contentEnter;
		        editInfoContent+="</tr>"+contentEnter;
		        
				addInfoContent+="<tr>"+contentEnter;
				addInfoContent+="\t<td style=\"border: none;\">"+contentEnter;
				addInfoContent+="\t\t"+nullAbleMark+ci.getComment()+"："+contentEnter;
				addInfoContent+="\t\t<div class=\"input-wrap\">"+contentEnter;
				addInfoContent+="\t\t\t<input type=\"text\" id=\""+ci.getJavaPropName()+"\" name=\""+ci.getJavaPropName()+"\" value=\"\" class=\"fluid-input\""+requiredMark+" />"+contentEnter;
				addInfoContent+="\t\t</div>"+contentEnter;
				addInfoContent+="\t</td>"+contentEnter;
				addInfoContent+="</tr>"+contentEnter;
				
				tableHeader+="<th>"+ci.getComment()+"</th>\r\n\t\t\t";
				
				tableBodyJS+="<td>{{"+ci.getJavaPropName()+"}}</td>\r\n\t\t\t";
			}else if(dbTablePKColumnName.equals(ci.getColumnName())){
				editInfoContent+="<!-- 隐藏主键 -->"+contentEnter;
				editInfoContent+="<input type=\"hidden\" id=\"edit-"+ci.getJavaPropName()+"\" name=\""+ci.getJavaPropName()+"\" value=\"\" />"+contentEnter;
				tableHeader+="<th style=\"display:none\">"+ci.getComment()+"</th>\r\n\t\t\t";
				tableBodyJS+="<td style=\"display:none\">{{"+ci.getJavaPropName()+"}}</td>\r\n\t\t\t";
			}
			//追加创建时间和更新时间
			if("CREATE_TIME".equals(ci.getColumnName())||"UPDATE_TIME".equals(ci.getColumnName())){
				tableHeader+="<th>"+ci.getComment()+"</th>\r\n\t\t\t";
				tableBodyJS+="<td>{{"+ci.getJavaPropName()+"}}</td>\r\n\t\t\t";
			}
		}
		fileContent = ReadTempUtil.replaceVar(fileContent,replacedMap);
		fileContent = fileContent.replaceAll("#addInfoContent#",addInfoContent);
		fileContent = fileContent.replaceAll("#editInfoContent#",editInfoContent);
		fileContent = fileContent.replaceAll("#tableHeader#",tableHeader);
		fileContent = fileContent.replaceAll("#tableBodyJS#",tableBodyJS);
		String entityVar = replacedMap.get("entityVar");
		fileContent = fileContent.replaceAll("#subEntityNameToLow#",entityVar.substring(2).toLowerCase());
		fileContent = fileContent.replaceAll("#entityVarToLow#",entityVar.toLowerCase());
		
		
		CreateFileUtil.createFile(fileContent, "\\html\\WEB-INF\\velocity\\weixin",entityVar.substring(2).toLowerCase()+"-list.html");
		System.out.println("html-->ok");
	}
	/**
	 * 创建htmlJS 文件
	 */
	public static void createHtmlJS(){
		String tableName = ConfigHelper.getValue("dbTableName").trim();
		String contentEnter="\r\n\t";
		String fileContent = ReadTempUtil.readFileContent("HtmlJSTemp.txt");
		Map<String,String> replacedMap = GetConfigInfoUtil.getConfigMap();
		String selectJsP1 = "",selectJsP2 = "" ,editValidate = "",editValidateIf = "";
		Map<String,ColumnInfo> mapInfo =  CommonDAO.getTableColumnInfo(tableName);
		String dbTablePKColumnName = replacedMap.get("dbTablePKColumnName");
		Iterator<String> it = mapInfo.keySet().iterator();
		String entityName = replacedMap.get("entityName");
		String subEntityName = "";
		String subModifier = "";
		int index = 1;
		while(it.hasNext()){
			ColumnInfo ci = mapInfo.get(it.next().toString());
			//解决索引错误
			if("CREATE_TIME".equals(ci.getColumnName())||"UPDATE_TIME".equals(ci.getColumnName())){
				index++;
				continue;
			}
			if(!"DELETE_FLAG".equals(ci.getColumnName())){
				selectJsP1+="var "+ci.getJavaPropName()+" = \\$(this).find(\"td:eq("+index+")\").html();"+"\r\n\t\t";
				selectJsP2+="\\$(\"#edit-"+ci.getJavaPropName()+"\").val("+ci.getJavaPropName()+");"+"\r\n\t\t";
				index++;
			}
			if("N".equals(ci.getNullAble())&&!"DELETE_FLAG".equals(ci.getColumnName())){
				editValidate += "var "+ci.getJavaPropName()+" = \\$(\"#edit-"+ci.getJavaPropName()+"\").val();"+contentEnter;
				if(dbTablePKColumnName.equals(ci.getColumnName())){
					editValidateIf +="if("+ci.getJavaPropName()+"==\"\"){"+contentEnter;
					editValidateIf +="\talert(\"请选择一条信息!\");"+contentEnter;
					editValidateIf +="\treturn false;"+contentEnter;
					editValidateIf +="}"+contentEnter;
				}else{
					editValidateIf +="if("+ci.getJavaPropName()+"==\"\"){"+contentEnter;
					editValidateIf +="\talert(\""+ci.getComment()+"不能为空!\");"+contentEnter;
					editValidateIf +="\treturn false;"+contentEnter;
					editValidateIf +="}"+contentEnter;
				}
			}
		}
		if(entityName.startsWith("WX")){
			subEntityName = "/"+entityName.substring(2).toLowerCase();
			subModifier = "/wx";
			
		}else if(entityName.startsWith("QY")){
			subEntityName = "/"+entityName.substring(2).toLowerCase();
			subModifier = "/qywx";
		}
		fileContent = fileContent.replaceAll("#subModifier#",subModifier);
		fileContent = fileContent.replaceAll("#subEntityName#",subEntityName);
		fileContent = ReadTempUtil.replaceVar(fileContent,replacedMap);
		fileContent = fileContent.replaceAll("#selectJsP1#",selectJsP1);
		fileContent = fileContent.replaceAll("#selectJsP2#",selectJsP2);
		fileContent = fileContent.replaceAll("#editValidate#",editValidate);
		fileContent = fileContent.replaceAll("#editValidateIf#",editValidateIf);
		String entityVar = replacedMap.get("entityVar");
		
		CreateFileUtil.createFile(fileContent, "\\js\\assets\\js\\weixin",entityVar.substring(2).toLowerCase()+"-list.js");
		System.out.println("htmlJS-->ok");
	}
	private static String getSplitEntityName(String subEntityName){
		String splitEntityName = "";
		int startIndex = 0;
		for(int i = 0;i<subEntityName.length();i++){
			if(Character.isUpperCase(subEntityName.charAt(i))){
				if(i==0) continue;
				splitEntityName += "-"+subEntityName.substring(startIndex, i).toLowerCase();
				startIndex = i;
			}
		}
		splitEntityName += "-"+subEntityName.substring(startIndex, subEntityName.length()).toLowerCase();
		System.out.println(splitEntityName.substring(1));
		return splitEntityName.substring(1);
	}

	public static void main(String[] args) {
		createController();
		createService();
		createServiceImpl();
		createEntity();
		createSqlMapperFile();
		createHtmlJS();
		createHtml();
	}

}
