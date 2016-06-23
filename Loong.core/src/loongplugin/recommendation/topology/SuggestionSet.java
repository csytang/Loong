package loongplugin.recommendation.topology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IMember;


/**
 * A SuggestionSet contains all the suggestions pertaining to a particular concern.
 */
public class SuggestionSet {
	private Set aSuggestions;
	private List aListeners;
	
	/**
	 * The constructor. Creates a new, empty set of suggestions
	 * */
	public SuggestionSet()
	{
		aSuggestions = new HashSet();
		aListeners = new ArrayList();
	}
	
	/**
	 * @return the set of suggestions.
	 * */
	public Set getSuggestions()
	{
		return aSuggestions;
	}
	
	/**
	 * @return a String representing all the suggestions in the SuggestionSet
	 */
	public String toString()
	{
		String lReturn = "";
		for( Iterator lI = aSuggestions.iterator(); lI.hasNext(); )
		{
			lReturn += lI.next() + "; ";
		}
		return lReturn;
	}
	
	/**
	 * 
	 * @param pElement the element for which to return the suggestion.
	 * @return The suggestion associated with the given element.
	 */
	
	public Suggestion getSuggestion( IMember pElement )
	{
		for(Iterator lI = aSuggestions.iterator(); lI.hasNext(); )
		{
			Object lNext = lI.next();
			if( lNext instanceof Suggestion)
			{
				Suggestion lSuggestion = (Suggestion)lNext;
				if( lSuggestion.getElement().equals( pElement ))
				{
					return lSuggestion;
				}
			}
		}
		return null;
	}
	
	/**
	 * Sets the suggestions of SuggestionSet to the suggestions of the given SuggestionSet.
	 * @param pSuggestionSet the SuggestionSet to be set.
	 */
	public void setSuggestionSet( SuggestionSet pSuggestionSet )
	{
		aSuggestions = pSuggestionSet.getSuggestions();
		notifyChange();
	}
	
	/**
	 * Adds a suggestion to the suggestion set.
	 * If a suggestion with the same IMember already exists:
	 * the existing suggestion's degree is set to the max of the added and the existing suggestions,
	 * the reason strings of the existing and added suggestions are concatenated,
	 * and new related elements are added to the set of related elements,
	 * but the suggestion is not duplicated.
	 * @param pSuggestion The suggestion to add to the set
	 * */
	public void addSuggestion( Suggestion pSuggestion )
	{
		boolean lDuplicate = false;
		for( Iterator lI = aSuggestions.iterator(); lI.hasNext(); )
		{
			Suggestion lNext =(Suggestion)lI.next();
			if( lNext.getElement().equals( pSuggestion.getElement() ))
			{
				if( lNext.getDegree() < pSuggestion.getDegree() )
				{
					lNext.setDegree( pSuggestion.getDegree() );
				}
				lNext.appendReason( pSuggestion.getReason() );
				lNext.addConcernElement( pSuggestion.getConcernElements() );
				lDuplicate = true;
			}
		}
		if( !lDuplicate )
		{
			aSuggestions.add( pSuggestion );
		}
		notifyChange();
	}
	
	/**
	 * Removes a suggestion from the suggestion set.
	 * @param pSuggestion The suggestion to remove from the set
	 * */
	public void removeSuggestion( Suggestion pSuggestion )
	{
		aSuggestions.remove( pSuggestion );
		notifyChange();
	}
	
	/**
	 * Clears the set of suggestions.
	 */
	public void clear()
	{
		aSuggestions.clear();
	}

	/**
	 * Adds a litener to the list. 
	 * @param pListener The listener to add.
	 * */
	public void addListener( SuggestionArrayChangeListener pListener )
	{
		aListeners.add( pListener );
	}
	
	/**
	 * Removes a Listener from the list.
	 * @param pListener The listener to remove.
	 */
	public void removeListener( SuggestionArrayChangeListener pListener )
	{
		aListeners.remove( pListener );
	}
	
	/**
     * Notifies all observers of a change in the suggestion set.
     */
    private void notifyChange()
    {
        for( int lI = 0; lI < aListeners.size(); lI++ )
        {
            ((SuggestionArrayChangeListener)aListeners.get( lI )).suggestionsChanged();
        }
    }
    
}

