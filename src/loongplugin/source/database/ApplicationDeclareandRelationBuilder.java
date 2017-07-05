package loongplugin.source.database;

import java.util.Set;
import java.util.Stack;

import loongplugin.color.coloredfile.ASTID;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.SourceFileColorManager;
import loongplugin.feature.Feature;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LElementColorManager;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LICategories;
import loongplugin.source.database.model.LRelation;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


public class ApplicationDeclareandRelationBuilder {
	
	private CLRAnnotatedSourceFile aannotatedsourcefile;
	private ICompilationUnit alCU;
	private LFlyweightElementFactory aelementfactory;
	private ProgramDatabase aDB;
	private SourceFileColorManager acolorManager;
	private LElement curCUElement;
	private LElement curType;
	private LElement curMethod;
	private Stack<LElement>curTypeReminder;
	private int curParamIndex;
	private LElementColorManager aelementcolormanager;
	
	public ApplicationDeclareandRelationBuilder(ProgramDatabase pDB,ICompilationUnit lCU,
			CLRAnnotatedSourceFile pannotatedsourcefile,LFlyweightElementFactory pelementfactory, LElementColorManager pelementColorManager){
		aDB = pDB;
		alCU = lCU;
		aannotatedsourcefile = pannotatedsourcefile;
		aelementfactory = pelementfactory;
		acolorManager = (SourceFileColorManager) aannotatedsourcefile.getColorManager();
		curTypeReminder = new Stack<LElement>();
		aelementcolormanager = pelementColorManager;
		curParamIndex = -1;
	}
	
	

	public void createElementsAndDeclareRelations() {
		// TODO Auto-generated method stub
		CompilationUnit ast = JDTParserWrapper.parseCompilationUnit(alCU);
		//System.out.println("-----||||----Processing Unit------||||-----:"+alCU.getElementName());
		traverseAST(ast);
		//System.out.println("||||---------Finish Processing Unit-----------||||:"+alCU.getElementName());
	}
	/**
	 * String pId,LICategories pcategory,
			CLRAnnotatedSourceFile pColorSourceFile,ASTNode pastNode,String pdisplayName
			
	 * @param node
	 */
	void traverseAST(ASTNode node) {

		node.accept(new ASTVisitor() {

			@Override
			public boolean visit(CompilationUnit node) {
				curCUElement = (LElement) aelementfactory.createLElement(LICategories.COMPILATION_UNIT,node,null,aannotatedsourcefile);
				addElement(curCUElement,getColor(node));
				return super.visit(node);
			}

			private void addElement(LElement element,Set<Feature> colors) {
				aDB.addElement(element);
				
				for (Feature color : colors)
						aelementcolormanager.addElementToColor(color, element);
				
			}
			
			private Set<Feature> getColor(ASTNode node) {
				
				return acolorManager.getColors(node);
				
				
			}

			@Override
			public boolean visit(ImportDeclaration node) {
				LElement curImport = (LElement) aelementfactory.createLElement(
						LICategories.IMPORT, node,null, aannotatedsourcefile);
				addElement(curImport,getColor(node));

				aDB.addRelationAndTranspose(curCUElement,
						LRelation.DECLARES_IMPORT, curImport);

				return super.visit(node);
			}

			@Override
			public boolean visit(EnumDeclaration node) {
				ITypeBinding binding = node.resolveBinding();
				visitType(node, binding);
				return super.visit(node);
			}

			@Override
			public boolean visit(TypeDeclaration node) {

				ITypeBinding binding = node.resolveBinding();
				visitType(node, binding);
				return super.visit(node);

			}

			public void visitType(ASTNode node, ITypeBinding binding) {

				if (binding == null)
					return;
				// backup the current type
				LElement oldType = curType;
				
				curType = (LElement) aelementfactory.createLElement(
						LICategories.TYPE, node,binding,aannotatedsourcefile);
				addElement(curType,getColor(node));

				if (!binding.isTopLevel()) {// 返回这一对象是否表示顶层的class interface enum 或者 annotation 类型

					// ADD DECLARE RELATIONSHIP FOR COMPILTATTION UNIT
					aDB.addRelationAndTranspose(curCUElement,
							LRelation.DECLARES_TYPE_TRANSITIVE, curType);

					// ADD DECLARE RELATIONSHIP FOR TYPE
					aDB.addRelationAndTranspose(oldType,
							LRelation.DECLARES_TYPE, curType);

					// ADD TRANSITIVE DECLARE RELATIONSHIP FOR SUPER TYPES
					for (LElement remType : curTypeReminder) {
						aDB.addRelationAndTranspose(remType,
										LRelation.DECLARES_TYPE_TRANSITIVE,
										curType);
					}

					curTypeReminder.push(oldType);

				} else {
					// ADD DECLARE RELATIONSHIP FOR COMPILTATTION UNIT
					aDB.addRelationAndTranspose(curCUElement,
							LRelation.DECLARES_TYPE, curType);
				}

			}

			@Override
			public void endVisit(EnumDeclaration node) {
				ITypeBinding binding = node.resolveBinding();
				endVisitType(binding);

			}

			@Override
			public void endVisit(TypeDeclaration node) {
				ITypeBinding binding = node.resolveBinding();
				endVisitType(binding);

			}

			public void endVisitType(ITypeBinding binding) {

				if (binding == null)
					return;

				// restore current type and temp method
				if (!curTypeReminder.isEmpty()) {
					curType = (LElement) curTypeReminder.pop();
				} else {
					curType = null;
				}
			}

			public boolean visit(MethodDeclaration node) {
				IMethodBinding binding = node.resolveBinding();
				if (binding != null) {
					//函数声明
					
					curMethod = (LElement) aelementfactory.createLElement(LICategories.METHOD, node,binding,aannotatedsourcefile);
					addElement(curMethod,getColor(node));
					
					
					curParamIndex = 0;

					// ADD DECLARE RELATIONSHIP FOR TYPE
					aDB.addRelationAndTranspose(curType,
							LRelation.DECLARES_METHOD, curMethod);

					// ADD TRANSITIVE DECLARE RELATIONSHIP FOR COMPILTATTION
					// UNIT
					aDB.addRelationAndTranspose(curCUElement,LRelation.DECLARES_METHOD_TRANSITIVE,curMethod);

					// ADD TRANSITIVE DECLARE RELATIONSHIP FOR SUPER TYPES
					for (LElement remType : curTypeReminder) {
						aDB.addRelationAndTranspose(remType,LRelation.DECLARES_METHOD_TRANSITIVE,curMethod);
					}
					
					// 将这个函数的methodbinding中得IMethod加入到 IMethod对应
					IMethod imethod = (IMethod)binding.getJavaElement();
					curMethod.setIMethod(imethod);
				}
				return super.visit(node);
			}

			@Override
			public void endVisit(MethodDeclaration node) {
				curMethod = null;

			}

			public boolean visit(VariableDeclarationFragment node) {
				IVariableBinding binding = node.resolveBinding();
				visitFieldOrVariable(node, binding);
				return super.visit(node);
			}

			public boolean visit(SingleVariableDeclaration node) {
				IVariableBinding binding = node.resolveBinding();
				visitFieldOrVariable(node, binding);
				return super.visit(node);
			}

			public boolean visit(EnumConstantDeclaration node) {
				IVariableBinding binding = node.resolveVariable();
				visitFieldOrVariable(node, binding);
				return super.visit(node);
			}

			public void visitFieldOrVariable(ASTNode node,
					IVariableBinding binding) {
				if (binding == null)
					return;

				LElement curElement = null;
				LRelation curRelation = null;
				LRelation curTransitiveRelation = null;
				//类类型变量
				if (binding.isField() || binding.isEnumConstant()) {
					
					curElement = (LElement) aelementfactory.createLElement(
							LICategories.FIELD, node,binding,aannotatedsourcefile);
					addElement(curElement,getColor(node));
					curRelation = LRelation.DECLARES_FIELD;
					curTransitiveRelation = LRelation.DECLARES_FIELD_TRANSITIVE;

					// ADD DECLARE RELATIONSHIP FOR TYPE
					aDB.addRelationAndTranspose(curType, curRelation,curElement);

				} else {
					
					curElement = (LElement) aelementfactory.createLElement(
							LICategories.LOCAL_VARIABLE, node,binding,aannotatedsourcefile);

					if (binding.isParameter())
						((LElement) curElement).setParamIndex(curParamIndex++);

					addElement(curElement,getColor(node));
					curRelation = LRelation.DECLARES_LOCAL_VARIABLE;
					curTransitiveRelation = LRelation.DECLARES_LOCAL_VARIABLE_TRANSITIVE;

					// ADD DECLARE RELATIONSHIP FOR TYPE
					aDB.addRelationAndTranspose(curType, curTransitiveRelation,
							curElement);

					// check if null as block could also be an intializer
					if (curMethod != null) {
						// ADD DECLARE RELATIONSHIP FOR METHOD
						aDB.addRelationAndTranspose(curMethod, curRelation,
								curElement);
					}

				}

				// ADD TRANSITIVE DECLARE RELATIONSHIP FOR COMPILTATTION UNIT
				aDB.addRelationAndTranspose(curCUElement,
						curTransitiveRelation, curElement);

				// ADD TRANSITIVE DECLARE RELATIONSHIP FOR SUPER TYPES
				for (LElement remType : curTypeReminder) {
					aDB.addRelationAndTranspose(remType, curTransitiveRelation,
							curElement);
				}

			}

		});
	}
	private String getDisplayName(org.eclipse.jdt.core.dom.ASTNode node) {

		// ALEX
		if (node instanceof ImportDeclaration)
			return ((ImportDeclaration) node).getName().getFullyQualifiedName();

		if (node instanceof SimpleType)
			return ((SimpleType) node).getName().getFullyQualifiedName();

		if (node instanceof PrimitiveType)
			return ((PrimitiveType) node).toString();

		if (node instanceof TypeDeclaration)
			return ((TypeDeclaration) node).getName().getFullyQualifiedName();

		if (node instanceof EnumDeclaration)
			return ((EnumDeclaration) node).getName().getFullyQualifiedName();

		if (node instanceof MethodDeclaration)
			return ((MethodDeclaration) node).getName().getFullyQualifiedName();

		if (node instanceof MethodInvocation)
			return ((MethodInvocation) node).getName().getFullyQualifiedName();

		if (node instanceof SuperMethodInvocation)
			return ((SuperMethodInvocation) node).getName()
					.getFullyQualifiedName();

		if (node instanceof ClassInstanceCreation)
			return ((ClassInstanceCreation) node).getType().toString();

		if (node instanceof VariableDeclarationFragment)
			return ((VariableDeclarationFragment) node).getName()
					.getFullyQualifiedName();

		if (node instanceof EnumConstantDeclaration)
			return ((EnumConstantDeclaration) node).getName()
					.getFullyQualifiedName();

		if (node instanceof SingleVariableDeclaration)
			return ((SingleVariableDeclaration) node).getName()
					.getFullyQualifiedName();

		if (node instanceof SimpleName)
			return ((SimpleName) node).getFullyQualifiedName();

		// ALEX

		return org.eclipse.jdt.core.dom.ASTNode.nodeClassForType(
				node.getNodeType()).getSimpleName();
	}
	
	
	
	
}
