package loongplugin.source.database;
/* JayFX - A Fact Extractor Plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~swevo/jayfx)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.5 $
 */
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import loongplugin.feature.Feature;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LRelation;
/**
 * A database storing all the relations between different program elements.
 */
public class ProgramDatabase {
	/**
	 * Data bundle associated with an element.  Contains modifier 
	 * flags and a map linking relations to their ranges.
	 * an IElement instance.
	 */
	
	
	class Bundle implements Serializable{
	
		private static final long serialVersionUID = 1L;
		private Map<LRelation, Set<LElement>> aRelations;
		
		/**
		 * Creates a new, empty information bundle.
		 * @param pModifier A modifier flag.
		 */
		public Bundle()
		{
			aRelations = new HashMap<LRelation, Set<LElement>>();
		}
		
		/**
		 * @return The Map of relations to range.  never null.
		 */
		public Map<LRelation, Set<LElement>> getRelationMap()
		{
			return aRelations;
		}
	}
	
	// Maps IElements (unique because of the Flyweight pattern
	// to bundles containing modifiers and relations 
    private Map<LElement, Bundle> aElements; 
    
    private Map<String, LElement> elementIndexMap;
    
    private boolean debug = false;
    /**
     * Creates an empty program database.
     */
    public ProgramDatabase() 
    {
        aElements = new HashMap<LElement, Bundle>();
        elementIndexMap = new HashMap<String, LElement>();
    }
    
    /**
     * Returns all the elements indexed in the database.
     * @return A Set of IElement objects
     */
    public Set<LElement> getAllElements()
    {
    	return aElements.keySet();
    }
	
    /**
     * Returns whether an element is indexed in the database.
     * @param pElement An element to check for.  Should not be null.
     * @return Whether the database has information about pElement.
     */
    public boolean contains( LElement pElement )
    {
    	assert( pElement != null );
        return aElements.containsKey( pElement );
    }
    
    /**
     * Adds an element in the database.  The element is initialized with
     * an empty relation set.  If the element is already in the database,
     * nothing happens.
     * @param pElement The element to add.  Should never be null.
     * @param pModifier The modifier flags for this element.
     */
    public void addElement(LElement pElement)
    {
    	assert( pElement != null );
        if( !aElements.containsKey( pElement ))
        {
            aElements.put(pElement,new Bundle());
            elementIndexMap.put(pElement.getId(), pElement);
        }
    }
    
    /** Adds a relation pRelation between pElement1 and pElement2.
     * If pElement1 or pElement2 does not exist in the database, an exception is raised,
     * so these should always be added first.
     * @param pElement1 The first element in the relation, never null.
     * @param pRelation The relation, never null.
     * @param pElement2 The second element in the relation, never null.
     * @throws ElementNotFoundException If pElement1 or pElement2 is not found in the database.
     */
    public void addRelation( LElement pElement1, LRelation pRelation, LElement pElement2 ) throws ElementNotFoundException
    {
    	assert( pElement1 != null );
    	assert( pElement2 != null );
    	assert( pRelation != null );
    	
    	if( !contains( pElement1 ))
    		throw new ElementNotFoundException( pElement1.getId() );
    	if( !contains( pElement2 ))
    		throw new ElementNotFoundException( pElement2.getId() );
    	
        Map<LRelation, Set<LElement>> lRelations =  ( aElements.get( pElement1 )).getRelationMap();
        assert( lRelations != null );
        
        Set<LElement> lElements = lRelations.get( pRelation );
        if( lElements == null )
        {
            lElements = new HashSet<LElement>();
            lRelations.put( pRelation, lElements );
        }
        lElements.add( pElement2 );
    }
    
    /**
     * Returns the set of elements related to the domain element through the
     * specified relation.
     * @param pElement The domain element.  Cannot be null.
     * @param pRelation The target relation.  Cannot be null.
     * @return A Set of IElement representing the desired range.  Never null.
     * @throws ElementNotFoundException If pElement is not indexed in the database
     */
    public Set<LElement> getRange( LElement pElement, LRelation... pRelations) throws ElementNotFoundException
    {
    	assert( pElement != null );
    	assert( pRelations != null );
    	if( !contains( pElement ))
    		throw new ElementNotFoundException( pElement.getId() );
    	
        Set<LElement> lReturn = new HashSet<LElement>();
        Map<LRelation, Set<LElement>> lRelations = ( aElements.get( pElement )).getRelationMap();
        for (LRelation lRelation : pRelations){
			if (lRelations.containsKey(lRelation)) {
				lReturn.addAll(lRelations.get(lRelation));
			}
    	}
        return lReturn;
        
        
    }
    
    /**
     * Convenience method to add a relatio and its transpose at the same time.
     * @param pElement1 The domain of the relation.  Should not be null.
     * @param pRelation The Relation relating the domain to the range.  Should not be null.
     * @param pElement2 The range of the relation.  Should not be null.
     * @throws ElementNotFoundException if either of pElement1 or pElement2 are not
     * indexed in the database.
     */
    public void addRelationAndTranspose(LElement pElement1, LRelation pRelation, LElement pElement2 ) throws ElementNotFoundException
    {
    	assert( pElement1 != null );
    	assert( pElement2 != null );
    	assert( pRelation != null );
    	
    	if( !contains( pElement1 )){
    		return;
    		//throw new ElementNotFoundException( pElement1.getId() );
    	}
    	if( !contains( pElement2 )){
    		return;
    		//throw new ElementNotFoundException( pElement2.getId() );
    	}
    	
    	if (contains(pElement1) && contains(pElement2)) {
			addRelation(pElement1, pRelation, pElement2);
			addRelation(pElement2, pRelation.getInverseRelation(), pElement1);
		}
    }
    
    /** 
     * Returns whether pElements has any associated relations.
     * @param pElement The element to check.  Must not be null and exist in the
     * database.
     * @return True if pElement has any associated relations.
     * @throws ElementNotFoundException If either pFrom or pTo is not indexed in the database.
     */
    public boolean hasRelations( LElement pElement ) throws ElementNotFoundException
    {
    	assert( pElement != null );
    	if( !contains( pElement ))
    		throw new ElementNotFoundException( pElement.getId() );
    	
       Map lRelations = ((Bundle)aElements.get( pElement )).getRelationMap();
       return !lRelations.isEmpty();
    }
    
    public Map<LRelation, Set<LElement>> getRelationMap(LElement pElement) throws ElementNotFoundException{
    	assert( pElement != null );
    	if( !contains( pElement ))
    		throw new ElementNotFoundException( pElement.getId() );
    	Map lRelations = ((Bundle)aElements.get( pElement )).getRelationMap();
    	return lRelations;
    }

    
    /**
     * Dumps an image of the database to System.out.
     * For testing purposes.  Can be removed from stable releases.
     */
    public void dump()
    {
        for( Iterator i = aElements.keySet().iterator(); i.hasNext(); )
        {
        	LElement lElement1 = (LElement)i.next();
            System.out.println( lElement1 );
            Map lRelations = (Map)aElements.get( lElement1 );
            for( Iterator j = lRelations.keySet().iterator(); j.hasNext(); )
            {
                LRelation lRelation = (LRelation)j.next();
                System.out.println("    " + lRelation );
                for( Iterator k = ((Set)lRelations.get( lRelation )).iterator(); k.hasNext(); )
                {
                    System.out.println("        " + k.next() );
                }
            }
        }
    }

	public LElement getElement(String id) {
		// TODO Auto-generated method stub
		return elementIndexMap.get(id);
	}
    
   
}
