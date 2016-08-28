package loongpluginsartool.bow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LRelation;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class ASTNodeDictionary {
	/**
	 * FieldDeclaration
	 * MethodDeclaration
	 * CLassDeclaration
	 * InterfaceDeclaration
	 * CompilationUnit
	 * EnumConstant
	 * Type
	 */
	public static ArrayList<LElement>dictionary = new ArrayList<LElement>();
	
	
	public ASTNodeDictionary(){
		dictionary = new ArrayList<LElement>();
	}
	
	/**
	 * add a specific astnode to dictionary
	 * @param node
	 */
	public static void addToDict(LElement element){
		ASTNode node = element.getASTNode();
		if(node instanceof FieldDeclaration){
			dictionary.add(element);
		}else if(node instanceof EnumConstantDeclaration){
			dictionary.add(element);
		}else if(node instanceof MethodDeclaration){
			dictionary.add(element);
		}else if(node instanceof PackageDeclaration){
			dictionary.add(element);
		}else if(node instanceof CompilationUnit){
			dictionary.add(element);
		}else if(node instanceof TypeDeclaration){
			dictionary.add(element);
		}
	}
	
	public static boolean isValidDictElement(LElement element){
		ASTNode node = element.getASTNode();
		if(node instanceof FieldDeclaration||
				node instanceof EnumConstantDeclaration||
				node instanceof MethodDeclaration||
				node instanceof PackageDeclaration||
				node instanceof CompilationUnit||
				node instanceof TypeDeclaration){
			return true;
		}else 
			return false;
	}
	public static Vector normalizeToVectorBoW(LElement element,ApplicationObserver AOB){
		Vector node_vector = new Vector();
		for(int i = 0;i < dictionary.size();i++){
			node_vector.add(i, 0);
		}
		// all relation backward and forward
		
		Set<LRelation> validTransponseRelations = LRelation.getAllRelations(element.getCategory(), true, false);
		validTransponseRelations.addAll(LRelation.getAllRelations(element.getCategory(), true, true));
	
		for (LRelation tmpTransRelation : validTransponseRelations) {
			Set<LElement> forwardElements = new HashSet<LElement>();
			forwardElements = AOB.getRange(element,tmpTransRelation);
			for(LElement forwardelement:forwardElements){
				if(dictionary.contains(forwardelement)){
					int index = dictionary.indexOf(forwardelement);
					int count = (int) node_vector.get(index);
					node_vector.set(index, count+1);
				}
			}
		}
		
		Set<LRelation> inversevalidTransponseRelations = LRelation.getAllRelations(element.getCategory(), false, false);
		inversevalidTransponseRelations.addAll(LRelation.getAllRelations(element.getCategory(), false, true));
		for(LRelation tmpinverseTransRelation:inversevalidTransponseRelations){
			Set<LElement> backwardElements = new HashSet<LElement>();
			backwardElements = AOB.getRange(element,tmpinverseTransRelation);
			for(LElement backwardelement:backwardElements){
				if(dictionary.contains(backwardelement)){
					int index = dictionary.indexOf(backwardelement);
					int count = (int) node_vector.get(index);
					node_vector.set(index, count+1);
				}
			}
		}
		
		return node_vector;
	}
	
}
