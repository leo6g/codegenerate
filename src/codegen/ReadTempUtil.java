package codegen;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ReadTempUtil {
	
	public static String readFileContent(String tempName){
		String rtVal = "";
		try{
			InputStream fis = ReadTempUtil.class.getResourceAsStream("/resources/"+tempName);
			byte[] arr = new byte[fis.available()];
			fis.read(arr);
			fis.close();
			rtVal = new String(arr);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return rtVal;
	}
	
	public static String replaceVar(String originalStr,Map<String,String> replacedMap){
		Set<String> keys = replacedMap.keySet();
		for(Iterator<String> it = keys.iterator();it.hasNext();){
			String key = it.next().toString();
			
			String val = replacedMap.get(key);
			if(val != null){
				originalStr= originalStr.replaceAll("#"+key+"#", val);
			}
			
		}
		return originalStr;
	}

}
