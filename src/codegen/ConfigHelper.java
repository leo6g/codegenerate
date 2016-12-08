package codegen;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;



public class ConfigHelper {
	public static final Properties prop = new Properties();

	static {
		try {
			InputStream is = ConfigHelper.class.getResourceAsStream("/config.properties");
			BufferedReader bf = new BufferedReader(new  InputStreamReader(is,"UTF-8"));
			
			prop.load(bf);
			is.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getValue(String key) {
		return prop.getProperty(key);
	}
	
}