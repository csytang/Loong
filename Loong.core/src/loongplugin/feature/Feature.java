package loongplugin.feature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import loongplugin.source.database.model.LElement;
import loongplugin.views.featureview.FeatureView;


public class Feature implements PropertyConstants,Serializable,Comparable<Feature>{
	
	private static final long serialVersionUID = 1L;
	
	// The name of feature
	private String name;

	// Whether it is mandatory
	private boolean mandatory = false;
	
	private RGB rgb;
	
	private long id;
	// 
	private boolean concret = true;

	private boolean and = true;

	private boolean multiple = false;
	
	private boolean hidden = false;

	// feature model associated
	private FeatureModel featureModel;
	
	// all ASTNodes that belong to this feature
	private Map<ICompilationUnit,Set<ASTNode>> associatedASTNodes;
	
	private Set<LElement> associatedLElements;
	// feature initialize
	public Feature(FeatureModel featureModel) {
		this.featureModel = featureModel;
		name = "Unknown";
		sourceConnections.add(parentConnection);
		this.associatedASTNodes = new HashMap<ICompilationUnit,Set<ASTNode>>();
		this.associatedLElements = new HashSet<LElement>();
		
	}

	// feature initialize
	public Feature(FeatureModel featureModel, String name) {
		this.featureModel = featureModel;
		this.name = name;
		sourceConnections.add(parentConnection);
		this.associatedASTNodes = new HashMap<ICompilationUnit,Set<ASTNode>>();
		this.associatedLElements = new HashSet<LElement>();
		
	}

	public void setRGB(RGB color){
		this.rgb = color;
	}
	
	public RGB getRGB(){
		return this.rgb;
	}
	
	public boolean isAnd() {
		return and;
	}

	public boolean isOr() {
		return !and && multiple;
	}

	public boolean isAlternative() {
		return !and && !multiple;
	}

	public void changeToAnd() {
		and = true;
		multiple = false;
		fireChildrenChanged();
	}

	public void changeToOr() {
		and = false;
		multiple = true;
		fireChildrenChanged();
	}

	public void changeToAlternative() {
		and = false;
		multiple = false;
		fireChildrenChanged();
	}

	public void setAND(boolean and) {
		this.and = and;
		fireChildrenChanged();
	}

	public boolean isMandatorySet() {
		return mandatory;
	}

	// Whether it is a mandatory relation
	public boolean isMandatory() {
		return parent == null || !parent.isAnd() || mandatory;
	}
	
	// Set a mandatory relationship
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
		fireMandantoryChanged();
	}
	
	// Whether it is a hidden feature
	public boolean isHidden() {
		return hidden;
	}

	// Set this feature as a hidden feature
	public void setHidden(boolean hid) {
		this.hidden = hid;
	}
	
	
	public void setAbstract(Boolean value) {
		this.concret = !value;
		fireChildrenChanged();
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
		fireChildrenChanged();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		fireNameChanged();
	}

	/**
	 * Returns true if the rule can be writen in a format like 'Ab [Cd] Ef ::
	 * Gh'.
	 */
	public boolean hasInlineRule() {
		return getChildrenCount() > 1 && and && isMandatory() && !multiple;
	}

	private Feature parent;

	private LinkedList<Feature> children = new LinkedList<Feature>();

	public void setParent(Feature newParent) {
		if (newParent == parent)
			return;

		// delete old parent connection (if existing)
		if (parent != null) {
			parent.removeTargetConnection(parentConnection);
			parentConnection.setTarget(null);
		}

		// update the target
		parent = newParent;
		if (newParent != null) {
			parentConnection.setTarget(newParent);
			newParent.addTargetConnection(parentConnection);
		}
	}

	// Get the parent feature of current
	public Feature getParent() {
		return parent;
	}

	// Whether it is a root of feature model
	public boolean isRoot() {
		return parent == null;
	}

	// Get the children feature of current
	public LinkedList<Feature> getChildren() {
		return children;
	}
	
	// Set children of current feature
	public void setChildren(LinkedList<Feature> children) {
		if (this.children == children)
			return;
		for (Feature child : children) {
			child.setParent(this);
		}
		this.children = children;
		fireChildrenChanged();
	}

	// Whether it contains children
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	// Add child to children list
	public void addChild(Feature newChild) {
		children.add(newChild);
		newChild.setParent(this);
		fireChildrenChanged();
	}

	public void addChildAtPosition(int index, Feature newChild) {
		children.add(index, newChild);
		newChild.setParent(this);
		fireChildrenChanged();
	}

	// replace child with new one
	public void replaceChild(Feature oldChild, Feature newChild) {
		int index = children.indexOf(oldChild);
		children.set(index, newChild);
		oldChild.setParent(null);
		newChild.setParent(this);
		fireChildrenChanged();
	}

	// remove a child
	public void removeChild(Feature child) {
		children.remove(child);
		child.setParent(null);
		fireChildrenChanged();
	}

	// remove  the last child
	public Feature removeLastChild() {
		Feature child = children.removeLast();
		child.setParent(null);
		fireChildrenChanged();
		return child;
	}
	
	// 
	private FeatureConnection parentConnection = new FeatureConnection(this);

	private LinkedList<FeatureConnection> sourceConnections = new LinkedList<FeatureConnection>();

	private LinkedList<FeatureConnection> targetConnections = new LinkedList<FeatureConnection>();

	private static final LinkedList<FeatureConnection> EMPTY_LIST = new LinkedList<FeatureConnection>();

	public List<FeatureConnection> getSourceConnections() {
		return parent == null ? EMPTY_LIST : sourceConnections;
	}

	public List<FeatureConnection> getTargetConnections() {
		return targetConnections;
	}

	public void addTargetConnection(FeatureConnection connection) {
		targetConnections.add(connection);
	}

	public boolean removeTargetConnection(FeatureConnection connection) {
		return targetConnections.remove(connection);
	}

	private LinkedList<PropertyChangeListener> listenerList = new LinkedList<PropertyChangeListener>();

	public void addListener(PropertyChangeListener listener) {
		if (!listenerList.contains(listener))
			listenerList.add(listener);
	}

	public void removeListener(PropertyChangeListener listener) {
		listenerList.remove(listener);
	}

	

	private void fireNameChanged() {
		PropertyChangeEvent event = new PropertyChangeEvent(this, NAME_CHANGED,
				false, true);
		for (PropertyChangeListener listener : listenerList)
			listener.propertyChange(event);
	}

	private void fireChildrenChanged() {
		PropertyChangeEvent event = new PropertyChangeEvent(this,
				CHILDREN_CHANGED, false, true);
		for (PropertyChangeListener listener : listenerList)
			listener.propertyChange(event);
	}

	private void fireMandantoryChanged() {
		PropertyChangeEvent event = new PropertyChangeEvent(this,
				MANDANTORY_CHANGED, false, true);
		for (PropertyChangeListener listener : listenerList)
			listener.propertyChange(event);
	}

	

	public boolean isAncestorOf(Feature next) {
		while (next.getParent() != null) {
			if (next.getParent() == this)
				return true;
			next = next.getParent();
		}
		return false;
	}

	public boolean isFirstChild(Feature child) {
		return children.indexOf(child) == 0;
	}

	public int getChildrenCount() {
		return children.size();
	}

	public Feature getFirstChild() {
		if (children.isEmpty())
			return null;
		return children.get(0);
	}

	public Feature getLastChild() {
		if (!children.isEmpty()) {
			return children.getLast();
		}
		return null;
	}

	public int getChildIndex(Feature feature) {
		return children.indexOf(feature);
	}

	public boolean isAbstract() {
		return (!this.concret);
	}

	public boolean isConcrete() {
		return this.concret;
	}

	public boolean isLayer() {
		return !isAbstract();
	}

	public boolean canHaveChildren() {
		return !featureModel.hasAbstractFeatures() || hasChildren();
	}
	
	public boolean isANDPossible() {
		if (parent == null || parent.isAnd())
			return false;
		for (Feature child : children) {
			if (child.isAnd())
				return false;
		}
		return true;
	}

	/**
	 * used externally to fire events, eg for graphical changes not anticipated
	 * in the core implementation
	 * 
	 * @param event
	 */
	public void fire(PropertyChangeEvent event) {
		for (PropertyChangeListener listener : listenerList)
			listener.propertyChange(event);
	}

	public Feature clone() {
		Feature feature = new Feature(featureModel,name);
		for (Feature child : children) {
			feature.addChild(child.clone());
		}
		feature.and = and;
		feature.mandatory = mandatory;
		feature.multiple = multiple;
		feature.hidden = hidden;
		feature.concret = concret;
		return feature;
	}
	
	
	
	public void setAnd() {
		this.and = true;
	}
	
	public void setOr() {
		this.and = false;
		this.multiple = true;
	}
	
	public void setAlternative() {
		this.and = false;
		this.multiple = false;
	}

	@Override
	public String toString() {
		return name;
	}
	public long getId(){
		return id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeLong(id);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		id = in.readLong();
	}

	private Object readResolve() throws ObjectStreamException, PartInitException {
		
		for (Feature feature : FeatureModelManager.getInstance().getFeatures()) {
				if (feature.id == id)
					return feature;
		}
		throw new InvalidObjectException("Feature with ID " + id
				+ " is not known.");
	}
	
	public void addASTNodeToFeature(ICompilationUnit unit,ASTNode node){
		if(!this.associatedASTNodes.containsKey(unit)){
			Set<ASTNode>nodes = new HashSet<ASTNode>();
			nodes.add(node);
			this.associatedASTNodes.put(unit, nodes);
		}else{
			this.associatedASTNodes.get(unit).add(node);
		}
	}
	
	public Map<ICompilationUnit,Set<ASTNode>> getASTNodeBelongs(){
		if(this.associatedASTNodes==null){
			return new HashMap<ICompilationUnit,Set<ASTNode>>();
		}
		return this.associatedASTNodes;
	}
	
	
	public static IProject getCurrentProject(){    
        ISelectionService selectionService =     
            Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    

        ISelection selection = selectionService.getSelection();    

        IProject project = null;    
        if(selection instanceof IStructuredSelection) {    
            Object element = ((IStructuredSelection)selection).getFirstElement();    

            if (element instanceof IResource) {    
                project= ((IResource)element).getProject();    
            } else if (element instanceof PackageFragmentRootContainer) {    
                IJavaProject jProject =     
                    ((PackageFragmentRootContainer)element).getJavaProject();    
                project = jProject.getProject();    
            } else if (element instanceof IJavaElement) {    
                IJavaProject jProject= ((IJavaElement)element).getJavaProject();    
                project = jProject.getProject();    
            }    
        }     
        return project;    
    }
	public int compareTo(Feature o) {
		if (this.id<o.id) return -1;
		if (this.id>o.id) return 1;
		return 0;
	}

	public void removeASTNodeToFeature(ICompilationUnit unit,ASTNode node) {
		// TODO Auto-generated method stub
		if(this.associatedASTNodes.containsKey(unit)){
			if(this.associatedASTNodes.get(unit).contains(node))
				this.associatedASTNodes.get(unit).remove(node);
		}
	}
	
	public void addLElementToFeature(LElement element){
		this.associatedLElements.add(element);
	}

	public Set<LElement> getLElementBelongs() {
		// TODO Auto-generated method stub
		return this.associatedLElements;
	}

	
	
	
}
