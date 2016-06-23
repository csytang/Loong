/* ConcernMapper - A concern modelling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.24 $
 */

package loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model.io;

import org.eclipse.jdt.core.IJavaProject;

/**
 * A singleton to control the converter used by the plug-in.
 */
public final class Converter
{
	private static IConverter aInstance = new EclipseHandleConverter();
	private static IJavaProject aJavaProject;
	
	private Converter()
	{}
	
	/**
	 * Sets the Java project to be used by any converter instance.
	 * @param pProject The project.
	 */
	public static synchronized void setJavaProject( IJavaProject pProject )
	{
		aJavaProject = pProject;
	}
	
	/**
	 * @return The Java project stored by this Converter.
	 */
	public static synchronized IJavaProject getJavaProject()
	{
		return aJavaProject;
	}
	
	/**
	 * Returns an instance of the converter.
	 * @return The converter.
	 */
	public static IConverter getInstance()
	{
		return aInstance;
	}
}
