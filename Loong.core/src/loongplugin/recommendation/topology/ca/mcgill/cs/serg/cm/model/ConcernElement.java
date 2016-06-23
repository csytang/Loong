/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.10 $
 */

package loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model;

/**
 * A ConcernElement represents an object that is part of a concern,
 * and the degree of membership of this element to the concern.
 */
class ConcernElement
{
	private static final int MAX_DEGREE = 100;
	
	private int aDegree;
	private String aComment = "";
	
	/** 
	 * Creates a new concern element.
	 * @param pDegree The membership degree, between 0 and 100 incl.
	 */
	public ConcernElement( int pDegree )
	{
		assert pDegree >= 0;
		assert pDegree <= MAX_DEGREE;
		aDegree = pDegree;
	}
	
	/** 
	 * Creates a new concern element, with comment.
	 * @param pDegree The membership degree, between 0 and 100 incl.
	 */
	public ConcernElement( int pDegree, String pComment )
	{
		this( pDegree );
		aComment = pComment;
	}
	
	/**
	 * @return The degree of the element.
	 */
	public int getDegree()
	{
	    return aDegree;
	}
	
	/**
	 * Sets the degree of the element.
	 * Description of ConcernElement
	 * @param pDegree The degree for the element
	 */
	public void setDegree( int pDegree )
	{
		assert pDegree >= 0 ;
		assert pDegree <= MAX_DEGREE ;
		aDegree = pDegree;
	}
	
	/**
	 * @return The comment of the element.
	 */
	public String getComment()
	{
	    return aComment;
	}
	
	/**
	 * Sets the comment of the element.
	 * @param pComment The degree for the element
	 */
	public void setComment( String pComment )
	{
		aComment = pComment;
	}
}
