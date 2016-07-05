package loongplugin.views.recommendedfeatureview;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;


public class ASTStringTracker extends ASTVisitor {
	
	private List<String> recommendfeatureNames = new LinkedList<String>();
	private List<String> recommendnonfeatureNames = new LinkedList<String>();

	
	public ASTStringTracker(ASTNode node){
		node.accept(this);
	}
	public List<String> getRecommendedFeatureNameList(){
		return recommendfeatureNames;
	}
	public List<String> getRecommendedNonFeatureNameList(){
		return recommendnonfeatureNames;
	}
	
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(Block node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(BlockComment node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(BooleanLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(BreakStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(CatchClause node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(CharacterLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(CompilationUnit node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ConditionalExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ContinueStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(CreationReference node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(Dimension node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(DoStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(EmptyStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(EnhancedForStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ForStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(IfStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(InfixExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(Initializer node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(IntersectionType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(LabeledStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(LambdaExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(MarkerAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(MemberRef node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(MemberValuePair node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(Modifier node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(NormalAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(NullLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(NumberLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ParameterizedType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ParenthesizedExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(PostfixExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(PrefixExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(PrimitiveType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(QualifiedName node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(QualifiedType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SimpleName node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SimpleType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SingleMemberAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SwitchCase node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SwitchStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(SynchronizedStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(TagElement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(TextElement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ThisExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ThrowStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(TryStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(TypeLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(TypeParameter node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(UnionType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(WhileStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(WildcardType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	@Override
	public boolean visit(ArrayAccess node) {
		// TODO Auto-generated method stub
		Expression expression = node.getArray();
		expression.accept(new ASTVisitor(){
			@Override
			public boolean visit(SimpleName node) {
				// TODO Auto-generated method stub
				recommendfeatureNames.add(node.toString());
				return super.visit(node);
			}
			
		});
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayCreation node) {
		// TODO Auto-generated method stub
		ArrayType type = node.getType();
		Type elementtype = type.getElementType();
		if(!elementtype.isSimpleType()){
			recommendnonfeatureNames.add(elementtype.toString());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		// TODO Auto-generated method stub
		List<Expression> expressions = node.expressions();
		for(Expression exp:expressions){
			recommendnonfeatureNames.add(exp.toString());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayType node) {
		// TODO Auto-generated method stub
		Type elementtype = node.getElementType();
		if(!elementtype.isSimpleType()){
			recommendnonfeatureNames.add(elementtype.toString());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(AssertStatement node) {
		// TODO Auto-generated method stub
		Expression expression = node.getExpression();
		recommendnonfeatureNames.add(expression.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(Assignment node) {
		// TODO Auto-generated method stub
		Expression leftSide = node.getLeftHandSide();
		Expression rightSide = node.getRightHandSide();
		recommendnonfeatureNames.add(leftSide.toString());
		recommendnonfeatureNames.add(rightSide.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(CastExpression node) {
		// TODO Auto-generated method stub
		Expression exp  = node.getExpression();
		recommendnonfeatureNames.add(exp.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		// TODO Auto-generated method stub
		Type nodetype = node.getType();
		recommendfeatureNames.add(nodetype.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		// TODO Auto-generated method stub
		List<Expression> arguments = node.arguments();
		IMethodBinding constructorbinding = node.resolveConstructorBinding();
		recommendfeatureNames.add(constructorbinding.getName());
		for(Expression argument:arguments){
			recommendnonfeatureNames.add(argument.toString());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		// TODO Auto-generated method stub
		SimpleName enumName = node.getName();
		recommendfeatureNames.add(enumName.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		// TODO Auto-generated method stub
		// could be feature name, use the type declaration
		/*
		 * EnumDeclaration:
     		[ Javadoc ] { ExtendedModifier } enum Identifier
         	[ implements Type { , Type } ]
         	{
         		[ EnumConstantDeclaration { , EnumConstantDeclaration } ] [ , ]
         		[ ; { ClassBodyDeclaration | ; } ]
         	}
		 */
		SimpleName nodeName = node.getName();
		recommendfeatureNames.add(nodeName.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(ExpressionMethodReference node) {
		// TODO Auto-generated method stub
		SimpleName methodName = node.getName();
		recommendfeatureNames.add(methodName.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		// TODO Auto-generated method stub
		String expStatement = node.toString();
		recommendnonfeatureNames.add(expStatement);
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldAccess node) {
		// TODO Auto-generated method stub
		SimpleName field = node.getName();
		recommendfeatureNames.add(field.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		// TODO Auto-generated method stub
		List<VariableDeclarationFragment>fields = node.fragments();
		for(VariableDeclarationFragment fieldfrag:fields){
			recommendfeatureNames.add(fieldfrag.getName().toString());
		}
		return super.visit(node);
	}

	

	@Override
	public boolean visit(ImportDeclaration node) {
		// TODO Auto-generated method stub
		Name importedName = node.getName();
		recommendnonfeatureNames.add(importedName.getFullyQualifiedName());
		return super.visit(node);
	}
	

	@Override
	public boolean visit(InstanceofExpression node) {
		// TODO Auto-generated method stub
		Expression leftOpenand = node.getLeftOperand();
		Type rightOpenand = node.getRightOperand();
		recommendnonfeatureNames.add(leftOpenand.toString());
		recommendfeatureNames.add(rightOpenand.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(Javadoc node) {
		// TODO Auto-generated method stub
		recommendnonfeatureNames.add(node.toString());
		return super.visit(node);
	}


	@Override
	public boolean visit(LineComment node) {
		// TODO Auto-generated method stub
		recommendnonfeatureNames.add(node.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodRef node) {
		// TODO Auto-generated method stub
		SimpleName methodName = node.getName();
		recommendfeatureNames.add(methodName.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodRefParameter node) {
		// TODO Auto-generated method stub
		SimpleName name = node.getName();
		Type type = node.getType();
		recommendfeatureNames.add(name.toString());
		recommendnonfeatureNames.add(type.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		// TODO Auto-generated method stub
		SimpleName methodName = node.getName();
		recommendfeatureNames.add(methodName.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		// TODO Auto-generated method stub
		SimpleName methodName = node.getName();
		List<Expression> arguments = node.arguments();
		recommendfeatureNames.add(methodName.toString());
		for(Expression exp:arguments){
			recommendnonfeatureNames.add(exp.toString());
		}
		
		return super.visit(node);
	}


	@Override
	public boolean visit(NameQualifiedType node) {
		// TODO Auto-generated method stub
		SimpleName name = node.getName();
		recommendnonfeatureNames.add(name.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		// TODO Auto-generated method stub
		Name packageName = node.getName();
		recommendfeatureNames.add(packageName.getFullyQualifiedName());
		return super.visit(node);
	}


	@Override
	public boolean visit(ReturnStatement node) {
		// TODO Auto-generated method stub
		Expression returnexp = node.getExpression();
		recommendnonfeatureNames.add(returnexp.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		// TODO Auto-generated method stub]
		SimpleName nodeName = node.getName();
		Type nodeType = node.getType();
		recommendnonfeatureNames.add(nodeType.toString());
		recommendfeatureNames.add(nodeName.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(StringLiteral node) {
		// TODO Auto-generated method stub
		String value = node.getLiteralValue();
		recommendnonfeatureNames.add(value);
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		// TODO Auto-generated method stub
		String supermethodName = node.resolveConstructorBinding().getName();
		recommendfeatureNames.add(supermethodName);
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		// TODO Auto-generated method stub
		SimpleName fieldName = node.getName();
		recommendfeatureNames.add(fieldName.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		// TODO Auto-generated method stub
		SimpleName methodName = node.getName();
		recommendfeatureNames.add(methodName.toString());
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		// TODO Auto-generated method stub
		SimpleName supermethodName = node.getName();
		recommendfeatureNames.add(supermethodName.toString());
		return super.visit(node);
	}



	@Override
	public boolean visit(TypeDeclaration node) {
		// TODO Auto-generated method stub
		for(TypeDeclaration type:node.getTypes()){
			recommendnonfeatureNames.add(type.getName().toString());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		// TODO Auto-generated method stub
		TypeDeclaration typeDeclaration = node.getTypeDeclaration();
		for(TypeDeclaration type:typeDeclaration.getTypes()){
			recommendnonfeatureNames.add(type.getName().toString());
		}
		
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeMethodReference node) {
		// TODO Auto-generated method stub
		SimpleName name = node.getName();
		Type type = node.getType();
		recommendfeatureNames.add(name.toString());
		recommendnonfeatureNames.add(type.toString());
		return super.visit(node);
	}


	@Override
	public boolean visit(VariableDeclarationExpression node) {
		// TODO Auto-generated method stub
		List<VariableDeclarationFragment>fragements = node.fragments();
		for(VariableDeclarationFragment frag:fragements){
			SimpleName name = frag.getName();
			recommendfeatureNames.add(name.toString());
		}
		return super.visit(node);
	}


	@Override
	public boolean visit(VariableDeclarationFragment node) {
		// TODO Auto-generated method stub
		SimpleName name = node.getName();
		recommendfeatureNames.add(name.toString());
		return super.visit(node);
	}

	
}
