package loongpluginfmrtool.toolbox.bunch;

import java.io.IOException;
import java.io.InputStream;

public class Bunch {
	public Bunch(){
		// Run a java app in a separate system process
		Process proc;
		try {
			
			
			proc = Runtime.getRuntime().exec("java -jar Bunch-3.5.jar");

			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
