package loongplugin.recommendation.topology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.CallLocation;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;



public class SuadeAnalyzer {
	
	// The Eclipse search engine, which could be used for obtain call and called by count;
	private SearchEngine aSearchEngine = new SearchEngine();
	private Map aCalledByCache;
	private Map aCallsCache;
	private boolean aFilterLibraries = true;
	private Map aAccessedByCache;
	private Map aAccessesCache;
	private SearchParticipant[] aParticipants = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
	private AnalyzerSearchRequestor aRequestor = new AnalyzerSearchRequestor();

	/**
	 * Creates an analyzer that filters libraries by default.
	 *
	 */
	public SuadeAnalyzer(){
		aCalledByCache = new HashMap();
		aCallsCache =  new HashMap();
		aAccessedByCache = new HashMap();
		aAccessesCache = new HashMap();
	}
	/** 
	 * Creates a new Analyzer.
	 * @param pFilterLibraries True if field accesses and method calls
	 * to non-source elements sould be included.
	 */
	public SuadeAnalyzer(boolean pFilterLibraries ){
		this();
		aFilterLibraries = pFilterLibraries;
	}
	
	/**
	 * Allows the setting of the filtering option.
	 * @param pFilterLibraries True to filter non source elements from the results.
	 */
	public void setFilter( boolean pFilterLibraries )
	{
		aFilterLibraries = pFilterLibraries;
		clearCache();
	}
	
	/**
	 * Gets the methods called by the given method as found by the Eclipse search engine.
	 * @param pMethod the method for which to find the called methods.
	 * @param pMonitor the progress monitor to report progress to.
	 * @return a Set containing the methods called by the given method.
	 */
	public Set calledBy(IMethod pMethod,IProgressMonitor pMonitor ){
		Set lResults = (Set)aCalledByCache.get( pMethod );
		if( lResults != null )
		{
			return lResults;
		}
		CallHierarchy callHierarchy = CallHierarchy.getDefault();
		 
		IMember[] members = {pMethod};
		
		MethodWrapper[] lCallers = callHierarchy.getCallerRoots(members);
		
	    HashSet lReturn = new HashSet();
	    for( int lI = 0; lI < lCallers.length; lI++ )
	    {
	    	lReturn.add( lCallers[lI].getMember());
	    }
	    aCalledByCache.put( pMethod, lReturn );
	    return lReturn;
	}
	
	/**
	 * Returns the methods which call the given method as found by the Eclipse search engine.
	 * @param pMethod the method for which to find the callers.
	 * @param pMonitor the progress monitor to report progress to.
	 * @return a Set containing the callers of the given method.
	 */
	public Set calls( IMethod pMethod, IProgressMonitor pMonitor )
	{
		Set lResults = (Set)aCallsCache.get( pMethod );
		if( lResults != null )
		{
			return lResults;
		}
		IMember[] members = {pMethod};
		MethodWrapper[] lCallees = CallHierarchy.getDefault().getCalleeRoots( members );
	    HashSet lReturn = new HashSet();
	    for( int lI = 0; lI < lCallees.length; lI++ )
	    {
	    	// Check for weird stuff like constructors of anonymous classes
	    	if( !(lCallees[lI].getMember() instanceof IMethod ))
	    	{
	    		continue;
	    	}
	    	if( !aFilterLibraries || !lCallees[lI].getMember().isBinary() )
	    	{
	    		lReturn.add( lCallees[lI].getMember());
	    	}
	    	if( canBeOverriden( lCallees[lI] ))
	    	{
	    		Set lOverriders = getOverriders( (IMethod)lCallees[lI].getMember(), pMonitor );
	    		lReturn.addAll( lOverriders );
	    	}
	    }
	    aCallsCache.put( pMethod, lReturn );
	    return lReturn;
	}
	/**
	 * Empties the cache of results.
	 */
	public void clearCache()
	{
		aCalledByCache.clear();
		aCallsCache.clear();
		aAccessedByCache.clear();
		aAccessesCache.clear();
	}
	
	/**
	 * Tests whether pMethod can be bound to a different implementation.
	 * Returns true for all methods except statics, finals, constructors, supers.
	 * @param pMethod 
	 * @return
	 */
	private boolean canBeOverriden( MethodWrapper pMethod )
	{
		boolean lReturn = true;
		
		IMethod lMethod = (IMethod)pMethod.getMember();
		
		try
		{
			
			if( lMethod.isConstructor() )
			{
				lReturn = false;
			}
			else if( Flags.isFinal( lMethod.getFlags() ))
			{
				lReturn = false;
			}
			else if( Flags.isStatic( lMethod.getFlags() ))
			{
				lReturn = false;
			}
			else if( isSuper( pMethod ))
			{
				lReturn = false;
			}
		}
		catch( JavaModelException lException )
		{
			//ProblemManager.reportException( lException );
		}
		
		return lReturn;
	}
	
	
	private Set getOverriders( IMethod pMethod, IProgressMonitor pMonitor )
	{
		Set lReturn = new HashSet();
		try
		{
			ITypeHierarchy lHierarchy = pMethod.getDeclaringType().newTypeHierarchy( pMethod.getJavaProject(), pMonitor );
			//if( pMethod.getDeclaringType().isInterface() )
			//{
//				IType[] lImplementors = lHierarchy.getImplementingClasses( pMethod.getDeclaringType());
//				for( int i = 0; i < lImplementors.length; i++ )
//				{
//					IMethod[] lMethods = lImplementors[i].getMethods();
//					for( int j = 0; j < lMethods.length; j++ )
//					{
//						if( lMethods[j].isSimilar( pMethod ))
//						{
//							if( !aFilterLibraries || !lMethods[j].isBinary() )
//					    	{
//								lReturn.add( lMethods[j] );
//					    	}
//						}
//					}
//				}
			//}
//			else
//			{
				IType[] lImplementors = lHierarchy.getAllSubtypes( pMethod.getDeclaringType() );
				for( int lI = 0; lI < lImplementors.length; lI++ )
				{
					IMethod[] lMethods = lImplementors[lI].getMethods();
					for( int lJ = 0; lJ < lMethods.length; lJ++ )
					{
						if( lMethods[lJ].isSimilar( pMethod ))
						{
							if( !aFilterLibraries || !lMethods[lJ].isBinary() )
					    	{
								lReturn.add( lMethods[lJ] );
					    	}
						}
					}
				}
//			}
		}
		catch( JavaModelException lException )
		{
			//ProblemManager.reportException( lException );
		}
		return lReturn;
	}
	
	/**
	 * Tests whether a method call was a super call.
	 * @param pMethod
	 * @return
	 */
	private boolean isSuper( MethodWrapper pMethod )
	{
		boolean lReturn = true;
		
		for( Iterator lI = pMethod.getMethodCall().getCallLocations().iterator(); lI.hasNext(); )
		{
			CallLocation lLocation = (CallLocation) lI.next();
			if( !lLocation.getCallText().trim().startsWith( "super" ))
			{
				lReturn = false;
				break;
			}
		}
		return lReturn;
	}
	
	/**
	 * Returns the fields accessed by the given method as found by the Eclipse search engine.
	 * @param pMethod the method for which to find the accessed fields.
	 * @return a Set containing the fields accessed by the given method.
	 */
	public Set accesses( IMethod pMethod )
	{	
		Set lResults = (Set)aAccessesCache.get( pMethod );
		if( lResults != null )
		{
			return lResults;
		}
		
		Set lReturn = new HashSet();
		aRequestor.beginReporting();
		aParticipants[0] =  SearchEngine.getDefaultSearchParticipant() ;
		try
		{
			aSearchEngine.searchDeclarationsOfAccessedFields( pMethod, aRequestor, null );
			aRequestor.endReporting();
			if( aFilterLibraries )
			{
				for( Iterator lI = aRequestor.getResults().iterator(); lI.hasNext(); )
				{
					IField lNext = (IField)lI.next();
					if( !lNext.isBinary() )
					{
						lReturn.add( lNext );
					}
				}
			}
			else
			{
				lReturn =  aRequestor.getResults();
			}
		}
		catch( CoreException lException )
		{
			//ProblemManager.reportExceptionAsync( lException );
		}
		aAccessesCache.put( pMethod, lReturn );
		return lReturn;
	}
	
	/**
	 * Returns the accessors of the given field as found by the Eclipse search engine in the scope of the parent project.
	 * @param pField the field whose accessors are to be returned.
	 * @return a Set containing the accessors of the given field.
	 */
	public Set accessedBy( IField pField )
	{
		Set lResults = (Set)aAccessedByCache.get( pField );
		if( lResults != null )
		{
			return lResults;
		}
		aRequestor.beginReporting();
		aParticipants[0] =  SearchEngine.getDefaultSearchParticipant() ;
		SearchPattern lPattern = SearchPattern.createPattern( pField, IJavaSearchConstants.REFERENCES );
		IJavaElement[] lProject = new IJavaElement[] {pField.getJavaProject()};
		
		try
		{
			aSearchEngine.search( lPattern, aParticipants, SearchEngine.createJavaSearchScope( lProject , true ), aRequestor, null );
			aRequestor.endReporting();
			aAccessedByCache.put( pField, aRequestor.getResults() );
			return aRequestor.getResults();
		}
		catch( CoreException lException )
		{
			//ProblemManager.reportExceptionAsync( lException );
		}
		return null;
	}
	
}
/**
 * {@inheritDoc}
 */
class AnalyzerSearchRequestor extends SearchRequestor
{	
	private Set aResults = new HashSet();
//	private boolean aIsReporting = false;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.jdt.core.search.SearchMatch)
	 */
	/**
	 * Implements org.eclipse.jdt.core.search.SearchRequestor.acceptSearchMatch
	 * Decides whether a search match should be inculded or not in the results of the search
	 * depending on the state of the preference to include/exclude binary methods.
	 * {@inheritDoc}
	 */
	public void acceptSearchMatch( SearchMatch pMatch ) throws CoreException 
	{
//		if( ((IMember)pMatch.getElement()).isBinary() &&
//				!SuadePlugin.getDefault().getPreferenceStore().getBoolean(
//						SuadePreferencePage.P_INCLUDE_BINARIES) );
//		else if( aIsReporting )
			aResults.add( pMatch.getElement() );
	}
	
	/**
	 * @see org.eclipse.jdt.core.search.SearchRequestor#beginReporting()
	 */
	public void beginReporting()
	{
		aResults = new HashSet();
//		aIsReporting = true;
	}
	
	/**
	 * @see org.eclipse.jdt.core.search.SearchRequestor#endReporting()
	 */
	public void endReporting()
	{
//		aIsReporting = false;
	}


	/**
	 * Gets the results of the search.
	 * @return a Set containing the results of the search.
	 */
	public Set getResults()
	{
		return aResults;
	}
}
