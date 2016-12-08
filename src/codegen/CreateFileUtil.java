package codegen;

import java.io.File;
import java.io.FileOutputStream;

public class CreateFileUtil {
	
	public static final String charsetEncoding="UTF-8";
	
	public static void createFile(String fileContent,String filePath,String fileName){
		try{
			
			String outPutFilePath = ConfigHelper.getValue("outputPath")+File.separator + filePath;
			
			File dir = new File(outPutFilePath);
			if (!dir.exists()) {
				System.out.println(dir.mkdirs());
			}
			
			
			FileOutputStream fos = new FileOutputStream(new File(
					ConfigHelper.getValue("outputPath")+File.separator
					+ filePath +File.separator +fileName));
			fos.write(fileContent.getBytes(charsetEncoding));
			fos.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
