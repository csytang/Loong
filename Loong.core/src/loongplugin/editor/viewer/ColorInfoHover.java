package loongplugin.editor.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.hover.IJavaEditorTextHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import loongplugin.color.ColorManager;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.CompilationUnitColorManager;
import loongplugin.color.coloredfile.IColorManager;
import loongplugin.color.coloredfile.IColoredJavaSourceFile;
import loongplugin.editor.CLREditor;
import loongplugin.editor.SelectionFinder;
import loongplugin.feature.Feature;
import loongplugin.utils.EditorUtility;



public class ColorInfoHover extends ColoredTextHover implements ITextHover,ITextHoverExtension2 {

	private IColoredJavaSourceFile sourceFile;
	private CompilationUnitColorManager colorManager;
	
	
	public ColorInfoHover(CLRAnnotatedSourceFile psourcefile){
		super(psourcefile);
		//NL = "<BR>";
	}
	
	public void setEditor(IEditorPart editor) {
		if (editor instanceof CLREditor) {
			setColoredSourceFile(((CLREditor) editor)
					.getCLRAnnotatedFile());
		} else {
			setColoredSourceFile(null);
		}
	}

	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		
		String info = super.getHoverInfo(textViewer, hoverRegion);
		if (info == NOT_COLORED)
			return null;
		return info;
		
		
	}

	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		// TODO Auto-generated method stub
		String info = super.getHoverInfo(textViewer, hoverRegion);
		if (info == NOT_COLORED)
			return null;
		return info;
	}


}
