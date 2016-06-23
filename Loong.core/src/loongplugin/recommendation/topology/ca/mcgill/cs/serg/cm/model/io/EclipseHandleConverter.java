/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.7 $
 */

package loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model.io;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;

/**
 * Serializes Java elements to strings using Eclipse handles.
 */
public class EclipseHandleConverter implements IConverter 
{
	/**
	 * @see ca.mcgill.cs.serg.cm.model.io.IConverter#toField(java.lang.String)
	 * @param pField The field is an Eclipse handle.  
	 * @return See above.
	 * @throws ConversionException See above.
	 */
	public IField toField( String pField ) throws ConversionException 
	{
		IJavaElement lReturn = attemptTranslation( pField );
		if( lReturn == null )
		{
			throw new ConversionException( "Cannot create IJavaElement from handle: " + pField );
		}
		if( !(lReturn instanceof IField ))
		{
			throw new ConversionException( "Handle does not correspond to a field: " + pField );
		}
		return (IField)lReturn;
	}

	/**
	 * @see ca.mcgill.cs.serg.cm.model.io.IConverter#toIDString(org.eclipse.jdt.core.IField)
	 * @param pField See above.
	 * @return See above.
	 */
	public String toIDString( IField pField ) 
	{
		return pField.getHandleIdentifier();
	}

	/**
	 * @see ca.mcgill.cs.serg.cm.model.io.IConverter#toIDString(org.eclipse.jdt.core.IMethod)
	 * @param pMethod See above.
	 * @return See above.
	 */
	public String toIDString( IMethod pMethod ) 
	{
		return pMethod.getHandleIdentifier();
	}

	/** 
	 * @see ca.mcgill.cs.serg.cm.model.io.IConverter#toMethod(java.lang.String)
	 * @param pMethod The method is an Eclipse handle. 
	 * @return See above.
	 * @throws ConversionException See above. 
	 */
	public IMethod toMethod( String pMethod ) throws ConversionException 
	{
		IJavaElement lReturn = attemptTranslation( pMethod );
		if( lReturn == null ) 
		{
			throw new ConversionException( "Cannot create IJavaElement from handle: " + pMethod );
		}
		if( !(lReturn instanceof IMethod ))
		{
			throw new ConversionException( "Handle does not correspond to a method: " + pMethod );
		}
		return (IMethod)lReturn;
	}
	
	/*
	 * Ensures that the project that the handles were stored with corresponds to
	 * the current project.
	 */
	private String adaptHandle( String pHandle, IProject pProject )
	{
		int lSlashIndex = pHandle.indexOf( '/' );
		String lReturn = pHandle;
		if( lSlashIndex > 0 )
		{
			String lTestString = "=" + pProject.getName();
			String lSubstring = pHandle.substring( 0, lSlashIndex );
			if( !lSubstring.equals( lTestString ))
			{
				lReturn = lTestString + pHandle.substring( lSlashIndex );
			}
		}
		return lReturn;
	}
	
	// returns null if no translation found
	private IJavaElement attemptTranslation( String pHandle )
	{
		IJavaElement lInitial = JavaCore.create( pHandle );
		IJavaElement lReturn = lInitial;
		if( (lReturn == null ) || !( lReturn.exists() ))
		{
			// Try to adapt handle
			IProject lCurrentProject = Converter.getJavaProject().getProject();
			lReturn = JavaCore.create( adaptHandle( pHandle, lCurrentProject ));
			
			if( ( lReturn != null ) && (lReturn.exists() ))
			{
				return lReturn;
			}
			else
			{
				// Try all the dependents
				try
				{
					IProject[] lReferencedProjects = lCurrentProject.getReferencedProjects();
					for( IProject lChild : lReferencedProjects )
					{
						lReturn = JavaCore.create( adaptHandle( pHandle, lChild ));
						if( ( lReturn != null ) && (lReturn.exists() ))
						{
							return lReturn;
						}
					}
				}
				catch( CoreException lException )
				{
					lReturn = lInitial;
				}
			}
		}
		return lInitial;
	}
}
