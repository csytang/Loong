/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.30 $
 */

package loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model.io;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model.ConcernModel;



/**
 * Reads in a Concern model stored in XML. Elements that cannot be converter 
 * are stored in the model as SerializedElements.
 */
public class ModelReader 
{
	private ConcernModel aModel;
	private IProgressMonitor aMonitor;
	private int aSkipped = 0; // Number of serialized elements in the last read.
	
	/**
	 * Reads a file into a Concern Model. The model will be reset.
	 * @param pModel The concern model to read into.
	 */
	public ModelReader( ConcernModel pModel )
	{
		aModel = pModel;
	}
	
	/**
     * Builds a Concern Model by loading it from a file.
     * @param pFile The file to load the model from
     * @param pMonitor The progress monitor for this task.  Should not be null.
     * @return The number of elements skipped while reading this file.
     * @throws ModelIOException if there is any problem reading the file.
     */
    public synchronized int read( IFile pFile, IProgressMonitor pMonitor ) throws ModelIOException
    {
    	aMonitor = pMonitor;
    	assert aMonitor != null;
    	aSkipped = 0; // This is not thread safe
    	
    	// Determines the Java project containing the file and use
    	// it to configure the Converter.
    	IProject lProject = pFile.getProject();
    	if( lProject != null )
    	{
    		IJavaProject lJavaProject = JavaCore.create( lProject );
    		if( lJavaProject != null )
    		{
    			Converter.setJavaProject( lJavaProject );
    		}
    	}
    	
    	Document lDocument = null;
    	InputStream lContents = null;
		try
		{
			DocumentBuilderFactory lFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder lBuilder = lFactory.newDocumentBuilder();
			lContents = pFile.getContents();
			lDocument = lBuilder.parse( lContents );
		}
		catch( ParserConfigurationException lException )
		{
			throw new ModelIOException( "Parser configuration problem. " + lException.getMessage() );
		}
		catch( SAXException lException )
		{
			throw new ModelIOException( "Could not parse input. " + lException.getMessage() );
		}
		catch( IOException lException )
		{
			throw new ModelIOException( "IO Exception while parsing input. " + lException.getMessage() );
		}
		catch( CoreException lException )
		{
		    throw new ModelIOException( "Could not obtain file content. " + lException.getMessage() );
		}
		finally
		{
			try
			{
				if( lContents != null )
				{
					lContents.close();
				}
			}
			catch( IOException lException )
			{
				throw new ModelIOException( "IO Exception while parsing input. " + lException.getMessage() );
			}
		}
		
		aMonitor.setTotal( numberOfElementNodes( lDocument.getDocumentElement() ));
		buildModel( lDocument.getDocumentElement() );
		return aSkipped;
    }
    
    /**
     * For usage by the progress monitor.
     * @param pNode The document node.
     * @return The number of elements nodes in this document.
     */
    private int numberOfElementNodes( Node pNode )
    {
    	int lReturn = 0;
    	if( isElementNode( pNode, XMLTags.Elements.MODEL.toString() ))
		{
    		NodeList lChildren = pNode.getChildNodes();
    		for( int lI = 0 ; lI < lChildren.getLength(); lI++ )
    		{
    			if( isElementNode( lChildren.item( lI ), XMLTags.Elements.CONCERN.toString() ))
    			{
    				NodeList lInnerChildren = lChildren.item( lI ).getChildNodes();
    				for( int lJ = 0; lJ < lInnerChildren.getLength(); lJ++ )
    				{
    				    if( isElementNode( lInnerChildren.item( lJ ), XMLTags.Elements.ELEMENT.toString() ))
    					{
    						lReturn++;
    					}
    				}
    			}
    		}
		}
    	return lReturn;
    }
    
    private void buildModel( Node pNode ) throws ModelIOException
	{	
		if( !isElementNode( pNode, XMLTags.Elements.MODEL.toString() ) )
		{
			throw new ModelIOException("Document node is not a <" + XMLTags.Elements.MODEL.toString() + "> node" );
		}
		
		NodeList lChildren = pNode.getChildNodes();
		for( int lI = 0 ; lI < lChildren.getLength(); lI++ )
		{
			if( isElementNode( lChildren.item( lI ), XMLTags.Elements.CONCERN.toString() ) )
			{
				buildConcern( lChildren.item( lI ) );
			}
			else
			{
			    throw new ModelIOException("Invalid node. Expecting <concern> node but got: " + lChildren.item( lI ).getNodeName() );
			}
		}
	}
    
    /**
     * @param pNode
     * @throws ModelIOException
     */
    private void buildConcern( Node pNode ) throws ModelIOException
	{
		String lConcern = pNode.getAttributes().getNamedItem( XMLTags.Attributes.NAME.toString() ).getNodeValue();
		aModel.newConcern( lConcern );
		
		Node lCommentNode = pNode.getAttributes().getNamedItem( XMLTags.Attributes.COMMENT.toString() );
		if( lCommentNode != null )
		{
			aModel.setConcernComment( lConcern, lCommentNode.getNodeValue() );
		}
		
		NodeList lChildren = pNode.getChildNodes();
				
		for( int lI = 0; lI < lChildren.getLength(); lI++ )
		{
		    if( isElementNode( lChildren.item( lI ), XMLTags.Elements.ELEMENT.toString() ))
			{
				Node lNode = lChildren.item( lI ).getAttributes().getNamedItem( XMLTags.Attributes.TYPE.toString() );
				if( lNode == null )
				{
					throw new ModelIOException( "Could not build concern model. Missing attribute " + 
							XMLTags.Attributes.TYPE.toString() + " in XML element type <" + lChildren.item( lI ).getNodeName()+">");
				}
				String lType = lNode.getNodeValue();
				lNode = lChildren.item( lI ).getAttributes().getNamedItem( XMLTags.Attributes.ID.toString() );
				if( lNode == null )
				{
					throw new ModelIOException( "Could not build concern model. Missing attribute " + 
							XMLTags.Attributes.ID.toString() + " in XML element type <" + lChildren.item( lI ).getNodeName()+">");
				}
				String lId = lNode.getNodeValue();
				lNode = lChildren.item( lI ).getAttributes().getNamedItem( XMLTags.Attributes.DEGREE.toString() );
				if( lNode == null )
				{
					throw new ModelIOException( "Could not build concern model. Missing attribute " + 
							XMLTags.Attributes.DEGREE.toString() + " in XML element type <" + lChildren.item( lI ).getNodeName()+">");
				}
				int lDegree = Integer.valueOf( lNode.getNodeValue() ).intValue();
				
				try
				{
				    Object lElement = null;
				    if( lType.equals( XMLTags.Values.FIELD.toString() ))
				    {
				        lElement = Converter.getInstance().toField( lId );
				    }
				    else if( lType.equals( XMLTags.Values.METHOD.toString() ))
				    {
				    	lElement = Converter.getInstance().toMethod( lId );
				    }
				    else
				    {
				        throw new ModelIOException( "Invalid element type: " + lType );
				    }
				    aModel.addElement( lConcern, lElement, lDegree);
				    lNode = lChildren.item( lI ).getAttributes().getNamedItem( XMLTags.Attributes.COMMENT.toString() );
					if( lNode != null )
					{
						aModel.setElementComment( lConcern, lElement, lNode.getNodeValue() );
					}
				    
				    aMonitor.worked( 1 );
				}
				catch( ConversionException lException )
				{
					aMonitor.worked( 1 );
					aSkipped++;
				}
			}
			else
			{
				throw new ModelIOException( "Could not build concern model. Invalid XML element type: <" + lChildren.item( lI ).getNodeName()+">");
			}
		}
	}
    
    private boolean isElementNode( Node pNode, String pElementName )
	{
		boolean lReturn = true;
		if( pNode.getNodeType() != Node.ELEMENT_NODE )
		{
			lReturn = false;
		}
		else
		{
			lReturn = pNode.getNodeName().equals( pElementName );
		}
		
		return lReturn;
	}
}
