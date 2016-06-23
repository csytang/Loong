package loongplugin.source.database.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongplugin.color.coloredfile.ASTID;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.CompilationUnitColorManager;
import loongplugin.events.ColorListChangedEvent;
import loongplugin.events.FileColorChangedEvent;
import loongplugin.feature.Feature;
import loongplugin.utils.EmbeddedASTNodeCollector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
/* JayFX - A Fact Extractor Plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~swevo/jayfx)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.4 $
 */
import org.eclipse.jdt.core.dom.*;

/**
 * Abstract class for the various program elements in the
 * model.  
 */ 
public class LElement {

	private String aId;
	private CLRAnnotatedSourceFile aColorSourceFile;
	private CompilationUnitColorManager aColorManager; 
	private ASTNode aastNode;
	
	private LICategories acategory;
	private Set<LICategories> subcategories;
	private Set<SlicingVariable> bindingvariables = new HashSet<SlicingVariable>();
	private int paramIndex = -1;
	private Map<IBinding,Set<ASTNode>>bindingLElements = new HashMap<IBinding,Set<ASTNode>>();
	
	private double probability = 0;
	private boolean debug = true;
	private Map<LElement,Double> priorprobability = new HashMap<LElement,Double>();
	private Map<LElement,Double> posterioriprobability = new HashMap<LElement,Double>();
	
	// this variable is only use for MethodDeclaration node;
	private IMethod bindMethod = null;
	
	private Set<LElement>priorLElements = new HashSet<LElement>();
	
	/**
	 * Builds an abstract element. 
	 * @param pId The id uniquely identifying the element.
	 * This id consists in the fully-qualified name of a class element,
	 * the field name appended to the fully-qualified named of the 
	 * declaring class for fields, and the name and signature appended
	 * to the fully-qualified name of the declaring class for methods.
	 */
	public  LElement(String pId,LICategories pcategory,CLRAnnotatedSourceFile pColorSourceFile,ASTNode pastNode)
	{
		aId = pId;
		aColorSourceFile = pColorSourceFile;
		aColorManager = (CompilationUnitColorManager) pColorSourceFile.getColorManager();
		aastNode = pastNode;
		
		acategory = pcategory;
		subcategories = new HashSet<LICategories>();
		for(Feature feature:aColorManager.getOwnColors(aastNode)){
			feature.addLElementToFeature(this);
			feature.addASTNodeToFeature(aColorSourceFile.getCompilationUnit(), pastNode);
		}
	}
	
	private LElement findDeclarationForFieldorVariable(LFlyweightElementFactory LElementFactory, SimpleName nameNode) {
		// TODO Auto-generated method stub
		IBinding binding = nameNode.resolveBinding();
		if (binding != null && binding instanceof IVariableBinding) {
			LElement curElement = LElementFactory.getElement(binding);
			return curElement;
		}
		return null;
	}
	
	
	public LElement visitFieldOrVariable(LFlyweightElementFactory LElementFactory,ASTNode node,IVariableBinding binding) {
		
		if (binding == null)
			return null;
		LElement curElement = null;
		if (binding.isField() || binding.isEnumConstant()) {
			curElement = (LElement) LElementFactory.getElement(binding);
		} else {
			curElement = (LElement) LElementFactory.getElement(binding);
		}
		
		return curElement;
	}
	
	private boolean canAdd(Set<SlicingVariable> bindingvariables, SlicingVariable slicevar){
		for(SlicingVariable variable:bindingvariables){
			if(variable.equals(slicevar)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 当我们遇到一个可分割的编译环境 将条件结点上的variable 加入到 这个结点中的子节点中
	 * @see computeSlicingVariable
	 * @param LElementFactory
	 * @param astnode
	 */
	public void bindSubASTNodeWithSlicingVariable(final LFlyweightElementFactory LElementFactory,ASTNode astnode, final Set<SlicingVariable> conditionslicingvariables){
		
		astnode.accept(new ASTVisitor(){
			@Override
			public void preVisit(ASTNode node) {
				// TODO Auto-generated method stub
				LElement nodeelement = LElementFactory.getElement(node);
				// 将所有的条件绑定加入到这个element中
				if(nodeelement!=null){
					for(SlicingVariable slice:conditionslicingvariables){
						if(canAdd(nodeelement.getBindSlicingVariable(),slice)){
							nodeelement.getBindSlicingVariable().add(slice);
						}
					}
				}
				super.preVisit(node);
			}
			
		});
		
		return;
	}
	
	
	
	
	public void computeSlicingVariable(final LFlyweightElementFactory LElementFactory){
		/*
		 * 对于一个 包含多个子ASTNode的节点 要递归的处理 加入一个ASTVisitor 当有变量就获取 信息
		 * 
		 */
		bindingvariables.clear();
		if(debug){
			System.out.println("-------------------------------------------");
			System.out.println("Current process node:\n"+aastNode.toString());
		}
		aastNode.accept(new ASTVisitor(){
			@Override
			public boolean visit(Assignment node) {
				/**
				 * a = b+c;
				 * a => b, c
				 * a  is parent(left hand)
				 * b,c are children(right hand)
				 */
				// TODO Auto-generated method stub
				final ASTNode curNode = node;
				final Set<SlicingVariable> children = new HashSet<SlicingVariable>();
				final Set<SlicingVariable> parent = new HashSet<SlicingVariable>();
				final Set<LElement> declarenameLeftNodeSet = new HashSet<LElement>();
				final Set<LElement> declarenameRightNodeSet = new HashSet<LElement>();
				node.getLeftHandSide().accept(new ASTVisitor() {// 左侧
					public boolean visit(SimpleName nameNode) {
						LElement declarenameLeftNode = findDeclarationForFieldorVariable(LElementFactory, nameNode);
						if(declarenameLeftNode!=null){
							SlicingVariable slicevariable = new SlicingVariable(declarenameLeftNode);
							if(canAdd(bindingvariables,slicevariable)){
								bindingvariables.add(slicevariable);
								if(debug){
									System.out.println("Add:\t"+nameNode.getFullyQualifiedName());
								}
							}
							parent.add(slicevariable);
							declarenameLeftNodeSet.add(declarenameLeftNode);
						}
						return super.visit(nameNode);
					}
				});
				node.getRightHandSide().accept(new ASTVisitor(){//右侧
					public boolean visit(SimpleName nameNode) {
						LElement declarenameRightNode = findDeclarationForFieldorVariable(LElementFactory, nameNode);
						if(declarenameRightNode!=null){
							SlicingVariable slicevariable = new SlicingVariable(declarenameRightNode);
							if(canAdd(bindingvariables,slicevariable)){
								bindingvariables.add(slicevariable);
								if(debug){
									System.out.println("Add:\t"+nameNode.getFullyQualifiedName());
								}
							}
							children.add(slicevariable);
							declarenameRightNodeSet.add(declarenameRightNode);
						}
						return super.visit(nameNode);
					}
				});
				
				
				// add inheritance dependency
				for(SlicingVariable schild:children){
					schild.addSlicingVariableParent(parent);
				}
				for(SlicingVariable sparent:parent){
					sparent.addSlicingVariableChildren(children);
				}
				
				for(LElement leftelement:declarenameLeftNodeSet){
					for(LElement rightelement:declarenameRightNodeSet){
						SlicingVariable slicevariable = new SlicingVariable(rightelement);
						if(canAdd(leftelement.bindingvariables,slicevariable)){
							leftelement.bindingvariables.add(slicevariable);
						}
					}
				}
				
				return super.visit(node);
			}

			@Override
			public boolean visit(CastExpression node) {
				// TODO Auto-generated method stub
				final ASTNode curNode = node;
				node.getExpression().accept(new ASTVisitor() {
						public boolean visit(SimpleName nameNode) {
							LElement declarenameNode = findDeclarationForFieldorVariable(LElementFactory, nameNode);
							if(declarenameNode!=null){
								SlicingVariable slicevariable = new SlicingVariable(declarenameNode);
								if(canAdd(bindingvariables,slicevariable)){
									bindingvariables.add(slicevariable);
									if(debug){
										System.out.println("Add:\t"+nameNode.getFullyQualifiedName());
									}
								}
								if(canAdd(declarenameNode.bindingvariables,slicevariable)){
									declarenameNode.bindingvariables.add(slicevariable);
								}
							}
							return super.visit(nameNode);
						}
				});
				return super.visit(node);
			}
			@Override
			public boolean visit(AnonymousClassDeclaration node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(ArrayAccess node) {
				// TODO Auto-generated method stub
				
				return super.visit(node);
			}

			@Override
			public boolean visit(ArrayCreation node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(ArrayInitializer node) {
				// TODO Auto-generated method stub
				
				return super.visit(node);
			}

			@Override
			public boolean visit(ArrayType node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(AssertStatement node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(Block node) {
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
			public boolean visit(EmptyStatement node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(WildcardType node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(ClassInstanceCreation node) {
				// TODO Auto-generated method stub
				IMethodBinding binding = node.resolveConstructorBinding();
				List args = node.arguments();
				handleMethodCall(LElementFactory,node, binding, args);
				return super.visit(node);
			}

			@Override
			public boolean visit(CompilationUnit node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			/**
			 * TODO:需要分析 如何计算 和是否需要
			 */
			@Override
			public boolean visit(ConditionalExpression node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(ConstructorInvocation node) {
				// TODO Auto-generated method stub
				IMethodBinding binding = node.resolveConstructorBinding();
				List args = node.arguments();
				handleMethodCall(LElementFactory,node, binding, args);
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
			public boolean visit(DoStatement node) {
				// TODO Auto-generated method stub
				// 获得While Expression中的内容
				Expression expression = node.getExpression();
				final Set<SlicingVariable>conditionslicingvariables = new HashSet<SlicingVariable>();
				
				conditionslicingvariables.clear();
				// 获得条件内容
				expression.accept(new ASTVisitor() {

					public boolean visit(SimpleName nameNode) {

						LElement element = findDeclarationForFieldorVariable(LElementFactory, nameNode);
						if(element!=null){
							SlicingVariable slicevariable = new SlicingVariable(element);
							conditionslicingvariables.add(slicevariable);
							if(canAdd(bindingvariables,slicevariable)){
								bindingvariables.add(slicevariable);
							}
							if(canAdd(element.bindingvariables,slicevariable)){
								element.bindingvariables.add(slicevariable);
							}
						}
						return super.visit(nameNode);
					}

				});
				
				Statement nodebody = node.getBody();
				
				//将条件表达式中得variable slicing 加入到所有条件下的语句中
				if(nodebody!=null)
					bindSubASTNodeWithSlicingVariable(LElementFactory,nodebody,conditionslicingvariables);
				
				return super.visit(node);
			}
			@Override
			public boolean visit(EnhancedForStatement node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(EnumConstantDeclaration node) {
				// TODO Auto-generated method stub
				IVariableBinding binding = node.resolveVariable();
				LElement element = visitFieldOrVariable(LElementFactory, node, binding);
				if(element!=null){
					SlicingVariable slicevariable = new SlicingVariable(element);
					if(canAdd(bindingvariables,slicevariable)){
						bindingvariables.add(slicevariable);
					}
					if(canAdd(element.bindingvariables,slicevariable)){
						element.bindingvariables.add(slicevariable);
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(EnumDeclaration node) {				
				// TODO Auto-generated method stub

				return super.visit(node);
			}

			@Override
			public boolean visit(ExpressionMethodReference node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(ExpressionStatement node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(FieldAccess node) {
				// TODO Auto-generated method stub
				//获取访问的 field
				IVariableBinding variableBinding = ((FieldAccess)node).resolveFieldBinding();
				LElement element = visitFieldOrVariable(LElementFactory, node, variableBinding);
				if(element!=null){
					SlicingVariable slicevariable = new SlicingVariable(element);
					if(canAdd(bindingvariables,slicevariable)){
						bindingvariables.add(slicevariable);
						
					}
					if(canAdd(element.bindingvariables,slicevariable)){
						element.bindingvariables.add(slicevariable);
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(FieldDeclaration node) {
				// TODO Auto-generated method stub
				FieldDeclaration fielddecl = (FieldDeclaration)node;
				List<VariableDeclarationFragment> fieldfragments = fielddecl.fragments();
				
				for(VariableDeclarationFragment field:fieldfragments){
					IVariableBinding binding = field.resolveBinding();
					LElement element = visitFieldOrVariable(LElementFactory,field, binding);
					if(element!=null){
						SlicingVariable slicevariable = new SlicingVariable(element);
						if(canAdd(bindingvariables,slicevariable)){
							bindingvariables.add(slicevariable);
						}
						if(canAdd(element.bindingvariables,slicevariable)){
							element.bindingvariables.add(slicevariable);
						}
					}
				}
				
				
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
				
				// 获得While Expression中的内容
				Expression expression = node.getExpression();
				final Set<SlicingVariable>conditionslicingvariables = new HashSet<SlicingVariable>();
				
				conditionslicingvariables.clear();
				// 获得条件内容
				expression.accept(new ASTVisitor() {

					public boolean visit(SimpleName nameNode) {

						LElement element = findDeclarationForFieldorVariable(LElementFactory, nameNode);
						if(element!=null){
							SlicingVariable slicevariable = new SlicingVariable(element);
							conditionslicingvariables.add(slicevariable);
							if(canAdd(bindingvariables,slicevariable)){
								bindingvariables.add(slicevariable);
								
							}
							if(canAdd(element.bindingvariables,slicevariable)){
								element.bindingvariables.add(slicevariable);
							}
						}
						return super.visit(nameNode);
					}

				});
				
				Statement thenbody = node.getThenStatement();
				
				//将条件表达式中得variable slicing 加入到所有条件下的语句中
				if(thenbody!=null)
					bindSubASTNodeWithSlicingVariable(LElementFactory,thenbody,conditionslicingvariables);
				
				Statement elsebody = node.getElseStatement();
				
				//将条件表达式中得variable slicing 加入到所有条件下的语句中
				if(elsebody!=null)
					bindSubASTNodeWithSlicingVariable(LElementFactory,elsebody,conditionslicingvariables);
				
				return super.visit(node);
			}

			@Override
			public boolean visit(Initializer node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(InstanceofExpression node) {
				// TODO Auto-generated method stub
				final ASTNode curNode = node;
				node.getLeftOperand().accept(new ASTVisitor() {

					public boolean visit(SimpleName nameNode) {

						LElement element = findDeclarationForFieldorVariable(LElementFactory, nameNode);
						if(element!=null){
							SlicingVariable slicevariable = new SlicingVariable(element);
							if(canAdd(bindingvariables,slicevariable)){
								bindingvariables.add(slicevariable);
							}
							if(canAdd(element.bindingvariables,slicevariable)){
								element.bindingvariables.add(slicevariable);
							}
						}
						return super.visit(nameNode);
					}

				});
				return super.visit(node);
			}

			

			@Override
			public boolean visit(MethodRef node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodRefParameter node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				// TODO Auto-generated method stub
				IMethodBinding binding = node.resolveBinding();
				if (binding != null) {
					LElement curMethod = LElementFactory.getElement(binding);
					node.accept(new ASTVisitor(){
						public boolean visit(SimpleName nameNode) {
							LElement declarenameNode = findDeclarationForFieldorVariable(LElementFactory, nameNode);
							if(declarenameNode!=null){
								SlicingVariable slicevariable = new SlicingVariable(declarenameNode);
								if(canAdd(bindingvariables,slicevariable)){
									bindingvariables.add(slicevariable);
								}
								if(canAdd(declarenameNode.bindingvariables,slicevariable)){
									declarenameNode.bindingvariables.add(slicevariable);
								}
							}
							return super.visit(nameNode);
						}
					});
					//createInherritedAndOverriddenMethodRelations(binding);

				}
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodInvocation node) {
				// TODO Auto-generated method stub
				IMethodBinding binding = node.resolveMethodBinding();
				List<Expression> arguements = node.arguments();
				handleMethodCall(LElementFactory,node,binding,arguements);
				
				return super.visit(node);
			}

			
			@Override
			public boolean visit(ParameterizedType node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			
			@Override
			public boolean visit(QualifiedName node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}


			@Override
			public boolean visit(ReturnStatement node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(SimpleName node) {
				// TODO Auto-generated method stub
				LElement declarenameNode = findDeclarationForFieldorVariable(LElementFactory, node);
				if(declarenameNode!=null){
					SlicingVariable slicevariable = new SlicingVariable(declarenameNode);
					if(canAdd(bindingvariables,slicevariable)){
						bindingvariables.add(slicevariable);
					}
					if(canAdd(declarenameNode.bindingvariables,slicevariable)){
						declarenameNode.bindingvariables.add(slicevariable);
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(SimpleType node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			

			@Override
			public boolean visit(SingleVariableDeclaration node) {
				// TODO Auto-generated method stub
				IVariableBinding binding = node.resolveBinding();
				node.accept(new ASTVisitor(){
					public boolean visit(SimpleName nameNode) {
						LElement element = findDeclarationForFieldorVariable(LElementFactory, nameNode);
						if(element!=null){
							SlicingVariable slicevariable = new SlicingVariable(element);
							if(canAdd(bindingvariables,slicevariable)){
								bindingvariables.add(slicevariable);
							}
							if(canAdd(element.bindingvariables,slicevariable)){
								element.bindingvariables.add(slicevariable);
							}
						}
						return super.visit(nameNode);
					}
				});
				return super.visit(node);
			}

			@Override
			public boolean visit(StringLiteral node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(SuperConstructorInvocation node) {
				// TODO Auto-generated method stub
				IMethodBinding binding = node.resolveConstructorBinding();
				List args = node.arguments();
				handleMethodCall(LElementFactory,node, binding, args);
				return super.visit(node);
			}

			@Override
			public boolean visit(SuperFieldAccess node) {
				// TODO Auto-generated method stub
				
				return super.visit(node);
			}

			@Override
			public boolean visit(SuperMethodInvocation node) {
				// TODO Auto-generated method stub
				IMethodBinding binding = node.resolveMethodBinding();
				List args = node.arguments();
				handleMethodCall(LElementFactory,node, binding, args);
				return super.visit(node);
			}

			@Override
			public boolean visit(SuperMethodReference node) {
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
				Expression expression = node.getExpression();
				List<Statement> casestatements = node.statements();
				//对于每一个case statement 加入在expression中绑定的变量
				final Set<SlicingVariable>conditionslicingvariables = new HashSet<SlicingVariable>();
				
				conditionslicingvariables.clear();
				// 获得条件内容
				expression.accept(new ASTVisitor() {

					public boolean visit(SimpleName nameNode) {

						LElement element = findDeclarationForFieldorVariable(LElementFactory, nameNode);
						if(element!=null){
							SlicingVariable slicevariable = new SlicingVariable(element);
							conditionslicingvariables.add(slicevariable);
							if(canAdd(bindingvariables,slicevariable)){
								bindingvariables.add(slicevariable);
								
							}
							
						}
						return super.visit(nameNode);
					}

				});

				for(Statement casestatement:casestatements){
					//将条件表达式中得variable slicing 加入到所有条件下的语句中
					bindSubASTNodeWithSlicingVariable(LElementFactory,casestatement,conditionslicingvariables);
				}
				
				return super.visit(node);
			}

			@Override
			public boolean visit(SynchronizedStatement node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}


			@Override
			public boolean visit(TypeDeclaration node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(TypeDeclarationStatement node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(TypeLiteral node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(TypeMethodReference node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}

			@Override
			public boolean visit(TypeParameter node) {
				// TODO Auto-generated method stub
				return super.visit(node);
			}


			@Override
			public boolean visit(VariableDeclarationExpression node) {
				// TODO Auto-generated method stub
				List<VariableDeclarationFragment> fragment = ((VariableDeclarationExpression)node).fragments();
				for(VariableDeclarationFragment varifragment:fragment){
					IVariableBinding binding = varifragment.resolveBinding();
					LElement element = visitFieldOrVariable(LElementFactory,varifragment, binding);
					if(element!=null){
						SlicingVariable slicevariable = new SlicingVariable(element);
						if(canAdd(bindingvariables,slicevariable)){
							bindingvariables.add(slicevariable);
						}
						if(canAdd(element.bindingvariables,slicevariable)){
							element.bindingvariables.add(slicevariable);
						}
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(VariableDeclarationStatement node) {
				// TODO Auto-generated method stub
				List<VariableDeclarationFragment> fragment = ((VariableDeclarationStatement)node).fragments();
				for(VariableDeclarationFragment varifragment:fragment){
					IVariableBinding binding = varifragment.resolveBinding();
					LElement element = visitFieldOrVariable(LElementFactory,varifragment, binding);
					if(element!=null){
						SlicingVariable slicevariable = new SlicingVariable(element);
						if(canAdd(bindingvariables,slicevariable)){
							bindingvariables.add(slicevariable);
						}
						if(canAdd(element.bindingvariables,slicevariable)){
							element.bindingvariables.add(slicevariable);
						}
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(VariableDeclarationFragment node) {
				// TODO Auto-generated method stub
				IVariableBinding binding = node.resolveBinding();
				LElement element = visitFieldOrVariable(LElementFactory,node, binding);
				if(element!=null){
					SlicingVariable slicevariable = new SlicingVariable(element);
					if(canAdd(bindingvariables,slicevariable)){
						bindingvariables.add(slicevariable);
					}
					if(canAdd(element.bindingvariables,slicevariable)){
						element.bindingvariables.add(slicevariable);
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(WhileStatement node) {
				// TODO Auto-generated method stub
				
				// 获得While Expression中的内容
				Expression expression = node.getExpression();
				final Set<SlicingVariable>conditionslicingvariables = new HashSet<SlicingVariable>();
				conditionslicingvariables.clear();
				// 获得条件内容
				expression.accept(new ASTVisitor() {
					public boolean visit(SimpleName nameNode) {
						LElement element = findDeclarationForFieldorVariable(LElementFactory, nameNode);
						if(element!=null){
							SlicingVariable slicevariable = new SlicingVariable(element);
							conditionslicingvariables.add(slicevariable);
							if(canAdd(bindingvariables,slicevariable)){
								bindingvariables.add(slicevariable);
								
							}
							
						}
						return super.visit(nameNode);
					}

				});
				
				Statement whilebody = node.getBody();
				
				//将条件表达式中得variable slicing 加入到所有条件下的语句中
				bindSubASTNodeWithSlicingVariable(LElementFactory,whilebody,conditionslicingvariables);
				
				return super.visit(node);
			}

		});
		if(debug){
			System.out.println("-------------------------------------------");
		}
		
	}
	
	protected void handleMethodCall(LFlyweightElementFactory LElementFactory,ASTNode node,IMethodBinding binding, List args) {
		// TODO Auto-generated method stub
		/*
		 * a->b
		 * For a call statement from caller [a] to method [b]
		 * 
		 */
		if (binding != null) {
			final LFlyweightElementFactory final_LElementFactory=LElementFactory;
			/*
			 * 第一个部分 将当前 a->b中a中定义的 参数 加入到 当前的 bindingvariable中
			 */
			final LElement targetMethodElement = LElementFactory.getElement(binding);
			final LElement callSite = LElementFactory.getElement(node);
			for (int i = 0; i < args.size(); i++) {
				final ASTNode tmpArg = (ASTNode) args.get(i);
				tmpArg.accept(new ASTVisitor(){
					public boolean visit(SimpleName nameNode) {
						LElement declarenameNode = findDeclarationForFieldorVariable(final_LElementFactory,nameNode);
						if(declarenameNode!=null){
							SlicingVariable slicevariable = new SlicingVariable(declarenameNode);
							if(canAdd(bindingvariables,slicevariable)){
								bindingvariables.add(slicevariable);
								
							}
							if(canAdd(declarenameNode.bindingvariables,slicevariable)){
								declarenameNode.bindingvariables.add(slicevariable);
							}
							/*
							 * 从A 到 B的方法调用 
							 * 那么将参数也要加入到B中
							 */
							if(targetMethodElement!=null){//如果是null,则是API函数
								if(targetMethodElement.canAdd(targetMethodElement.getBindSlicingVariable(), slicevariable)){
									targetMethodElement.getBindSlicingVariable().add(slicevariable);
								}
							}
						}
						return super.visit(nameNode);
					}
				});
			}
			
			if(targetMethodElement!=null){
				for(SlicingVariable variable:callSite.getBindSlicingVariable()){
					if(targetMethodElement.canAdd(targetMethodElement.getBindSlicingVariable(), variable)){
						targetMethodElement.getBindSlicingVariable().add(variable);
					}
				}
			}
			
		}
	}

	

	/**
	 * This method must be redeclared here for compatibility
	 * with the IElement interface.  Returns the category of the element 
	 * within the general model.
	 * @return An int representing the category of the element.
	 * @see ca.ubc.cs.javadb.model.IElement#getCategory()
	 */
	public LICategories getCategory(){
		return acategory;
	}
	
	/**
	 * This method must be redeclared here for compatibility
	 * with the IElement interface.  Returns the unique (fully qualified)
	 * name of the element.
	 * @return A String representing the fully qualified name of the
	 * element.
	 * @see ca.ubc.cs.javadb.model.IElement#getId()
	 */
	
	public String getId()
	{
		return aId;
	}
	
	/** 
	 * Returns a String representation of the element.
	 * @return The element's ID.
	 */
	public String toString()
	{
		return getId();
	}
	
	


	public ASTNode getASTNode(){
		return aastNode;
	}

	public  Set<Feature> getAssociatedFeatures(){
		return aColorManager.getOwnColors(aastNode);
	}
	

	public void addSubcategory(LICategories pCategory) {
		// TODO Auto-generated method stub
		subcategories.add(pCategory);
	}

	/**
	 * only for ALocalVariableElement
	 */
	public int getParamIndex() {
		return paramIndex;
	}

	/**
	 * only for ALocalVariableElement
	 */
	public void setParamIndex(int paramIndex) {
		this.paramIndex = paramIndex;
	}
	
	//DEBUG
	public String getCompilationUnitName(){
		String name = aColorSourceFile.getColorFile().getName();
		return aColorSourceFile.getColorFile().getName().substring(0, name.length()-"clr".length());
	}

	public Set<LICategories> getSubCategories() {
		// TODO Auto-generated method stub
		return subcategories;
	}

	public CLRAnnotatedSourceFile getCLRFile() {
		// TODO Auto-generated method stub
		return aColorSourceFile;
	}
	
	/**
	 * 计算先验概率 当前节点是条件 resultelement是结果
	 */
	public void setPrior_Probability(LElement resultelement,double probability){
		if(!priorprobability.containsKey(resultelement)){
			priorprobability.put(resultelement, probability);
		}
	}
	
	public boolean hasPrior_Probability(LElement forwardelement){
		if(priorprobability.containsKey(forwardelement))
			return true;
		else
			return false;
	}
	
	/**
	 * 计算后验概率 假定条件是 evidenceelement
	 */
	public void setPosteriori_Probability(LElement evidenceelement,double probability){
		if(!posterioriprobability.containsKey(evidenceelement)){
			posterioriprobability.put(evidenceelement, probability);
		}else{
			double existprobability = posterioriprobability.get(evidenceelement);
			posterioriprobability.put(evidenceelement, existprobability+probability);
		}
	}
	
	public double getPrior_Probability(LElement resultelement){
		if(!priorprobability.containsKey(resultelement)){
			return 0;
		}else{
			return priorprobability.get(resultelement);
		}
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof LElement){
			LElement leobj = (LElement)obj;
			if(this.aId == leobj.getId()){
				return true;
			}else
				return false;
		}else
			return false;
	}

	public Set<SlicingVariable> getBindSlicingVariable() {
		// TODO Auto-generated method stub
		Set<SlicingVariable> temp = new HashSet<SlicingVariable>();
		temp.addAll(bindingvariables);
		for(SlicingVariable slice:temp){
			Set<SlicingVariable> slicechildren = loadChildren(slice);
			for(SlicingVariable slicechid:slicechildren){
				if(canAdd(temp,slicechid)){
					temp.add(slicechid);
				}
			}
		}
		return temp;
	}
	public Set<SlicingVariable> getBindSlicingVariable(ASTNode node) {
		
		IBinding ibinding = null;
		if(node instanceof TypeDeclaration){
			TypeDeclaration typedecl = (TypeDeclaration)node;
			ibinding = typedecl.resolveBinding();
		}else if(node instanceof SimpleName){
			SimpleName typeName = (SimpleName)node;
			ibinding = typeName.resolveTypeBinding();
		}else if(node instanceof CompilationUnit){
			CompilationUnit typecompunit = (CompilationUnit)node;
			AbstractTypeDeclaration typeunit = (AbstractTypeDeclaration) typecompunit.types().get(0);
			ibinding = typeunit.resolveBinding();
		}else if(node instanceof MethodDeclaration){
			MethodDeclaration methoddecl = (MethodDeclaration)node;
			ibinding = methoddecl.resolveBinding();
		}else if(node instanceof ArrayAccess){
			ArrayAccess arrayaccessnode = (ArrayAccess)node;
			ibinding = arrayaccessnode.resolveTypeBinding();
		}else if(node instanceof ArrayCreation){
			ArrayCreation arraycreation = (ArrayCreation)node;
			ibinding = arraycreation.resolveTypeBinding();
		}else if(node instanceof ArrayInitializer){
			ArrayInitializer arrayinit = (ArrayInitializer)node;
			ibinding = arrayinit.resolveTypeBinding();
		}else if(node instanceof FieldAccess){
			FieldAccess fieldaccess = (FieldAccess)node;
			ibinding = fieldaccess.resolveFieldBinding();
		}else if(node instanceof MethodInvocation){
			MethodInvocation methodinvok = (MethodInvocation)node;
			ibinding = methodinvok.resolveMethodBinding();
		}else if(node instanceof SimpleType){
			SimpleType type = (SimpleType)node;
			ibinding = type.resolveBinding();
		}
		
		
		
		assert ibinding!=null;
		
		Set<SlicingVariable> temp = new HashSet<SlicingVariable>();
		temp.addAll(bindingvariables);
		
		for(SlicingVariable slice:temp){
			if(slice.getBinding()!=null){
				if(!slice.getBinding().equals(ibinding)){
					continue;
				}
			}else{
				continue;
			}
			
			Set<SlicingVariable> slicechildren = loadChildren(slice);
			for(SlicingVariable slicechid:slicechildren){
				if(canAdd(temp,slicechid)){
					temp.add(slicechid);
				}
			}
		}
		return temp;
	}

	private Set<SlicingVariable> loadChildren(SlicingVariable slice) {
		// TODO Auto-generated method stub
		/**
		 * 加入对于 类的约束 条件 在入口处
		 */
		Set<SlicingVariable> childrentmps = slice.getSlicingChildren();
		return childrentmps;
	}

	public void setProbability(LElement element,double dprobability) {
		// TODO Auto-generated method stub
		if(element==null){
			probability = dprobability;
			return;
		}else if(priorLElements.contains(element)){
			return;
		}else{
			probability += dprobability;
			priorLElements.add(element);
		}
	}
	
	public void setIMethod(IMethod imethod){
		if(aastNode instanceof MethodDeclaration){
			this.bindMethod = imethod;
		}
	}
	public IMethod getIMethod(){
		if(aastNode instanceof MethodDeclaration){
			return bindMethod;
		}
		return null;
	}

	public double getProbability() {
		// TODO Auto-generated method stub
		return probability;
	}
	
	public CompilationUnit getCompilationUnit(){
		try {
			return aColorSourceFile.getAST();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getASTID(){
		return ASTID.calculateId(aastNode);
	}

	public Map<IBinding, Set<ASTNode>> computeIBindingASTs() {
		// TODO Auto-generated method stub
		if(bindingLElements.isEmpty()){
			bindingLElements = EmbeddedASTNodeCollector.collectBindingASTNodes(aastNode);
			return bindingLElements;
		}else
			return bindingLElements;
	}
	
}
