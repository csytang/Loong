package loongpluginfmrtool.module.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;

public class ConfigurationOption {
	public Expression aconfigOption;
	private Map<ConfigurationOption,ConfigRelation> aconfigOpToRelation;
	private Set<Statement>select_statement = new HashSet<Statement>();
	private Set<Statement>unselect_statement = new HashSet<Statement>();
	private Set<ASTNode>affected_astnodes = new HashSet<ASTNode>();
	public ConfigurationOption(Expression pconfigOption){
		this.aconfigOption = pconfigOption;
		this.aconfigOpToRelation = new HashMap<ConfigurationOption,ConfigRelation>();
	}
	
	public void addConfiguration(ConfigurationOption option,ConfigRelation relation){
		aconfigOpToRelation.put(option,relation);
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
		if(this.aconfigOpToRelation.containsKey(config)){
			return this.aconfigOpToRelation.get(config);
		}else{
			return ConfigRelation.UNRELATE;
		}
	}
	
	public void addConfigRelation(ConfigurationOption config,ConfigRelation relation){
		this.aconfigOpToRelation.put(config, relation);
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
	
}
