/* ConcernMapper - A concern modeling plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~martin/cm)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.4 $
 */

package loongplugin.recommendation.topology.ca.mcgill.cs.serg.cm.model.io;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;

/**
 * Behavior to convert Java elements to unique strings, and back again.
 */
public interface IConverter 
{
	/**
	 * Returns the field in the Java model associated with the parameter
	 * string.
	 * @param pField A string ID representing the field to fetch.
	 * @return A Field object corresponding to pField.
	 * @throws ConversionException If the conversion cannot be made properly.
	 */
	IField toField(String pField) throws ConversionException;

	/**
	 * Returns the method in the Java model associated with the parameter string.
	 * @param pMethod A string ID representing the method to fetch.
	 * @return A Method object corresponding to pMethod
	 * @throws ConversionException If the conversion cannot be made properly.
	 */
	IMethod toMethod(String pMethod) throws ConversionException;

	/**
	 * Converts pField into a unique descriptor.
	 * @param pField The field to convert
	 * @return A String representing the field.
	 */
	String toIDString(IField pField);

	/**
	 * Converts pMethod into a unique descriptor.
	 * @param pMethod The method to convert.
	 * @return A String representing the method.
	 * @throws ConversionException If we cannot convert the method properly.
	 */
	String toIDString(IMethod pMethod )	throws ConversionException;
}
