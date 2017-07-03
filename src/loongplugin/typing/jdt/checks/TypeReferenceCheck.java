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

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.typing.model.IEvaluationStrategy;
import loongplugin.typing.jdt.JDTTypingProvider;

/**
 * checks colors between a field and references to it
 * 
 * @author ckaestne
 * 
 */
public class TypeReferenceCheck extends AbstractJDTTypingCheck {

	private final ITypeBinding target;

	public TypeReferenceCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			ITypeBinding target) {
		super(file, typingProvider, source);
		this.target = target;
	}

	public boolean evaluate(IEvaluationStrategy strategy) {
		return strategy.implies(file.getFeatureModel(), file.getColorManager()
				.getColors(source), typingProvider.getBindingColors()
				.getColors(target));
	}

	public String getErrorMessage() {
		return "Referencing type which is not present in some variants: "
				+ target.getName();
	}

	public String getProblemType() {
		return "loongplugin.typing.jdt.typereference";
	}

}
