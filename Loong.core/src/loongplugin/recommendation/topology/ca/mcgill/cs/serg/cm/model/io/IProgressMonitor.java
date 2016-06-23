/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.3 $
 */

package loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model.io;

/**
 * Behavior describing incremental progress.
 */
public interface IProgressMonitor 
{
	/**
	 * Sets the total number of increments to complete.
	 * @param pTotal The number of increments to complete.
	 */
	void setTotal( int pTotal );
	
	/**
	 * Indicate that progress was completed for 
	 * a specified number of increments.
	 * @param pAmount The number of increments.
	 */
	void worked( int pAmount );
}
