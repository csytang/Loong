/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.12 $
 */

package loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A concern is a named entity containing elements.
 */
class Concern
{
	private Map<Object, ConcernElement> aElements;
	private String aComment = ""; // Note: never null
	
	/**
	 * Creates a new, empty concern.
	 */
	public Concern()
	{
		aElements = new HashMap<Object, ConcernElement>();
	}
	
	/**
	 * Attaches pComment to this concern.
	 * @param pComment The comment to attach.
	 */
	public void setComment( String pComment )
	{
		aComment = pComment;
	}
	
	/**
	 * @return The comment attached to this concern.
	 */
	public String getComment()
	{
		return aComment;
	}
	
	/**
	 * Returns the comment for an element in this concern model,
	 * or empty string if the element does not exist.
	 * @param pElement The element to check.,
	 * @return The comment for this element.
	 */
	public String getComment( Object pElement )
	{
	    String lReturn = "";
	    ConcernElement lElement = aElements.get( pElement );
	    if( lElement != null )
	    {
	        lReturn = lElement.getComment();
	    }
	    return lReturn;
	}
	
	/**
	 * Adds an element.  If an element with the same hashCode as pElement already
	 * exists, the old element is overwritten.
	 * @param pElement The element to add
	 * @param pDegree It degree.  Must be between 0 and 100 incl.
	 */
	public void addElement( Object pElement, int pDegree )
	{
		aElements.put( pElement, new ConcernElement( pDegree ));
	}
	
	/**
	 * Indicates wether pElement exists in the concern.
	 * @param pElement The object to test.
	 * @return true if pElement is part of the model.
	 */
	public boolean contains( Object pElement )
	{
		return aElements.containsKey( pElement );
	}
	
	/**
	 * Returns the degree for an element in this concern model,
	 * or 0 if the element does not exist.
	 * @param pElement The element to check.,
	 * @return The membership degree for this element.
	 */
	public int getDegree( Object pElement )
	{
	    int lReturn = 0;
	    ConcernElement lElement = aElements.get( pElement );
	    if( lElement != null )
	    {
	        lReturn = lElement.getDegree();
	    }
	    return lReturn;
	}
	
	/**
	 * @return All the elements in the set.
	 */
	public Set<Object> getElements()
	{
	    return aElements.keySet();
	}
	
	/**
	 * Sets the degree of an element, overriding any previous value.
	 * Does nothing if the element does not exist.
	 * @param pElement The element whose degree to set.
	 * @param pDegree The degree for the element
	 */
	public void setDegree( Object pElement, int pDegree )
	{
		ConcernElement lElement = aElements.get( pElement );
		if( lElement != null )
		{
			lElement.setDegree( pDegree );
		}
	}
	
	/**
	 * Sets the comment of an element, overriding any previous value.
	 * Does nothing if the element does not exist.
	 * @param pElement The element whose degree to set.
	 * @param pComment The comment for the element
	 */
	public void setComment( Object pElement, String pComment )
	{
		ConcernElement lElement = aElements.get( pElement );
		if( lElement != null )
		{
			lElement.setComment( pComment );
		}
	}
	
	/**
	 * Removes pElement from this concern.  If it does not exist, nothing happens.
	 * @param pElement The element to remove.
	 */
	public void deleteElement( Object pElement )
	{
		aElements.remove( pElement );
	}
}
