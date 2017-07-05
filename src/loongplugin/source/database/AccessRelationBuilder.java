package loongplugin.source.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.SourceFileColorManager;
import loongplugin.feature.Feature;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LElementColorManager;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LICategories;
import loongplugin.source.database.model.LRelation;
import loongplugin.utils.MethodPathItem;
import loongplugin.utils.OverridingRelationUtils;
import loongplugin.utils.TypePathItem;

public class AccessRelationBuilder {
	
	private ProgramDatabase aDB;
	private LFlyweightElementFactory LElementFactory;
	private CLRAnnotatedSourceFile aannotatedsourcefile;
	private LElementColorManager LElementColorManager;
	
	private LElement curCUElement;
	private LElement curImport;
	private LElement curType;
	private LElement curMethod;
	private LElement curField;
	private LElement curLocalVariable;
	private LElement curExtendsAccess;

	private Stack<LElement> curTypeReminder;

	private Set<LElement> curParameter;
	private LocalContextElement curContext;
	private Stack<LocalContextElement> curContextReminder;

	private Map<String, LElement> importMap;
	private SourceFileColorManager sourceColorManager;
	
	private boolean puremod =false;
	private class LocalContextElement {
		private ASTNode node;

		private LElement element;
		private ASTNode accessNode;

		public LocalContextElement(ASTNode node, ASTNode accessNode, LElement element) {
			this.node = node;
			this.element = element;
			this.accessNode = accessNode;
		}

		public ASTNode getNode() {
			return node;
		}

		public LElement getElement() {
			return element;
		}

		public ASTNode getAccessNode() {
			return accessNode;
		}

	}
	
	public AccessRelationBuilder(ProgramDatabase pDB,
			LFlyweightElementFactory pLElementFactory) {

		this.aDB = pDB;
		this.LElementFactory = pLElementFactory;
	}

	public void buildRelations(ICompilationUnit lCU,
			CLRAnnotatedSourceFile pannotatedsourcefile,
			LElementColorManager pelementColorManager) {

		this.aannotatedsourcefile = pannotatedsourcefile;
		this.LElementColorManager = pelementColorManager;
		this.sourceColorManager = (SourceFileColorManager) pannotatedsourcefile.getColorManager();
		CompilationUnit ast = JDTParserWrapper.parseCompilationUnit(lCU);
		reset();
		update(ast);
	}
	public void buildRelations(ICompilationUnit lCU){
		puremod = true;
		CompilationUnit ast = JDTParserWrapper.parseCompilationUnit(lCU);
		reset();
		update(ast);
	}

	private void update(ASTNode node) {
		// TODO Auto-generated method stub
		node.accept(new ASTVisitor() {

			private void addElement(LElement element, Set<Feature> colors) {
				aDB.addElement(element);
				if(puremod){
					for (Feature color : colors)
						LElementColorManager.addElementToColor(color, element);
				}
			}

			private Set<Feature> getColor(ASTNode node) {
				if(!puremod){
					return sourceColorManager.getColors(node);
				}else{
					try {
						throw new Exception("in pure mod not color information is available(from: AccessRelationBuilder)");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
			}

			// COMPILATION UNIT CONTEXT//
			@Override
			public boolean visit(CompilationUnit node) {
				// create the CU element and store it
				curCUElement = (LElement) LElementFactory.getElement(node);
				return super.visit(node);
			}

			// IMPORT CONTEXT//
			@Override
			public boolean visit(ImportDeclaration node) {
				curImport = (LElement) LElementFactory.getElement(node);

				IBinding binding = node.resolveBinding();
				if (binding instanceof ITypeBinding) {
					importMap.put(((ITypeBinding) binding).getKey(), curImport);
				}

				return super.visit(node);
			}

			public void endVisit(ImportDeclaration node) {
				curImport = null;
			}

			// TYPE CONTEXT//
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

				if (!binding.isTopLevel()) {
					curTypeReminder.push(curType);
				}

				curType = (LElement) LElementFactory.getElement(binding);

				if (curType == null)
					return;

				createExtendsAndImplementsTypeRelations(binding);

				// define extends access node
				if (node.getNodeType() != ASTNode.TYPE_DECLARATION)
					return;

				Object curExtendsType = node.getStructuralProperty(TypeDeclaration.SUPERCLASS_TYPE_PROPERTY);

				if (curExtendsType == null)
					return;
				
				ASTNode curTypeAccess = (ASTNode) curExtendsType;//Type access from node --> curTypeAccess
				
				curExtendsAccess = (LElement) LElementFactory.createLElement(LICategories.TYPE_ACCESS, (ASTNode) curExtendsType,null, aannotatedsourcefile);

				 
			}

			private void createExtendsAndImplementsTypeRelations(ITypeBinding binding) {

				List<TypePathItem> directTypes = new ArrayList<TypePathItem>();
				List<TypePathItem> transitiveTypes = new ArrayList<TypePathItem>();

				if (collectExtendsAndImplementsTypeRelations(binding,
						directTypes, transitiveTypes)) {

					LElement typeElement;
					for (TypePathItem pathItem : directTypes) {
						typeElement = (LElement) LElementFactory.getElement(pathItem.getBinding());
						if (typeElement != null) {
							if (pathItem.isInterface()) {
								//一个类型 实现了另一个类型
								aDB.addRelationAndTranspose(curType,LRelation.IMPLEMENTS_TYPE,typeElement);
							} else {
								aDB.addRelationAndTranspose(curType,LRelation.EXTENDS_TYPE,typeElement);
							}
						}
					}

					for (TypePathItem pathItem : transitiveTypes) {

						typeElement = (LElement) LElementFactory
								.getElement(pathItem.getBinding());

						if (typeElement != null) {

							if (pathItem.isInterface()) {
								aDB.addRelationAndTranspose(
												curType,
												LRelation.IMPLEMENTS_TYPE_TRANSITIVE,
												typeElement);
							} else {
								aDB.addRelationAndTranspose(curType,
										LRelation.EXTENDS_TYPE_TRANSITIVE,
										typeElement);
							}
						}

					}
				}
			}

			private boolean collectExtendsAndImplementsTypeRelations(ITypeBinding declTypeBinding,
					List<TypePathItem> directTypes,List<TypePathItem> transitiveTypes) 
			{

				if (declTypeBinding == null || transitiveTypes == null
						|| directTypes == null)
					return false;

				Set<String> checkedInterfaces = new HashSet<String>();
				ITypeBinding[] interfaces = declTypeBinding.getInterfaces();

				for (ITypeBinding tmpInterface : interfaces) {
					directTypes.add(new TypePathItem(tmpInterface, true));
					checkedInterfaces.add(tmpInterface.getKey());
					OverridingRelationUtils.collectExtendedAndImplementedTypesInInterfaces(
									tmpInterface, transitiveTypes,
									checkedInterfaces);
				}

				ITypeBinding superClass = declTypeBinding.getSuperclass();
				if (superClass != null) {
					directTypes.add(new TypePathItem(superClass, false));
					OverridingRelationUtils
							.collectExtendedAndImplementedTypesInSuperClasses(
									superClass, transitiveTypes,
									checkedInterfaces);
				}

				if (directTypes.size() == 0 && transitiveTypes.size() == 0)
					return false;

				return true;
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

			// METHOD CONTEXT//
			public boolean visit(MethodDeclaration node) {

				IMethodBinding binding = node.resolveBinding();
				if (binding != null) {
					curMethod = LElementFactory.getElement(binding);

					createInherritedAndOverriddenMethodRelations(binding);

				}

				return super.visit(node);
			}

			private void createInherritedAndOverriddenMethodRelations(
					IMethodBinding binding) {

				List<MethodPathItem> inhMethods = new ArrayList<MethodPathItem>();

				if (collectInherritedOrOverridenMethods(binding, inhMethods)) {

					boolean first = true;
					LElement superMethod;

					LRelation overridesRelation = LRelation.OVERRIDES_METHOD;
					LRelation implementsRelation = LRelation.IMPLEMENTS_METHOD;

					for (MethodPathItem methodPathItem : inhMethods) {

						superMethod = (LElement) LElementFactory
								.getElement(methodPathItem.getBinding());

						if (superMethod != null) {

							if (!methodPathItem.isAbstract()) {
								aDB.addRelationAndTranspose(curMethod,
										overridesRelation, superMethod);// 子类方法 到 父类方法
							} else {
								aDB.addRelationAndTranspose(curMethod,
										implementsRelation, superMethod);// 子类方法 到 父类接口
							}
						}

						if (first) {
							first = false;
							overridesRelation = LRelation.OVERRIDES_METHOD_TRANSITIVE;
							implementsRelation = LRelation.IMPLEMENTS_METHOD_TRANSITIVE;
						}

					}
				}
			}

			private boolean collectInherritedOrOverridenMethods(
					IMethodBinding binding, List<MethodPathItem> inhMethods) {

				ITypeBinding declTypeBinding = binding.getDeclaringClass();

				if (declTypeBinding == null)
					return false;

				Set<String> checkedInterfaces = new HashSet<String>();

				// (recursively) collects all keys of methods in abstract
				// classes which
				// belongs to this declaration
				OverridingRelationUtils.collectSimilarMethodKeysInSuperClasses(
						binding, declTypeBinding.getSuperclass(), inhMethods,
						checkedInterfaces);

				// (recursively) collects all keys of methods in interfaces
				// which
				// belongs to this declaration
				OverridingRelationUtils.collectSimilarMethodKeysInInterfaces(
						binding, declTypeBinding.getInterfaces(), inhMethods,
						checkedInterfaces);

				// the set should contain at least one inherited method
				if (inhMethods.size() == 0)
					return false;

				return true;
			}

			@Override
			public void endVisit(MethodDeclaration node) {
				curMethod = null;
			}

			@Override
			public boolean visit(ConstructorInvocation node) {
				IMethodBinding binding = node.resolveConstructorBinding();
				List args = node.arguments();
				handleMethodCall(node, binding, args);
				return super.visit(node);
			}

			@Override
			public void endVisit(ConstructorInvocation node) {

				handleEndMethodCall(node);
			}

			@Override
			public boolean visit(ClassInstanceCreation node) {
				IMethodBinding binding = node.resolveConstructorBinding();
				List args = node.arguments();
				handleMethodCall(node, binding, args);
				return super.visit(node);
			}

			@Override
			public void endVisit(ClassInstanceCreation node) {

				handleEndMethodCall(node);
			}

			@Override
			public boolean visit(SuperConstructorInvocation node) {
				IMethodBinding binding = node.resolveConstructorBinding();
				List args = node.arguments();
				handleMethodCall(node, binding, args);
				return super.visit(node);
			}

			@Override
			public void endVisit(SuperConstructorInvocation node) {

				handleEndMethodCall(node);
			}

			@Override
			public boolean visit(SuperMethodInvocation node) {
				IMethodBinding binding = node.resolveMethodBinding();
				List args = node.arguments();
				handleMethodCall(node, binding, args);
				return super.visit(node);
			}

			@Override
			public void endVisit(SuperMethodInvocation node) {

				handleEndMethodCall(node);
			}

			@Override
			public boolean visit(MethodInvocation node) {
				IMethodBinding binding = node.resolveMethodBinding();
				List args = node.arguments();
				handleMethodCall(node, binding, args);
				return super.visit(node);

			}

			@Override
			public void endVisit(MethodInvocation node) {
				handleEndMethodCall(node);
			}

			private void handleMethodCall(ASTNode node, IMethodBinding binding, List arguments) {

				if (binding != null) {

					// DEFINING CONTEXT FOR METHOD
					LElement curElement = LElementFactory.getElement(binding);

					if (curContext != null) {
						curContextReminder.push(curContext);
					}

					// cur element could also be null!
					curContext = new LocalContextElement(node, null, curElement);

					Set<LElement> localVars = null;
					if (curElement != null) {
						localVars = aDB.getRange(curElement,LRelation.DECLARES_LOCAL_VARIABLE);
					}

					curParameter = new HashSet<LElement>();

					// create param access elements
					for (int i = 0; i < arguments.size(); i++) {
						ASTNode tmpArg = (ASTNode) arguments.get(i);
						
						LElement paramAccessElement = (LElement) LElementFactory.createLElement(LICategories.PARAMETER_ACCESS,tmpArg,null,aannotatedsourcefile);
						addElement(paramAccessElement, getColor(tmpArg));

						curParameter.add(paramAccessElement);

						if (localVars == null)
							continue;

						// aDB.addRelationAndTranspose(curElement,
						// ARelation.ACCESS_PARAMETER, paramAccessElement);

						for (LElement localVar : localVars) {
							if (((LElement) localVar).getParamIndex() == i) {
								aDB.addRelationAndTranspose(localVar,LRelation.REQUIRES,paramAccessElement);// 从本地到 调用函数
							}
						}

					}

					if (curParameter.size() == 0)
						curParameter = null;

					handleMethodAccess(node, curElement);

				}
			}

			private void handleEndMethodCall(ASTNode node) {
				handleEndVisitContext(node);

				curParameter = null;

			}

			// LOCAL VARIABLE AND FIELD CONTEXT//
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

				if (binding.isField() || binding.isEnumConstant()) {
					curField = (LElement) LElementFactory.getElement(binding);
				} else {
					curLocalVariable = (LElement) LElementFactory
							.getElement(binding);

				}

				// VariableDeclarationFragment extra handling is needed!
				if (node.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT) {

					handleTypeAccessForVarDeclFragment(node);
				}

			}

			public void endVisit(EnumConstantDeclaration node) {
				IVariableBinding binding = node.resolveVariable();
				endVisitFieldOrVariable(node, binding);
			}

			public void endVisit(VariableDeclarationFragment node) {
				IVariableBinding binding = node.resolveBinding();
				endVisitFieldOrVariable(node, binding);
			}

			public void endVisit(SingleVariableDeclaration node) {
				IVariableBinding binding = node.resolveBinding();
				endVisitFieldOrVariable(node, binding);
			}

			public void endVisitFieldOrVariable(ASTNode node,
					IVariableBinding binding) {

				if (binding == null)
					return;

				if (binding.isField() || binding.isEnumConstant()) {
					curField = null;
				} else {
					curLocalVariable = null;

				}
			}

			public boolean visit(CastExpression node) {
				final ASTNode curNode = node;
				node.getExpression().accept(new ASTVisitor() {

					public boolean visit(SimpleName nameNode) {

						addContextForFieldorVariable(curNode, nameNode);
						return super.visit(nameNode);
					}

				});

				return super.visit(node);
			}

			public void endVisit(CastExpression node) {
				handleEndVisitContext(node);
			}

			public boolean visit(InstanceofExpression node) {
				final ASTNode curNode = node;
				node.getLeftOperand().accept(new ASTVisitor() {

					public boolean visit(SimpleName nameNode) {

						addContextForFieldorVariable(curNode, nameNode);
						return super.visit(nameNode);
					}

				});

				return super.visit(node);
			}

			public void endVisit(InstanceofExpression node) {
				handleEndVisitContext(node);
			}

			@Override
			public boolean visit(Assignment node) {
				final ASTNode curNode = node;

				node.getLeftHandSide().accept(new ASTVisitor() {

					public boolean visit(SimpleName nameNode) {

						addContextForFieldorVariable(curNode, nameNode);
						return super.visit(nameNode);
					}

				});

				return super.visit(node);

			}

			private void addContextForFieldorVariable(ASTNode contextNode,
					SimpleName nameNode) {
				IBinding binding = nameNode.resolveBinding();
				
				if (binding != null && binding instanceof IVariableBinding) {

					// DEFINING CONTEXT FOR FIELD OR LOCAL VARIABLE!
					LElement curElement = LElementFactory.getElement(binding);

					if (curContext != null) {
						curContextReminder.push(curContext);
					}

					curContext = new LocalContextElement(contextNode, nameNode,
							curElement);

				}
			}

			public void endVisit(Assignment node) {
				handleEndVisitContext(node);
			}

			private void handleEndVisitContext(ASTNode node) {
				if (curContext == null)
					return;

				while (curContext != null && curContext.getNode().equals(node)) {

					if (!curContextReminder.isEmpty()) {
						curContext = curContextReminder.pop();
					} else {
						curContext = null;
					}

				}
			}

			// ACCESS HANDLING//

			@Override
			public boolean visit(SimpleName node) {
				visitName(node);
				return super.visit(node);
			}

			// HANDLE ACCESS
			public void visitName(Name node) {

				IBinding binding = node.resolveBinding();

				if (binding == null)
					return;

				if (binding instanceof ITypeBinding) {
					handleTypeAccess(node, (ITypeBinding) binding);
				} else if (binding instanceof IVariableBinding) {
					handleFieldOrVariableAccess(node,
							(IVariableBinding) binding);
				}
				// else if (binding instanceof IMethodBinding) {
				// handleMethodAccess(node,(IMethodBinding) binding);
				// }

			}

			// HANDLE TYPE ACCESS
			private void handleTypeAccess(Name node, ITypeBinding binding) {
				/*
				 * node 名称 binding 对应的类型信息
				 */

				ASTNode parent = node.getParent();

				// don't check type or enum declarations
				if (parent instanceof TypeDeclaration)
					return;

				if (parent instanceof EnumDeclaration)
					return;

				ASTNode elementNode = node;

				if (parent instanceof Type) {
					elementNode = parent;
				}
				//From 
				
				LElement typeAccessElement = (LElement) LElementFactory.createLElement(LICategories.TYPE_ACCESS,elementNode,null,aannotatedsourcefile);
				addElement(typeAccessElement, getColor(elementNode));

				LElement typeElement = (LElement) LElementFactory.getElement(binding);

				// ADD ACCESS TO ACTUAL TYPE
				if (typeElement != null)
					aDB.addRelationAndTranspose(typeAccessElement,
							LRelation.BELONGS_TO, typeElement);// 从类型使用 到 类型 

				LElement importElement = importMap.get(binding.getKey());
				if (importElement != null && !importElement.equals(curImport))
					aDB.addRelationAndTranspose(typeAccessElement,
							LRelation.BELONGS_TO, importElement);//  从类型到 引用

				// ADD ALL ELEMENTS WHO ACCESS THIS ELEMENT

				// ADD ALWAYS ACCESS RELATION FOR COMP. UNIT
				aDB.addRelationAndTranspose(curCUElement,
								LRelation.ACCESS_TYPE_TRANSITIVE,
								typeAccessElement);

				// ADD ACCESS ELEMENT TO OTHER ELEMENTS DEPENDING ON CURRENT
				// CONTEXT
				if (curImport != null)
					aDB.addRelationAndTranspose(curImport,
							LRelation.ACCESS_TYPE, typeAccessElement);//从 类型 到 类型的访问

				if (curType != null)
					aDB.addRelationAndTranspose(curType,
							LRelation.ACCESS_TYPE_TRANSITIVE,
							typeAccessElement);

				for (LElement tmpType : curTypeReminder) {
					aDB.addRelationAndTranspose(tmpType,
							LRelation.ACCESS_TYPE_TRANSITIVE,
							typeAccessElement);
				}

				// if (curParameter != null) {
				// for (AIElement paramAccess : curParameter) {
				// aDB.addRelationAndTranspose(typeAccessElement,
				// ARelation.DECLARES_PARAMETER, paramAccess);
				// }
				// curParameter = null;
				// }

				boolean directRelationAdded = false;

				// if is one of them, will be handled separately later on
				switch (elementNode.getParent().getNodeType()) {
				case ASTNode.VARIABLE_DECLARATION_STATEMENT:
					directRelationAdded = true;
					break;
				case ASTNode.VARIABLE_DECLARATION_EXPRESSION:
					directRelationAdded = true;
					break;
				case ASTNode.FIELD_DECLARATION:
					directRelationAdded = true;
					break;
				}

				if (!directRelationAdded && curContext != null) {

					directRelationAdded = true;
					if (curContext.getElement() != null)
						aDB.addRelationAndTranspose(curContext.getElement(),
								LRelation.ACCESS_TYPE, typeAccessElement);

				}

				for (LocalContextElement tmpContext : curContextReminder) {
					if (tmpContext.getElement() != null)
						aDB.addRelationAndTranspose(tmpContext.getElement(),
								LRelation.ACCESS_TYPE_TRANSITIVE,
								typeAccessElement);
				}

				if (curLocalVariable != null) {
					if (directRelationAdded) {
						aDB.addRelationAndTranspose(curLocalVariable,
								LRelation.ACCESS_TYPE_TRANSITIVE,
								typeAccessElement);
					} else {
						aDB.addRelationAndTranspose(curLocalVariable,
								LRelation.ACCESS_TYPE, typeAccessElement);
						directRelationAdded = true;
					}
				}

				if (curField != null) {
					if (directRelationAdded) {
						aDB.addRelationAndTranspose(curField,
								LRelation.ACCESS_TYPE_TRANSITIVE,
								typeAccessElement);
					} else {
						aDB.addRelationAndTranspose(curField,
								LRelation.ACCESS_TYPE, typeAccessElement);
						directRelationAdded = true;
					}
				}

				if (curMethod != null) {
					aDB.addRelationAndTranspose(curMethod,
							LRelation.DECLARES_TYPE_ACCESS,
							typeAccessElement);

					if (directRelationAdded) {
						aDB.addRelationAndTranspose(curMethod,
								LRelation.ACCESS_TYPE_TRANSITIVE,
								typeAccessElement);
					} else {
						aDB.addRelationAndTranspose(curMethod,
								LRelation.ACCESS_TYPE, typeAccessElement);
					}
				}

			}

			// in VariableDeclarationFragment the type is stored
			// in parent node, which was already handled
			private void handleTypeAccessForVarDeclFragment(ASTNode node) {

				ASTNode parentNode = node.getParent();

				if (parentNode == null)
					return;

				Type type = null;

				switch (parentNode.getNodeType()) {
				case ASTNode.VARIABLE_DECLARATION_STATEMENT:
					type = ((VariableDeclarationStatement) parentNode)
							.getType();
					break;
				case ASTNode.VARIABLE_DECLARATION_EXPRESSION:
					type = ((VariableDeclarationExpression) parentNode)
							.getType();
					break;
				case ASTNode.FIELD_DECLARATION:
					type = ((FieldDeclaration) parentNode).getType();
					break;
				}

				if (type == null)
					return;

				LElement typeAccessElement = (LElement) LElementFactory
						.getElement(type);

				if (typeAccessElement == null)
					return;

				if (curField != null)
					aDB.addRelationAndTranspose(curField,
							LRelation.ACCESS_TYPE, typeAccessElement);

				if (curLocalVariable != null)
					aDB.addRelationAndTranspose(curLocalVariable,
							LRelation.ACCESS_TYPE, typeAccessElement);

			}

			// HANDLE FieldOrVariableAccess
			private void handleFieldOrVariableAccess(Name node,
					IVariableBinding binding) {

				ASTNode parent = node.getParent();

				// don't check field / variable access in declaration
				if (parent instanceof VariableDeclarationFragment)
					return;

				if (parent instanceof EnumConstantDeclaration)
					return;

				if (parent instanceof SingleVariableDeclaration)
					return;

				LElement element = LElementFactory.getElement(binding);

				LRelation accessRelation = LRelation.ACCESS_FIELD;
				LRelation accessTransitiveRelation = LRelation.ACCESS_FIELD_TRANSITIVE;
				LRelation declaresRelation = LRelation.DECLARES_FIELD_ACCESS;
				LICategories cat = LICategories.FIELD_ACCESS;
				boolean isField = true;

				if (element != null && element.getCategory() != LICategories.FIELD) {
					accessRelation = LRelation.ACCESS_LOCAL_VARIABLE;
					accessTransitiveRelation = LRelation.ACCESS_LOCAL_VARIABLE_TRANSITIVE;
					declaresRelation = LRelation.DECLARES_LOCAL_VARIABLE_ACCESS;
					cat = LICategories.LOCAL_VARIABLE_ACCESS;
				}
				
				LElement accessElement;
				accessElement= LElementFactory.createLElement(cat,node,null,aannotatedsourcefile);
				
				addElement(accessElement, getColor(node));

				// ADD ACCESS TO ACTUAL Element
				if (element != null)
					aDB.addRelationAndTranspose(accessElement,
							LRelation.BELONGS_TO, element);// element 是 field 或者 variable accessElement是对它的访问

				// ADD ALL ELEMENTS WHO ACCESS THIS ELEMENT

				// ADD ALWAYS TRANS. ACCESS RELATION FOR COMP. UNIT
				aDB.addRelationAndTranspose(curCUElement,
						accessTransitiveRelation, accessElement);

				// ADD ALWAYS TRANS. ACCESS RELATION FOR TYPE
				if (curType != null) {

					aDB.addRelationAndTranspose(curType,
							accessTransitiveRelation, accessElement);// 从 类型 到 访问[本地变量]

					// TODO: CHECK SUPER ACCESS FOR OUT OF CONTEXT ELEMENTS!

					// check if field access is a super field access
					if (curExtendsAccess != null && element != null && isField) {

						if (isSuperAccess(element,
								LRelation.T_DECLARES_FIELD)) {
							aDB.addRelationAndTranspose(accessElement,
									LRelation.BELONGS_TO, curExtendsAccess);
						}

					}
				}

				for (LElement tmpType : curTypeReminder) {
					aDB.addRelationAndTranspose(tmpType,
							accessTransitiveRelation, accessElement);
				}

				boolean directRelationAdded = false;
				// do not add variable access for it self
				if (curContext != null) {
					if (curContext.getElement() == null) {
						if (!node.equals(curContext.getAccessNode())) {
							directRelationAdded = true;
						}
					} else if (!curContext.getElement().equals(element)) {
						aDB.addRelationAndTranspose(curContext.getElement(),
								accessRelation, accessElement);
						directRelationAdded = true;
					}
				}

				LocalContextElement tmpContext;
				int size = curContextReminder.size();
				for (int i = 0; i < size; i++) {
					tmpContext = curContextReminder.get(i);

					// letzte eintrag!
					if (!directRelationAdded && size - i == 1) {

						directRelationAdded = true;

						if (tmpContext.getElement() == null)
							continue;

						aDB.addRelationAndTranspose(tmpContext.getElement(),
								accessRelation, accessElement);

						continue;

					}
					if (tmpContext.getElement() != null)
						aDB.addRelationAndTranspose(tmpContext.getElement(),
								accessTransitiveRelation, accessElement);

				}

				if (curLocalVariable != null) {
					if (directRelationAdded) {
						aDB.addRelationAndTranspose(curLocalVariable,
								accessTransitiveRelation, accessElement);
					} else {
						aDB.addRelationAndTranspose(curLocalVariable,
								accessRelation, accessElement);
						directRelationAdded = true;
					}
				}

				if (curField != null) {
					if (directRelationAdded) {
						aDB.addRelationAndTranspose(curField,
								accessTransitiveRelation, accessElement);
					} else {
						aDB.addRelationAndTranspose(curField, accessRelation,
								accessElement);
						directRelationAdded = true;
					}
				}

				if (curMethod != null) {
					aDB.addRelationAndTranspose(curMethod, declaresRelation,
							accessElement);

					if (directRelationAdded) {
						aDB.addRelationAndTranspose(curMethod,
								accessTransitiveRelation, accessElement);
					} else {
						aDB.addRelationAndTranspose(curMethod, accessRelation,
								accessElement);
					}
				}

			}

			// HANDLE METHOD ACCESS
			private void handleMethodAccess(ASTNode node, LElement element) {
				
				LElement accessElement;

				
				accessElement = LElementFactory.createLElement(LICategories.METHOD_ACCESS, node,null,aannotatedsourcefile);
				
				addElement(accessElement, getColor(node));

				// ADD BELONGS TO RELATION
				if (element != null)
					aDB.addRelationAndTranspose(accessElement,
							LRelation.BELONGS_TO, element);

				// ADD ALL ELEMENTS WHO ACCESS THIS ELEMENT

				// HANDLE CURRENT PARAMS
				if (curParameter != null) {
					for (LElement paramAccess : curParameter) {
						aDB.addRelationAndTranspose(accessElement,
								LRelation.DECLARES_PARAMETER, paramAccess);
					}
					curParameter = null;
				}

				// ADD ALWAYS TRANS. ACCESS RELATION FOR COMP. UNIT
				aDB.addRelationAndTranspose(curCUElement,
						LRelation.ACCESS_METHOD_TRANSITIVE, accessElement);

				// ADD ALWAYS TRANS. ACCESS RELATION FOR TYPE
				if (curType != null) {
					aDB.addRelationAndTranspose(curType,
							LRelation.ACCESS_METHOD_TRANSITIVE,
							accessElement);

					// TODO: CHECK IF IS SUPER ACCESS FOR OUT OF CONTEXT
					// ELEMENTS (element == null)
					// check if access is a super access
					if (curExtendsAccess != null && element != null) {

						if (isSuperAccess(element,
								LRelation.T_DECLARES_METHOD)) {
							aDB.addRelationAndTranspose(accessElement,
									LRelation.BELONGS_TO, curExtendsAccess);
						}

					}

				}

				for (LElement tmpType : curTypeReminder) {
					aDB.addRelationAndTranspose(tmpType,
							LRelation.ACCESS_METHOD_TRANSITIVE,
							accessElement);
				}

				boolean directRelationAdded = false;

				LocalContextElement tmpContext;
				int size = curContextReminder.size();
				for (int i = 0; i < size; i++) {
					tmpContext = curContextReminder.get(i);

					// set in last entry the direct relation
					if (size - i == 1) {

						directRelationAdded = true;

						// OUT OF CONTEXT ELEMENT!
						if (tmpContext.getElement() == null)
							continue;

						aDB.addRelationAndTranspose(tmpContext.getElement(),
								LRelation.ACCESS_METHOD, accessElement);

						continue;

					}

					// OUT OF CONTEXT ELEMENT!
					if (tmpContext.getElement() == null)
						continue;

					aDB.addRelationAndTranspose(tmpContext.getElement(),
							LRelation.ACCESS_METHOD_TRANSITIVE,
							accessElement);

				}

				if (curLocalVariable != null) {
					if (directRelationAdded) {
						aDB.addRelationAndTranspose(curLocalVariable,
								LRelation.ACCESS_METHOD_TRANSITIVE,
								accessElement);
					} else {
						aDB.addRelationAndTranspose(curLocalVariable,
								LRelation.ACCESS_METHOD, accessElement);
						directRelationAdded = true;
					}
				}

				if (curField != null) {
					if (directRelationAdded) {
						aDB.addRelationAndTranspose(curField,
								LRelation.ACCESS_METHOD_TRANSITIVE,
								accessElement);
					} else {
						aDB.addRelationAndTranspose(curField,
								LRelation.ACCESS_METHOD, accessElement);
						directRelationAdded = true;
					}
				}

				if (curMethod != null) {
					aDB.addRelationAndTranspose(curMethod,LRelation.DECLARES_METHOD_ACCESS,accessElement);

					if (directRelationAdded) {
						aDB.addRelationAndTranspose(curMethod,
								LRelation.ACCESS_METHOD_TRANSITIVE,
								accessElement);
					} else {
						aDB.addRelationAndTranspose(curMethod,
								LRelation.ACCESS_METHOD, accessElement);
					}
				}

			}

			private boolean isSuperAccess(LElement declElement,
					LRelation declRelation) {

				Set<LElement> declareRange = aDB.getRange(declElement,
						declRelation);

				if (!declareRange.contains(curType)) {
					Set<LElement> superTypes = new HashSet<LElement>();
					superTypes.addAll(aDB.getRange(curType,
							LRelation.EXTENDS_TYPE));
					superTypes.addAll(aDB.getRange(curType,
							LRelation.EXTENDS_TYPE_TRANSITIVE));

					for (LElement tmpSuperType : superTypes) {
						if (declareRange.contains(tmpSuperType)) {
							return true;
						}

					}

				}

				return false;

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
	private void reset() {
		// TODO Auto-generated method stub
		curCUElement = null;
		curImport = null;
		curTypeReminder = new Stack<LElement>();

		curContextReminder = new Stack<LocalContextElement>();

		importMap = new HashMap<String, LElement>();

		curType = null;
		curMethod = null;
		curField = null;
		curLocalVariable = null;
		curContext = null;
		curExtendsAccess = null;
		curParameter = null;
	}

}
