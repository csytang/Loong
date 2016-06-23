/* ConcernMapper - A concern modelling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.25 $
 */

package loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model.io;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model.ConcernModel;


/**
 * Writes a concern model to disk.
 */
public class ModelWriter 
{
	// The model to write
	private ConcernModel aModel;
	
	/**
	 * Creates a new ModelWriter wrapping a concern model to write.
	 * @param pModel The model to write.
	 */
	public ModelWriter( ConcernModel pModel )
	{
		aModel = pModel;
	}
	
	/**
	 * Writes the concern model to a file.
	 * @param pFile The file to write the model to.
	 * @throws ModelIOException If there is any problem writing to the file.
	 */
	public void write( IFile pFile ) throws ModelIOException
	{
		/**
		 * Creates a file concurrently with other threads.
		 */
		class CreateFile extends Thread
    	{
    		private IFile aFile;
    		private PipedInputStream aInStream;
    		    		
    		/**
    		 * Creates a new file to write to.
    		 * @param pFile The file handle.
    		 * @param pInStream An input stream.
    		 */
    		public CreateFile( IFile pFile, PipedInputStream pInStream )
    		{
    			aFile = pFile;
    			aInStream = pInStream;
    			start();
    		}
    			
    		/**
    		 * @see java.lang.Runnable#run()
    		 */
    		public void run()
    		{
    			try
    			{
    			    if( aFile.exists() )
    			    {
    			        aFile.setContents( aInStream, true, false, null );
    			    }
    			    else
    			    {
    			        aFile.create( aInStream, true, null );
    			    }
    			}
    			catch( CoreException lException )
    			{
    				throw new RuntimeException( "Exception while creating a new file", lException );
    			}
    			finally
    			{
    			    try
    			    {
    			        aInStream.close();
    			    }
    			    catch( IOException lException )
    			    {
    			        throw new RuntimeException( "Exception while creating a new file", lException );
    			    }
    			}
    		}
    	}
        
        try
        {
        	PipedOutputStream lOutStream = new PipedOutputStream();
            PipedInputStream lInStream = new PipedInputStream( lOutStream );
            Thread lThread = new CreateFile( pFile, lInStream );
            Source lSource = new DOMSource( createDocument() );
     		Result lResult = new StreamResult( lOutStream );
            Transformer lTransformer = TransformerFactory.newInstance().newTransformer();
            lTransformer.transform( lSource, lResult );
            lOutStream.flush();
    	    lOutStream.close();
    	    try
    	    {
    	    	lThread.join();
    	    }
    	    catch( InterruptedException lException )
    	    {
    	    	// Proceed
    	    }
        }
        catch( TransformerConfigurationException lException )
        {
            throw new ModelIOException( lException.getMessage() );
        }
        catch( TransformerException lException )
        {
            throw new ModelIOException( lException.getMessage() );
        }
        catch( IOException lException )
        {
            throw new ModelIOException( lException.getMessage() );
        }
	}
	
	/**
     * Builds a DOM document representing all the concern models in this model.
     * @return a Document.  Never null.
     */
    private Document createDocument() throws ModelIOException
    {
        Document lDocument = null;
        try
        {
            DocumentBuilderFactory lFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder lBuilder = lFactory.newDocumentBuilder();
            lDocument = lBuilder.newDocument();
            Element lConcernModel = lDocument.createElement( XMLTags.Elements.MODEL.toString() );
           
            lDocument.appendChild( lConcernModel );
            
            String[] lConcerns = aModel.getConcernNames();
            
            for( int lI = 0; lI < lConcerns.length; lI++ )
            {
                lConcernModel.appendChild( createConcernNode( lDocument, lConcerns[lI] ) );
            }
        }
        catch( ParserConfigurationException lException )
        {
            throw new ModelIOException( lException.getMessage() );
        }
        return lDocument;
    }
    
    private Element createConcernNode( Document pDocument, String pConcern ) throws ModelIOException
    {
        Element lReturn = pDocument.createElement( XMLTags.Elements.CONCERN.toString() );
        lReturn.setAttribute( XMLTags.Attributes.NAME.toString(), pConcern );
        
        String lComment = aModel.getConcernComment( pConcern );
        if( lComment.length() > 0 )
        {
        	lReturn.setAttribute( XMLTags.Attributes.COMMENT.toString(), lComment );
        }
        
        for( Object lNext : aModel.getElements( pConcern ))
        {
        	Element lElement = pDocument.createElement( XMLTags.Elements.ELEMENT.toString() );
        	lReturn.appendChild( lElement );
        	if( lNext instanceof IMethod )
        	{
        		lElement.setAttribute( XMLTags.Attributes.TYPE.toString(), XMLTags.Values.METHOD.toString() );
        		try
        		{
        			lElement.setAttribute( XMLTags.Attributes.ID.toString(), Converter.getInstance().toIDString( (IMethod)lNext ));
        			lElement.setAttribute( XMLTags.Attributes.DEGREE.toString(), new Integer( aModel.getDegree( pConcern, lNext )).toString());
        			lComment = aModel.getElementComment( pConcern, lNext );
        			if( lComment.length() > 0 )
        			{
        				lElement.setAttribute( XMLTags.Attributes.COMMENT.toString(), lComment );
        			}
        		}
        		catch( ConversionException lException )
        		{
        			lReturn.removeChild( lElement );
        		}
        	}
        	else if( lNext instanceof IField )
        	{
        		lElement.setAttribute( XMLTags.Attributes.TYPE.toString(), XMLTags.Values.FIELD.toString() );
        		lElement.setAttribute( XMLTags.Attributes.ID.toString(), Converter.getInstance().toIDString( (IField)lNext ));
        		lElement.setAttribute( XMLTags.Attributes.DEGREE.toString(), new Integer( aModel.getDegree( pConcern, lNext )).toString());
        		lComment = aModel.getElementComment( pConcern, lNext );
    			if( lComment.length() > 0 )
    			{
    				lElement.setAttribute( XMLTags.Attributes.COMMENT.toString(), lComment );
    			}
        	}
        	else if( lNext != null )
        	{
        		throw new ModelIOException( "Unsupported type: " + lNext.getClass().getName());
        	}
        }
        return lReturn;
    }
}
