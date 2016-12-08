package pojo;

public class ColumnInfo {
	
	private String columnName; // 列名称
	
	private String columnType; //列类型
	
	private String comment;// 列备注--注解
	
	private String javaPropName; // 对应的java属性名称
	
	private Integer dataLength; // 字段长度
	
	private String nullAble;
	

	public String getNullAble() {
		return nullAble;
	}

	public void setNullAble(String nullAble) {
		this.nullAble = nullAble;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getJavaPropName() {
		return javaPropName;
	}

	public void setJavaPropName(String javaPropName) {
		this.javaPropName = javaPropName;
	}

	public String toString(){
		return "{columnName:"+columnName+",columnType:"+columnType+",comment:"+comment+",javaPropName:"+javaPropName+",dataLenth:"+dataLength+"}";
	}

	public Integer getDataLength() {
		return dataLength;
	}

	public void setDataLength(Integer dataLength) {
		this.dataLength = dataLength;
	}
	
	

}
