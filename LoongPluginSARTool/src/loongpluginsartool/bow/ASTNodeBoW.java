package loongpluginsartool.bow;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;

import org.eclipse.jdt.core.dom.ASTNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ASTNodeBoW {
	/**
	 * Create ASTNode based Bag of Word
	 * The BoW in ASTNode 
	 */
	private LElement element;
	private ASTNode node;
	private Vector vector;
	public ASTNodeBoW(LElement pelement){
		this.element = pelement;
		this.node = this.element.getASTNode();
		ASTNodeDictionary.addToDict(this.element);
	}
	
	/**
	 * create bag of word
	 */
	public void createBagofWord(ApplicationObserver AOB){
		this.vector = ASTNodeDictionary.normalizeToVectorBoW(this.element, AOB);
	}
	
	public Vector getWordVect(){
		return this.vector;
	}
	
}
