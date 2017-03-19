package loongplugin.editor.projection;

import java.util.Iterator;

import org.eclipse.jdt.internal.ui.text.java.hover.SourceViewerInformationControl;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHoverExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.jface.text.source.projection.IProjectionPosition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;


/**
 * Annotation hover for projection annotations.
 *
 * @since 3.0
 */
public class CLRProjectionAnnotationHover implements IAnnotationHover, IAnnotationHoverExtension{
/*
 * (non-Javadoc)
 * @see org.eclipse.jface.text.source.IAnnotationHoverExtension#getHoverControlCreator()
 * Hover support is provided in the platform text framework, 
 * allowing you to implement informational hovers (or infopops) over the text and
 * the rulers shown in your editor.
 */
	private IInformationControlCreator fInformationControlCreator;
	
	@Override
	public IInformationControlCreator getHoverControlCreator() {
		// TODO Auto-generated method stub
		if (fInformationControlCreator != null)
			return fInformationControlCreator;

		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new SourceViewerInformationControl(parent, true,SWT.NONE,JFaceResources.TEXT_FONT);
			}
		};
	}

	@Override
	public boolean canHandleMouseCursor() {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * Returns the information which should be presented when a hover popup 
	 * is shown for the specified hover region. 
	 * The hover region has the same semantics as the region returned by getHoverRegion. 
	 * If the returned information is null or empty no hover popup will be shown.
	 * 当指针hover时候弹出提示信息 对于特点的hover区域。
	 * 如果返回的信息是空或者null 那么没有hover popup会被返回
	 * 
	 */
	@Override
	public Object getHoverInfo(ISourceViewer sourceViewer, ILineRange lineRange, int visibleNumberOfLines) {
		// TODO Auto-generated method stub
		return getProjectionTextAtLine(sourceViewer, lineRange.getStartLine(), visibleNumberOfLines);
	}
	private String getProjectionTextAtLine(ISourceViewer viewer, int line, int numberOfLines) {
		/*
		 * 1. viewer 当前加入的 sourceviewer
		 * 2. startline 开始行
		 * 3. 可见行数  lineRange(startline,startline+numberOfLines); 
		 * 
		 * 
		 */
		IAnnotationModel model= null;
		if (viewer instanceof ISourceViewerExtension2) {
			ISourceViewerExtension2 viewerExtension= (ISourceViewerExtension2) viewer;
			IAnnotationModel visual= viewerExtension.getVisualAnnotationModel();
			if (visual instanceof IAnnotationModelExtension) {
				IAnnotationModelExtension modelExtension= (IAnnotationModelExtension) visual;
				model= modelExtension.getAnnotationModel(CLRProjectionSupport.INLINEPROJECTION);
			}
		}

		if (model != null) {
			try {
				IDocument document= viewer.getDocument();
				Iterator e= model.getAnnotationIterator();
				while (e.hasNext()) {
					CLRProjectionAnnotation annotation= (CLRProjectionAnnotation) e.next();
					if (!annotation.isCollapsed())
						continue;

					Position position= model.getPosition(annotation);
					if (position == null)
						continue;

					if (isCaptionLine(annotation, position, document, line))
						return getText(document, position.getOffset(), position.getLength(), numberOfLines);

				}
			} catch (BadLocationException x) {
			}
		}
		
		
		
		return null;
	}
	
	
	/*
	 * @since 3.1
	 */
	private boolean isCaptionLine(CLRProjectionAnnotation annotation, Position position, IDocument document, int line) {
		if (position.getOffset() > -1 && position.getLength() > -1) {
			try {
				int captionOffset;
				if (position instanceof IProjectionPosition)
					captionOffset= ((IProjectionPosition) position).computeCaptionOffset(document);
				else
					captionOffset= 0;
				int startLine= document.getLineOfOffset(position.getOffset() + captionOffset);
				return line == startLine;
			} catch (BadLocationException x) {
			}
		}
		return false;
	}
	
	
	
	@Override
	public ILineRange getHoverLineRange(ISourceViewer viewer, int lineNumber) {
		// TODO Auto-generated method stub
		return new LineRange(lineNumber, 1);
	}

	@Override
	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
		// TODO Auto-generated method stub
		return "郭德纲好";
	}

	public void setHoverControlCreator(IInformationControlCreator creator) {
		// TODO Auto-generated method stub
		fInformationControlCreator= creator;
	}

	private String getText(IDocument document, int offset, int length, int numberOfLines) throws BadLocationException {
		int endOffset= offset + length;

		try {
			int endLine= document.getLineOfOffset(offset) + Math.max(0, numberOfLines -1);
			IRegion lineInfo= document.getLineInformation(endLine);
			endOffset= Math.min(endOffset, lineInfo.getOffset() + lineInfo.getLength());
		} catch (BadLocationException x) {
		}

		return document.get(offset, endOffset - offset);
	}
	
}
