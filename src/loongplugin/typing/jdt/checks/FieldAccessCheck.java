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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IVariableBinding;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.Feature;
import loongplugin.typing.model.IEvaluationStrategy;
import loongplugin.typing.model.ITypingMarkerResolution;
import loongplugin.typing.jdt.JDTTypingProvider;
import loongplugin.typing.jdt.checks.resolutions.ASTBindingFinderHelper;
import loongplugin.typing.jdt.checks.resolutions.AbstractJDTTypingCheckWithResolution;

/**
 * checks colors between a field and references to it
 * 
 * @author ckaestne
 * 
 */
public class FieldAccessCheck extends AbstractJDTTypingCheckWithResolution {

	private final IVariableBinding targetField;

	public FieldAccessCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			IVariableBinding target) {
		super(file, typingProvider, source);
		this.targetField = target;
	}

	public boolean evaluate(IEvaluationStrategy strategy) {
		return strategy.implies(file.getFeatureModel(), file.getColorManager()
				.getColors(source), typingProvider.getBindingColors()
				.getColors(targetField));
	}

	public String getErrorMessage() {
		return "Access to field which is not present in some variants: "
				+ targetField.getName();
	}

	public String getProblemType() {
		return "loongplugin.typing.jdt.fieldaccess";
	}

	@Override
	protected void addResolutions(
			ArrayList<ITypingMarkerResolution> resolutions,
			HashSet<Feature> colorDiff) {
		resolutions
				.addAll(createChangeNodeColorResolution(
						findCallingStatement(source), colorDiff, true,
						"statement", 20));
		resolutions.addAll(createChangeNodeColorResolution(
				findCallingMethod(source), colorDiff, true, "method", 18));
		resolutions.addAll(createChangeNodeColorResolution(
				findCallingType(source), colorDiff, true, "type", 16));

		// add resolution for target (field declaration)
		ASTNode fieldDecl = ASTBindingFinderHelper.getFieldDecl(targetField);
		if (fieldDecl != null)
			resolutions.addAll(createChangeNodeColorResolution(fieldDecl,
					colorDiff, false, "field declaration", 14));
	}

	@Override
	protected Set<Feature> getTargetColors() {
		return typingProvider.getBindingColors().getColors(targetField);
	}

}
