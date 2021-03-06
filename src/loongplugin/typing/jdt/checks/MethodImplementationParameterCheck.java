/**
    Copyright 2010 Christian K�stner

    This file is part of CIDE.

    CIDE is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License.

    CIDE is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CIDE.  If not, see <http://www.gnu.org/licenses/>.

    See http://www.fosd.de/cide/ for further information.
*/

package loongplugin.typing.jdt.checks;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.typing.model.IEvaluationStrategy;
import loongplugin.typing.jdt.JDTTypingProvider;
import loongplugin.typing.jdt.checks.util.MethodPathItem;


/**
 * checks colors of parameters between method declaration /
 * implementation and related or rather inherited (abstract) method declarations
 * in interfaces and super classes. as necessary, throws according to the strategy an
 * error that method is not implemented in some variants. 
 * 
 * @author adreilin
 * 
 */
public class MethodImplementationParameterCheck extends AbstractJDTTypingCheck {

	private final int paramIndex;
	private final List<MethodPathItem> inherMethods;
	private final String name;

	public MethodImplementationParameterCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			IMethodBinding methodBinding, int paramIndex, List<MethodPathItem> inherMethods) {
		super(file, typingProvider, source);
		this.paramIndex = paramIndex;
		this.inherMethods = inherMethods;
		this.name = methodBinding.getName();
	}

	public boolean evaluate(IEvaluationStrategy strategy) {

		// checks "AND" condition for all found methods
		for (MethodPathItem tmpItem : inherMethods) {
			
			if (!tmpItem.isDeclaringClassAbstract())
				return true;

			if (strategy.equal(file.getFeatureModel(), typingProvider
					.getBindingColors().getColors(tmpItem.getInheritedParamKeys().get(paramIndex)),
									file.getColorManager().getColors(source))) 
				continue;
							

			// we have found one overridden method for which "target -> source"
			// is false

			// checks if current item is abstract
			if (tmpItem.isAbstract())
				// check failed
				return false;
			else
				// another method implementation exists
				return true;

		}

		return true;

	}

	public String getErrorMessage() {
		return "Declaring method " + name + "does not implement inherited abstract methods in some variants. "
				+ "Check param list.";
	}

	public String getProblemType() {
		return "loongplugin.typing.jdt.methodimplementationparameter";
	}

}
