package loongpluginsartool.architecturerecovery.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.*;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LICategories;
import loongpluginsartool.bow.ASTNodeBoW;
import loongpluginsartool.bow.ASTNodeDictionary;

public class JavaRecoverGen {
	private ApplicationObserver observer;
	private ProgramDatabase pdb;
	private Set<LElement> allelements;
	private Map<LElement,ASTNodeBoW> elementToBoW = new HashMap<LElement,ASTNodeBoW>();
	private boolean debug = true;
	public JavaRecoverGen(ApplicationObserver pobserver){
		this.observer = pobserver;
		this.pdb = pobserver.getProgramDatabase();
		this.allelements = new HashSet<LElement>();
	}
	
	public void BeginRecovery(){
		allelements = this.pdb.getAllElements();
		// 创建BoW
		for(LElement element:allelements){
			if(ASTNodeDictionary.isValidDictElement(element)){
				ASTNodeBoW astbow = new ASTNodeBoW(element);
				elementToBoW.put(element, astbow);
			}
			
		}
		for(LElement element:elementToBoW.keySet()){
			ASTNodeBoW astbow = elementToBoW.get(element);
			astbow.createBagofWord(observer);
			// DEBUG
			if(debug){
				System.out.println(element.getASTID()+"\t"+astbow.getWordVect());
			}
			// FINISH
		}
		
		//
	}
	
}
