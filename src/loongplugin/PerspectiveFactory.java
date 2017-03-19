package loongplugin;


import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class PerspectiveFactory implements IPerspectiveFactory {
	
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		// TODO Auto-generated method stub
		
		// Get the editor area.
		String editorArea = layout.getEditorArea();
		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout topLeft = layout.createFolder("topLeft",IPageLayout.LEFT, 0.25f, editorArea);
		topLeft.addView(JavaUI.ID_PACKAGES);
		
		
		
		// Put the view on the bottom with task view
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.8f, editorArea);
		
		bottom.addView(IPageLayout.ID_TASK_LIST);	
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		
		
		// Bottom right: Task List view
		IFolderLayout right = layout.createFolder("right",IPageLayout.RIGHT, 0.75f, editorArea);
		right.addView(IPageLayout.ID_OUTLINE);
		
	}

}
