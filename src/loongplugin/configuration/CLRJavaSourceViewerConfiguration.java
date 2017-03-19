package loongplugin.configuration;

import loongplugin.editor.CLREditor;
import loongplugin.editor.viewer.ColorInfoHover;
import loongplugin.editor.viewer.ColoredTextHover;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;

public class CLRJavaSourceViewerConfiguration extends JavaSourceViewerConfiguration{
	
	private CLREditor editor = null;
	public CLRJavaSourceViewerConfiguration(IColorManager colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
		// TODO Auto-generated constructor stub
		if(editor instanceof CLREditor)
			this.editor = (CLREditor) editor;
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType, int stateMask) {
		// TODO Auto-generated method stub
		
		if(editor!=null){
			return new ColorInfoHover(editor.getCLRAnnotatedFile());
		}else
			return super.getTextHover(sourceViewer, contentType, stateMask);
		
		
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType) {
		// TODO Auto-generated method stub
		if(editor!=null){
			return new ColorInfoHover(editor.getCLRAnnotatedFile());
		}else
			return super.getTextHover(sourceViewer, contentType);
	}

	@Override
	public IInformationControlCreator getInformationControlCreator(
			ISourceViewer sourceViewer) {
		// TODO Auto-generated method stub
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, false);
			}
		};
	}

	
}
