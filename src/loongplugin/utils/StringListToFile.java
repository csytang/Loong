package loongplugin.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Write a list of String to a file
 * @author tangchris
 *
 */

public class StringListToFile {
	private File file;
	private List<String> strlist = new LinkedList<String>();
	public StringListToFile(List<String>stringlist,String filePath){
		file = new File(filePath);
		strlist = stringlist;
	}
	public void writeToFile(){
		try {
			FileOutputStream stream = new FileOutputStream(file);
			for(String str:strlist){
				stream.write(str.getBytes());
				stream.write("\n".getBytes());
			}
			stream.flush();
			stream.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
