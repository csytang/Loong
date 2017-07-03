package loongplugin.configuration;
import java.util.ArrayList;

public class TreeElement {

	ArrayList<TreeElement> children = new ArrayList<TreeElement>();
	
	TreeElement parent = null;
	
	public void addChild(TreeElement child) {
		children.add(child);
		child.setParent(this);
	}
	
	public void setParent(TreeElement parent) {
		this.parent = parent;
	}

	public TreeElement getParent() {
		return parent;
	}

	public void setChild(TreeElement child) {
		removeChildren();
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeElement child) {
		children.remove(child);
		child.setParent(null);
	}

	public void removeChildren() {
		for (TreeElement child : children)
			child.setParent(null);
		children.clear();
	}

	public TreeElement[] getChildren() {
		return (TreeElement[]) children.toArray(new TreeElement[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

}