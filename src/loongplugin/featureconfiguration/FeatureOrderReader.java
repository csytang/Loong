/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2010  FeatureIDE Team, University of Magdeburg
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package loongplugin.featureconfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Read the .order file from the project directory. 
 * The constructor need the location of the project as parameter
 * 
 * @author Christian Becker
 */
public class FeatureOrderReader {

	private File file;
	
	private	Scanner scanner = null;
	
	public FeatureOrderReader(File file){
		
		this.file = new File(file.toString()+System.getProperty("file.separator")+".order");
	}
	/**
	 * 
	 * @return Return the  feature order as an ArrayList
	 */
	
	public ArrayList<String> featureOrderRead(){	
		ArrayList<String> list = new ArrayList<String>();
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("Can not read .order file");		
		}
		while(scanner.hasNext()){
			
			list.add(scanner.next());
		}
		
		return list;
	}
	
	
}
