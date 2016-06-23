/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.34 $
 */

package loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Concern Model represents a collection of concerns.  This class
 * also implements the Observable role of the Observer design pattern
 * and a Facade to use the concern model.
 * 
 * Streaming mode. For optimization the Concern model supports a mode for updates
 * that do not result in observer notifications (the streaming mode).  This is
 * useful for multiple updates to the model.  The model can be put in streaming mode 
 * with startStreaming() and returned to normal mode with stopStreaming().  These methods
 * should always be called in pairs in the same scope.
 * 
 * Inconsistencies. Any element in the model can be marked as "inconsistent" using the 
 * makeInconsistent method. Inconsistency is a general attribute that can be used to 
 * make elements that, although part of a concern model, are not in synch with the 
 * environment.
 */
public class ConcernModel 
{
	/** Indicates a unqualified model change event. */
	public static final int DEFAULT = 0;
	
	/** Indicates a model change event where only the comment of an element or concern changes. */
	public static final int COMMENT = 1;
	
	/** Indicates a model change event clearing the view. */
	public static final int CLEAR_VIEW = 2;
	
	private static final int MAX_DEGREE = 100;
	
	private boolean aStreaming = false;
	
	private Map<String, Concern> aConcerns;
	//private List<ConcernModelChangeListener> aListeners;
		
	/** 
	 * Creates a new, empty concern model.
	 */
	public ConcernModel()
	{
		aConcerns = new HashMap<String, Concern>();
		//aListeners = new ArrayList<ConcernModelChangeListener>();
	}
	
	/**
	 * Puts the model in streaming mode.
	 */
	public void startStreaming()
	{
		aStreaming = true;
	}

	/**
	 * Puts the model in normal (non-streaming) mode.
	 * Triggers an update. Should generally be put in a finally
	 * block.
	 */
	public void stopStreaming()
	{
		aStreaming = false;
		//notifyChange();
	}
	
	/**
	 * Puts the model in normal (non-streaming) mode.
	 * Triggers an update if pHasChanged is true.
	 * Should generally be put in a finally block.
	 * @param pHasChanged true if there is a change in the model that should
	 *  be reported to listeners such as the ConcernMapper view.
	 */
	public void stopStreaming( boolean pHasChanged )
	{
		aStreaming = false;
		if( pHasChanged )
		{
			//notifyChange();
		}
	}
	
	/**
	 * @return True if the model is in streaming mode.
	 */
	private boolean isStreaming()
	{
		return aStreaming;
	}
	
	/** 
	 * Resets the model to an empty state.
	 */
	public void reset()
	{
		aConcerns.clear();
		//notifyChange( CLEAR_VIEW );
	}
	
	/**
	 * Returns an array containing the names of all the concerns
	 * in the concern model.
	 * @return The names of the concerns in the concern model.
	 */
	public String[] getConcernNames()
	{
	    return aConcerns.keySet().toArray( new String[aConcerns.size()] );
	}
	
	/**
     * Notifies all observers of a default change in the model.
     */
	/*
    private void notifyChange()
    {
    	if( isStreaming() )
    	{
    		return;
    	}
    	
        for( ConcernModelChangeListener lListener : aListeners )
        {
            lListener.modelChanged( DEFAULT );
        }
    }*/
    
    /**
     * Notifies all observers of a change in the model.
     * @param pChange The type of change.  See the constanst in this class.
     */
	/*
    public void notifyChange( int pChange )
    {
    	if( isStreaming() )
    	{
    		return;
    	}
        for( ConcernModelChangeListener lListener : aListeners )
        {
            lListener.modelChanged( pChange );
        }
    }
	*/
	/**
	 * Adds a listener to the list.
	 * @param pListener The listener to add.
	 */
	/*
	public void addListener( ConcernModelChangeListener pListener )
	{
		aListeners.add( pListener );
	}*/
	
	/**
	 * Removes a Listener from the list.
	 * @param pListener The listener to remove.
	 */
	/*
	public void removeListener( ConcernModelChangeListener pListener )
	{
		aListeners.remove( pListener );
	}
	*/
	
	/** 
	 * Determines whether pName is the name of an existing concern.
	 * @param pName The name to test for.
	 * @return true if pName is the name of a concern in the concern model.
	 */
	public boolean exists( String pName )
	{
		return aConcerns.containsKey( pName );
	}
	
	/**
	 * Determines whether an element already exists in a concern
	 * (whether as inconsistent or not).
	 * @param pConcern The concern to check
	 * @param pElement The element to check.
	 * @return true if the element already exists.
	 */
	public boolean exists( String pConcern, Object pElement )
	{
		if( !exists( pConcern ) )
		{
			return false;
		}
		
		return aConcerns.get( pConcern ).contains( pElement );
	}
	
	/**
	 * Returns all the elements in the concern.
	 * @param pConcern The concern whose elements to return.
	 * @return A Set of elements.  Type unspecified.
	 */
	@SuppressWarnings("unchecked")
	public Set getElements( String pConcern )
	{
	    Set lReturn = null;
	    if( exists( pConcern ))
	    {
	    	Set lTemp = aConcerns.get( pConcern ).getElements();
	    	lReturn = new HashSet( lTemp );
	    }
	    else
	    {
	       // throw new ConcernModelException( "Concern " + pConcern + " does not exist");
	    }
	    return lReturn;
	}
	
	/**
	 * Returns all the elements in the concern.
	 * @param pConcern The concern whose elements to return.
	 * @return A Set of elements.  Type unspecified.
	 */
	@SuppressWarnings("unchecked")
	public Set getAllElements( String pConcern )
	{
	    Set lReturn = null;
	    if( exists( pConcern ))
	    {
	    	lReturn = aConcerns.get( pConcern ).getElements();
	    }
	    else
	    {
	        //throw new ConcernModelException( "Concern " + pConcern + " does not exist");
	    }
	    return lReturn;
	}
	
	/**
	 * Obtains the membership degree for an element pElement in a concern pConcern.
	 * If the concerns exists but pElement is not part of the concern, returns 0.
	 * @param pConcern The concern containing pElement
	 * @param pElement The element to check
	 * @throws ConcernModelException of pConcern does not exist.
	 * @return The degree of pElement, between 0 and 100.  0 if pElement does not
	 * exist.
	 */
	public int getDegree( String pConcern, Object pElement )
	{
	    int lReturn = 0;
	    if( exists( pConcern ))
	    {
	        lReturn = aConcerns.get( pConcern ).getDegree( pElement );
	    }
	    else
	    {
	       // throw new ConcernModelException( "Concern " + pConcern + " does not exist");
	    }
	    return lReturn;
	}
	
	/**
	 * Obtains the comment for an element pElement in a concern pConcern.
	 * If the concerns exists but pElement is not part of the concern, returns the empty string.
	 * @param pConcern The concern containing pElement
	 * @param pElement The element to check
	 * @throws ConcernModelException of pConcern does not exist.
	 * @return The comment of pElement, empty string if pElement does not
	 * exist.
	 */
	public String getElementComment( String pConcern, Object pElement )
	{
	    String lReturn = "";
	    if( exists( pConcern ))
	    {
	        lReturn = aConcerns.get( pConcern ).getComment( pElement );
	    }
	    else
	    {
	       // throw new ConcernModelException( "Concern " + pConcern + " does not exist");
	    }
	    return lReturn;
	}
	
	/**
	 * Obtains the comment for an a concern pConcern.
	 * @param pConcern The concern to query
	 * @throws ConcernModelException of pConcern does not exist.
	 * @return The comment of pConcern
	 */
	public String getConcernComment( String pConcern )
	{
	    String lReturn = "";
	    if( exists( pConcern ))
	    {
	        lReturn = aConcerns.get( pConcern ).getComment();
	    }
	    else
	    {
	        //throw new ConcernModelException( "Concern " + pConcern + " does not exist");
	    }
	    return lReturn;
	}
		
	/**
	 * Sets the membership degree for an element in a concern.
	 * @param pConcern The name of the concern containing the element. Must exist.
	 * @param pElement The element whose degree to change.  Must exist.
	 * @param pDegree A value between 0 and 100.
	 * @throws ConcernModelException if pConcern or pElement do not exist.
	 */
	public void setDegree( String pConcern, Object pElement, int pDegree )
	{
		if( !exists( pConcern, pElement ))
		{
			//throw new ConcernModelException( pElement.toString() + " does not exist in concern " + pConcern );
		}
		if( (pDegree < 0) || (pDegree > MAX_DEGREE ))
		{
			//throw new ConcernModelException( "Degree must be between 0 and 100" );
		}
		
		aConcerns.get( pConcern ).setDegree( pElement, pDegree );
		//notifyChange();
	}
	
	/**
	 * Sets the comment for an element in a concern.
	 * @param pConcern The name of the concern containing the element. Must exist.
	 * @param pElement The element whose comment to change.  Must exist.
	 * @param pComment A non-null string
	 * @throws ConcernModelException if pConcern or pElement do not exist.
	 */
	public void setElementComment( String pConcern, Object pElement, String pComment )
	{
		if( !exists( pConcern, pElement ))
		{
			//throw new ConcernModelException( pElement.toString() + " does not exist in concern " + pConcern );
		}
		if( pComment == null )
		{
			//throw new ConcernModelException( "Comment must not be null" );
		}
		
		aConcerns.get( pConcern ).setComment( pElement, pComment );
		//notifyChange( COMMENT );
	}
	
	/**
	 * Adds a new empty concern to the model.
	 * @param pName The name of the concern.  No concern with the
	 * same name must exist in the model.  The concern must not be null or the empty string.
	 * @throws ConcernModelException if one of the preconditions does not hold
	 */
	public void newConcern( String pName )
	{
		if( exists( pName ))
		{
			//throw new ConcernModelException( "Trying to create a concern with a name in use: " + pName );
		}
		if( pName == null )
		{
			//throw new ConcernModelException( "Concern names cannot be null" );
		}
		if( pName.equals( "" ))
		{
			//throw new ConcernModelException( "Concern names cannot be empty" );
		}
		aConcerns.put( pName, new Concern());
		//notifyChange();
	}
	
	/**
	 * Adds an element to a concern.
	 * @param pConcern The concern in which to add the element.  Must exist
	 * @param pElement The element to add.  Must not exist.
	 * @param pDegree The degree.  Must be between 0 and 100.
	 * @throws ConcernModelException If any precondition does not hold.
	 */
	public void addElement( String pConcern, Object pElement, int pDegree )
	{
		Concern lConcern = aConcerns.get( pConcern );
		if( lConcern == null )
		{
			//throw new ConcernModelException( pConcern + " does not exist");
		}
		if( lConcern.contains( pElement ) )
		{
			//throw new ConcernModelException( pConcern + " already contains " + pElement );
		}
		if( (pDegree < 0) || (pDegree > MAX_DEGREE ))
		{
			//throw new ConcernModelException( "Degree must be between 0 and 100" );
		}
		lConcern.addElement( pElement, pDegree );
		//notifyChange();
	}
	
	/**
	 * Convenience method. Calls addElement(String,Object,int) and
	 * adds an element to the model with maximum degree value.
	 * @param pConcern The concern in which to add the element. Must exist.
	 * @param pElement The element to add. Must not exist.
	 * @throws ConcernModelException If any precondition does not hold.
	 */
	public void addElement( String pConcern, Object pElement )
	{
		addElement( pConcern, pElement, MAX_DEGREE );
	}
	
	/**
	 * Removes a concern from the model.
	 * @param pConcern The concern to remove.  Must exist.
	 * @throws ConcernModelException if pConcern does not exist.
	 */
	public void deleteConcern( String pConcern )
	{
		if( !exists( pConcern ))
		{
			//throw new ConcernModelException( pConcern + " does not exist");
		}
		
		aConcerns.remove( pConcern );

		//notifyChange();
	}
	
	/**
	 * Removes an element from its concern.
	 * @param pConcern The concern containing the element to remove.  Must exist.
	 * @param pElement The element to remove.  Must exist.
	 * @throws ConcernModelException If either pConcern or pElement do not exist in the model.
	 */
	public void deleteElement( String pConcern, Object pElement )
	{
		if( !exists( pConcern, pElement ))
		{
			//throw new ConcernModelException( pElement.toString() + " does not exist in concern " + pConcern );
		}
		
		aConcerns.get( pConcern ).deleteElement( pElement );
		
		//notifyChange();
	}
	
	/**
	 * Changes the name of a concern.
	 * @param pOldName The name of the concern to change.  This concern must exist.
	 * @param pNewName The new name for the concern.
	 * @throws ConcernModelException if pOldName is not an existing concern.
	 */
	public void renameConcern( String pOldName, String pNewName )
	{
		if( !exists( pOldName ))
		{
			//throw new ConcernModelException( "Concern " + pOldName + " does not exist in the model.");
		}
		
		aConcerns.put( pNewName, aConcerns.get( pOldName ) );
		aConcerns.remove( pOldName );
		//notifyChange();
	}
	
	/**
	 * Sets the comment of a concern.
	 * @param pConcern The name of the concern.  This concern must exist.
	 * @param pComment The new comment for the concern.
	 * @throws ConcernModelException if pConcern is not an existing concern.
	 */
	public void setConcernComment( String pConcern, String pComment )
	{
		if( !exists( pConcern ))
		{
			//throw new ConcernModelException( "Concern " + pConcern + " does not exist in the model.");
		}
		
		aConcerns.get( pConcern ).setComment( pComment );
		//notifyChange( COMMENT );
	}
}
