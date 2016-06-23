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
 * Stores tags for elements and attibutes in the XML representation
 * of concerns.
 */
public class XMLTags 
{
	/**
	 * XML Attributes.
	 */
	public enum Attributes
	{
		DEGREE, FORMAT, ID, NAME, TYPE, COMMENT;
		
		/** 
		 * @see java.lang.Enum#toString()
		 * {@inheritDoc}
		 */
		public String toString()
		{
			return name().toLowerCase();
		}
	}
	
	/**
	 * XML Elements.
	 */
	public enum Elements
	{
		CONCERN, ELEMENT, MODEL;
		
		/** 
		 * @see java.lang.Enum#toString()
		 * {@inheritDoc}
		 */
		public String toString()
		{
			return name().toLowerCase();
		}
	}
	
	/**
	 * XML Values.
	 */
	public enum Values
	{
		FIELD, METHOD;
		
		/** 
		 * @see java.lang.Enum#toString()
		 * {@inheritDoc}
		 */
		public String toString()
		{
			return name().toLowerCase();
		}
	}
}
