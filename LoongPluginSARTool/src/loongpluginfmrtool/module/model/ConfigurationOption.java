package loongpluginfmrtool.module.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;

public class ConfigurationOption extends ModuleComponent{
	public Expression aconfigOption;
	private Map<ConfigurationOption,ConfigRelation> internalconfigOpToRelation;
	private Set<Statement>select_statement = new HashSet<Statement>();
	private Set<Statement>unselect_statement = new HashSet<Statement>();
	private Set<ASTNode>affected_astnodes = new HashSet<ASTNode>();
	private Module associatedmodule;
	private CompilationUnit unit;
	public ConfigurationOption(Module passociatedmodule){
		super(passociatedmodule);
		unit = passociatedmodule.getCompilationUnit();
	}
	public ConfigurationOption(Expression pconfigOption,Module passociatedmodule){
		super(passociatedmodule);
		this.aconfigOption = pconfigOption;
		this.associatedmodule = passociatedmodule;
		this.internalconfigOpToRelation = new HashMap<ConfigurationOption,ConfigRelation>();
		unit = passociatedmodule.getCompilationUnit();
	}
	
	public void addConfiguration(ConfigurationOption option,ConfigRelation relation){
		internalconfigOpToRelation.put(option,relation);
	}
	
	public void addEnable_Statements(Statement element){
		select_statement.add(element);
		// 也计入到影响的 astnode部分
		affected_astnodes.add(element);
	}
	
	public void addAffected_ASTNode(ASTNode node){
		affected_astnodes.add(node);
	}
	
	public void addDisable_Statements(Statement element){
		unselect_statement.add(element);
		// 也计入到影响的 astnode部分
		affected_astnodes.add(element);
	}
	
	public ConfigRelation getConfigRelation(ConfigurationOption config){
		if(this.internalconfigOpToRelation.containsKey(config)){
			return this.internalconfigOpToRelation.get(config);
		}else{
			return ConfigRelation.UNRELATE;
		}
	}
	
	public void addConfigRelation(ConfigurationOption config,ConfigRelation relation){
		this.internalconfigOpToRelation.put(config, relation);
	}
	
	public Set<Statement> getEnable_Statements(){
		return select_statement;
	}
	
	public Set<Statement> getDisable_Statements(){
		return unselect_statement;
	}

	public Expression getExpression() {
		// TODO Auto-generated method stub
		return aconfigOption;
	}

	public Set<ASTNode> getAffectedASTNodes() {
		// TODO Auto-generated method stub
		return affected_astnodes;
	}
	
	public Module getAssociatedModule(){
		return associatedmodule;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return aconfigOption.toString();
	}
	public String getAffectedASTNodesRange() {
		// TODO Auto-generated method stub
		Set<Integer>selected_range = new HashSet<Integer>();
		for(Statement statement:select_statement){
			int start = unit.getLineNumber(statement.getStartPosition());
			int end = unit.getLineNumber(statement.getStartPosition()+statement.getLength());
			for(int i = start;i<=end;i++){
				selected_range.add(i);
			}
			
		}
		int []sorted  = new int[selected_range.size()];
		int index = 0;
		for(Integer it:selected_range){
			sorted[index] = it;
			index++;
		}
		
		Arrays.sort(sorted);
		String selected = "selected:[";
		for(int i = 0;i < sorted.length;i++){
			selected+= sorted[i];
			selected+= ",";
		}
		if(sorted.length!=0)
			selected = selected.substring(0, selected.length()-1);
		selected+="]";
		
		Set<Integer>unselected_range = new HashSet<Integer>();
		for(Statement statement:unselect_statement){
			int start = unit.getLineNumber(statement.getStartPosition());
			int end = unit.getLineNumber(statement.getStartPosition()+statement.getLength());
			for(int i = start;i<=end;i++){
				unselected_range.add(i);
			}
		}
		
		int []unsele_sorted  = new int[unselected_range.size()];
		index = 0;
		for(Integer it:unselected_range){
			unsele_sorted[index] = it;
			index++;
		}
		
		Arrays.sort(unsele_sorted);
		
		String unselected = "unselected:[";
		for(int i = 0;i < unsele_sorted.length;i++){
			unselected+= unsele_sorted[i];
			unselected+= ",";
		}
		if(unsele_sorted.length!=0)
			unselected = unselected.substring(0, unselected.length()-1);
		unselected+="]";
		
		return selected+unselected;
	}
	
	
	
}
