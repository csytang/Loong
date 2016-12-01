package loongplugin.source.database.model;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class SlicingVariable {
	
	private LElement lelement;
	
	// children 中的SlicingVariable 有当前的决定
	private Set<SlicingVariable> children = new HashSet<SlicingVariable>();
	
	// parent中的SlicingVariable 决定当前的节点
	private Set<SlicingVariable> parent = new HashSet<SlicingVariable>();
	
	// 将associate element 加入
	
	
	private IBinding ibinding = null;
	public SlicingVariable(LElement pelement){
		lelement = pelement;
		ASTNode node = pelement.getASTNode();
		if(node instanceof FieldDeclaration){
			FieldDeclaration nodefield = (FieldDeclaration)node;
			ibinding = nodefield.getType().resolveBinding();
		}else if(node instanceof VariableDeclarationStatement){
			VariableDeclarationStatement vardeclnode = (VariableDeclarationStatement)node;
			ibinding = vardeclnode.getType().resolveBinding();
		}else if(node instanceof VariableDeclarationFragment){
			VariableDeclarationFragment nodefrag = (VariableDeclarationFragment)node;
			IVariableBinding varbinding = nodefrag.resolveBinding();
			ibinding = varbinding.getType();
		}else if(node instanceof SingleVariableDeclaration){
			ibinding = ((SingleVariableDeclaration)node).getType().resolveBinding();
		}else{
			/*
			try {
				//throw new Exception("Unkown ASTNode:"+node);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		
	}
	
	public void addSlicingVariableChildren(SlicingVariable slicevariable){
		this.children.add(slicevariable);
	}
	public void addSlicingVariableChildren(Set<SlicingVariable> slicevariables){
		this.children.addAll(slicevariables);
	}
	
	public void addSlicingVariableParent(SlicingVariable slicevariable){
		this.parent.add(slicevariable);
	}
	
	public void addSlicingVariableParent(Set<SlicingVariable> slicevariables){
		this.parent.addAll(slicevariables);
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof SlicingVariable){
			SlicingVariable sliceobj = (SlicingVariable)obj;
			return sliceobj.getLElement().getId().equals(lelement.getId());
		}else{
			return false;
		}
		
	}

	protected LElement getLElement() {
		// TODO Auto-generated method stub
		return lelement;
	}

	public Set<SlicingVariable> getSlicingChildren() {
		// TODO Auto-generated method stub
		return children;
	}
	
	protected IBinding getBinding(){
		return ibinding;
	}
	
	
}
