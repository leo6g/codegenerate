package util;

public class CastColNameToPropNameUtil {
	
	public static String cast(final String colName){
		if(colName.indexOf("_")>0){
			StringBuilder sb = new StringBuilder();
			String [] arr = colName.split("_");
			int index = 0;
			for(String str:arr){
				if(index == 0){
					sb.append(str.toLowerCase());
				}else{
					sb.append(str.substring(0, 1).toUpperCase());
					sb.append(str.substring(1).toLowerCase());
				}
				index++;
			}
			return sb.toString();
		}else{
			return new String(colName).toLowerCase();
		}
	}
	
	
	public static void main(String[] ar){
		String test = CastColNameToPropNameUtil.cast("NAB_TEST_CDS");
		System.out.print(test);
	}

}
