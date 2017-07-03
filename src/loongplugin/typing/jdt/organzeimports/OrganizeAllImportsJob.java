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

package loongplugin.typing.jdt.organzeimports;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;

import loongplugin.LoongPlugin;
import loongplugin.color.coloredfile.ASTID;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.CompilationUnitColorManager;
import loongplugin.events.ASTColorChangedEvent;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModelNotFoundException;
import loongplugin.recommendation.typesystem.typing.jdt.organizeimports.ColoredSourceFileIteratorJob;
import loongplugin.source.database.JDTParserWrapper;
import loongplugin.typing.jdt.BindingProjectColorCache;
import loongplugin.typing.jdt.checks.resolutions.ASTBindingFinderHelper;

public class OrganizeAllImportsJob extends ColoredSourceFileIteratorJob {

	public OrganizeAllImportsJob(IProject[] project) {
		super(project, "Organizing imports", "Organizing");
	}

	protected void processSource(CLRAnnotatedSourceFile source)
			throws JavaModelException, CoreException {

		try {
			organizeImports(source, null);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sets the colors of all import statements. needs to parse the JDT ast and
	 * then assign colors to every bridged import statement
	 * 
	 * there are two ways to determine the colors of the type declarations.
	 * either there is a BandingProjectColorCache when the current typing
	 * context is known, or (if the cache is null) it is looked up manually,
	 * which is slow. consider this a long running process then.
	 * 
	 * @param source
	 * @param bindingProjectColorCache
	 *            cache or null if cache is not available
	 * @throws ParseException
	 * @throws CoreException
	 */
	public static void organizeImports(CLRAnnotatedSourceFile source,
			BindingProjectColorCache bindingProjectColorCache)
			throws ParseException, CoreException {
		CompilationUnitColorManager colorManager = (CompilationUnitColorManager) source.getColorManager();
		colorManager.beginBatch();
		Set<ASTNode> changedNodes = new HashSet<ASTNode>();
		try {

			CompilationUnit ast = JDTParserWrapper.parseJavaFile(source
					.getResource());

			for (Object astnode : ast.imports()) {
				if (astnode instanceof ImportDeclaration) {
					ImportDeclaration importDeclaration = (ImportDeclaration) astnode;
					IBinding importBinding = importDeclaration.resolveBinding();

					Set<Feature> importDeclColors = colorManager
							.getOwnColors(importDeclaration);
					Set<Feature> targetColors = new HashSet<Feature>();
					if (importBinding instanceof ITypeBinding) {
						targetColors = getTargetColors(
								bindingProjectColorCache,
								(ITypeBinding) importBinding);
					}

					if (targetColors.size() != importDeclColors.size()
							|| !targetColors.containsAll(importDeclColors)) {
						ASTNode c_importDeclaration = importDeclaration;
						for (Feature color : targetColors) {
							if (!importDeclColors.contains(color)) {
								colorManager.addColor(c_importDeclaration,
										color);
							}
						}
						for (Feature color : importDeclColors) {
							if (!targetColors.contains(color)) {
								colorManager.removeColor(c_importDeclaration,
										color);
							}
						}
						changedNodes.add(c_importDeclaration);
					}
				}

			}

		} finally {
			colorManager.endBatch();
			/**
			 * hack: informing only of changed root element when in fact only
			 * few imports have changed. doing this to avoid massive bridging.
			 */
			if (!changedNodes.isEmpty())
				LoongPlugin.getDefault().notifyListeners(
						new ASTColorChangedEvent(source, changedNodes, source));
		}
	}

	/**
	 * if the binding cache is available, this is simple, we can just return the
	 * values from the cache. otherwise we have to determine the target file,
	 * parse it, and look up the colors manually
	 * 
	 * source must not be null
	 */
	private static Set<Feature> getTargetColors(
			BindingProjectColorCache bindingProjectColorCache,
			ITypeBinding typeBinding) {
		if (bindingProjectColorCache != null)
			return bindingProjectColorCache.getColors(typeBinding);

		ASTNode typeDecl = ASTBindingFinderHelper.getTypeDecl(typeBinding);
		if (typeDecl == null)
			return Collections.EMPTY_SET;

		IFile file = (IFile) typeBinding.getJavaElement().getResource();
		CLRAnnotatedSourceFile typeSource = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile
				.getColoredJavaSourceFile(file);
		return typeSource.getColorManager().getColors(typeDecl);

	}
}
