/* JayFX - A Fact Extractor Plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~swevo/jayfx)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.8 $
 */

package loongplugin.source.database.model;

import java.util.Hashtable;

import loongplugin.color.coloredfile.ASTID;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;


public class LFlyweightElementFactory {
	private Hashtable<String, LElement> javaElements = null;

	// private static AFlyweightElementFactory factory = null;

	public LFlyweightElementFactory() {
		javaElements = new Hashtable<String, LElement>();
	}

	public LElement getElement(IBinding binding) {
		return (LElement) javaElements.get(binding.getKey());
	}

	public LElement getElement(String key) {
		return (LElement) javaElements.get(key);
	}

	public LElement getElement(ASTNode node) {
		String id = ASTID.calculateId(node);
		return (LElement) javaElements.get(id);
	}

	/**
	 * Returns a flyweight object representing a program element.
	 * 
	 * @param pCategory
	 *            The category of element. Must be a value declared in
	 *            ICategories.
	 * @param pId
	 *            The id for the element. For example, a field Id for
	 *            ICategories.FIELD.
	 * @see <a
	 *      href="http://java.sun.com/docs/books/jls/third_edition/html/binaryComp.html#13.1">
	 *      Java Specification, Third Section, 13.1 Section for the binary name
	 *      convention</a>
	 * @return A flyweight IElement.
	 * @exception AInternalProblemException
	 *                if an invalid category is passed as parameter.
	 */

	public LElement createLElement(LICategories pCategory, ASTNode node,IBinding binding,CLRAnnotatedSourceFile pColorSourceFile) {
		LElement lReturn = null;
		String nodeId = ASTID.calculateId(node);
		String bindingKey = null;
		
		if(binding!=null){
			bindingKey = binding.getKey();
			lReturn = javaElements.get(bindingKey);
			if(lReturn!=null){
				if (!lReturn.getCategory().equals(pCategory)) {
					lReturn.addSubcategory(pCategory);
				}
				return lReturn;
			}
		}
		
		lReturn = (LElement) javaElements.get(nodeId);
		if (lReturn != null) {
			if (!lReturn.getCategory().equals(pCategory)) {
				lReturn.addSubcategory(pCategory);
			}
			return lReturn;
		}
		lReturn = new LElement(nodeId,pCategory,pColorSourceFile,node);
		
		if (bindingKey != null)
			javaElements.put(bindingKey, lReturn);
		else
			javaElements.put(nodeId, lReturn);

		return lReturn;
	}

	
}
