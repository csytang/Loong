/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.6 $
 */

package loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model.io;

import java.io.IOException;

/**
 * An exception in the transfer of the model.
 */
public class ModelIOException extends IOException 
{
	/**
	 * An exception with a message.
	 * @param pMessage A message for the exception
	 */
	public ModelIOException( String pMessage ) 
	{
		super( pMessage );
	}
}
