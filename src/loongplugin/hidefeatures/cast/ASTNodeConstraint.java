package loongplugin.hidefeatures.cast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import loongplugin.recommendation.RecommendationContext;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LRelation;

public class ASTNodeConstraint {
	/**
	 * 
	 * @param node
	 */
	private ASTNode current;
	
	// a set of AST nodes as the constraint of current node.
	private Set<ASTNode> constraints = new HashSet<ASTNode>();
	
	private LElement element;
	
	private Set<LRelation> validTransponseRelations;
	private LFlyweightElementFactory elementfactory;
	
	protected ApplicationObserver AOB;
	
	public ASTNodeConstraint(ASTNode node,LElement pelement,LFlyweightElementFactory pelementfactory,ApplicationObserver pOB){
		this.current = node;
		this.element = pelement;
		this.elementfactory = pelementfactory;
		this.AOB = pOB;
		
		validTransponseRelations = new HashSet<LRelation>();
		validTransponseRelations.add(LRelation.T_ACCESS);
		validTransponseRelations.add(LRelation.T_REFERENCES);
		validTransponseRelations.add(LRelation.T_ACCESS_TYPE);
		validTransponseRelations.add(LRelation.T_ACCESS_FIELD);
		validTransponseRelations.add(LRelation.T_ACCESS_LOCAL_VARIABLE);
		validTransponseRelations.add(LRelation.T_ACCESS_METHOD);

		// check the data constraint
		Set<LElement> dfconstraints = checkDataflowConstraints();

		// name binding constraint
		LElement namebindconstraints = checkBindingConstraints();
		
	    if(namebindconstraints!=null){
	    	this.constraints.add(namebindconstraints.getASTNode());
	    }
		
	    for(LElement constraint:dfconstraints){
	    	this.constraints.add(constraint.getASTNode());
	    }
		
	}
	
	public Set<ASTNode> getConstraints(){
		return constraints;
	}


	private Set<LElement> checkDataflowConstraints() {
		Set<LElement> validelements = new HashSet<LElement>();
		for(LRelation relation:validTransponseRelations){
			Set<LElement> backwardElements = AOB.getRange(element,relation);
			validelements.addAll(backwardElements);
		}
		return validelements;
	}
	

	private LElement checkBindingConstraints() {
		/**
		 * IAnnotationBinding, IMemberValuePairBinding, IMethodBinding, IPackageBinding, ITypeBinding, IVariableBinding
		 */
		IBinding ibinding = null;
		if(this.current instanceof TypeDeclaration){
			TypeDeclaration typedecl = (TypeDeclaration)this.current;
			ibinding = typedecl.resolveBinding();
		}else if(this.current instanceof SimpleName){
			SimpleName typeName = (SimpleName)this.current;
			ibinding = typeName.resolveTypeBinding();
		}else if(this.current instanceof CompilationUnit){
			CompilationUnit typecompunit = (CompilationUnit)this.current;
			AbstractTypeDeclaration typeunit = (AbstractTypeDeclaration) typecompunit.types().get(0);
			ibinding = typeunit.resolveBinding();
		}else if(this.current instanceof MethodDeclaration){
			MethodDeclaration methoddecl = (MethodDeclaration)this.current;
			ibinding = methoddecl.resolveBinding();
		}else if(this.current instanceof ArrayAccess){
			ArrayAccess arrayaccessnode = (ArrayAccess)this.current;
			ibinding = arrayaccessnode.resolveTypeBinding();
		}else if(this.current instanceof ArrayCreation){
			ArrayCreation arraycreation = (ArrayCreation)this.current;
			ibinding = arraycreation.resolveTypeBinding();
		}else if(this.current instanceof ArrayInitializer){
			ArrayInitializer arrayinit = (ArrayInitializer)this.current;
			ibinding = arrayinit.resolveTypeBinding();
		}else if(this.current instanceof FieldAccess){
			FieldAccess fieldaccess = (FieldAccess)this.current;
			ibinding = fieldaccess.resolveFieldBinding();
		}else if(this.current instanceof MethodInvocation){
			MethodInvocation methodinvok = (MethodInvocation)this.current;
			ibinding = methodinvok.resolveMethodBinding();
		}else if(this.current instanceof SimpleType){
			SimpleType type = (SimpleType)this.current;
			ibinding = type.resolveBinding();
		}
		
		if(ibinding!=null){
			return this.elementfactory.getElement(ibinding);
		}else{
			return null;
		}
	
	}
	
}
