package loongplugin.recommendation.topology;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;

/**
 * A suggestion represents an object that is suggested by the plugin
 * It is described by the object it represents, the reason it was suggested
 * and the degree to which it is certain the suggestion is relevant.
 * */
public class Suggestion implements  IAdaptable{

	private IMember aElement;
	
	private Set aConcernElements = new HashSet();
	
	private String aName;
	
	private String aReason;
	
	private int aDegree;
	
	
	/**
	 * Creates a new suggestion.
	 * @param pElement The object represented by the suggestion
	 * @param pConcernElement The element from the concern model that caused the object to be suggested
	 * @param pReason The reason why the object was suggested
	 * @param pDegree The degree of certainty
	 * */
	public Suggestion( IMember pElement, IMember pConcernElement, String pReason, int pDegree )
	{
		aElement = pElement;
		aName = pElement.getElementName();
		aConcernElements.add( pConcernElement );
		setReason( pReason );
		setDegree( pDegree );
	}
	
	/**
	 * @see Object#toString()
	 * @return a String representing the suggestion
	 */
	public String toString()
	{
		return aElement.getElementName() + " (" + aDegree + ")";
	}
	
	
	/**
	 * @return The degree of the element.
	 * */
	public int getDegree()
	{
		return aDegree;
	}

	/**
	 * Sets the degree of the element.
	 * @param pDegree the degree to set.
	 * */
	public void setDegree( int pDegree )
	{
		aDegree = pDegree;
	}

	/**
	 * @return The reason the element was suggested
	 * */
	public String getReason()
	{
		return aReason;
	}

	/**
	 * Sets the reason the element was suggested.
	 * @param pReason the reason to set.
	 * */
	public void setReason( String pReason )
	{
		aReason = pReason;
	}
	
	/**
	 * Appends another reason for which the element was suggested.
	 * @param pReason the reason to append
	 * */
	public void appendReason( String pReason )
	{
		aReason += ", " + pReason;
	}

	/**
	 * @return The element represented by the suggestion
	 * */
	public IMember getElement()
	{
		return aElement;
	}
	
	/**
	 *@return The simple name of the element represented by the suggestion 
	 */
	public String getName()
	{
		return aName;
	}

	/**
	 * 
	 * @return The set of elments from the set of interest related to the suggested element
	 */
	public Set getConcernElements()
	{
		return aConcernElements;
	}

	/**
	 * 
	 * @param pConcernElements The element that is part of the set of interest
	 * 							and that is related to the suggested element
	 */
	public void addConcernElement(Set pConcernElements)
	{
		for(Iterator lI = pConcernElements.iterator(); lI.hasNext(); )
		{
			Object lNext = lI.next();
			if( !aConcernElements.contains( lNext ) && lNext instanceof IMember)
			{
				aConcernElements.add( lNext );
			}
		}

	}

	public Object getAdapter( Class adapter )
	{
		if( adapter == IJavaElement.class )
		{
			return getElement();
		}
		else
		{
			return null;
		}
	}

}
