package loongpluginfmrtool.module.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class CongVisitor extends ASTVisitor{

	private Set<Expression> conditionalExpressions = new HashSet<Expression>();
	private Map<Expression,Statement> selectedStatements = new HashMap<Expression,Statement>();
	private Map<Expression,Statement> unselectedStatements = new HashMap<Expression,Statement>();
	
	public boolean visit(IfStatement node) {
		IfStatement if_node = (IfStatement)node;
		Expression condition = if_node.getExpression();
		Statement then_statement = if_node.getThenStatement();
		Statement else_statement = if_node.getElseStatement();
		if(then_statement!=null){
			conditionalExpressions.add(condition);
			selectedStatements.put(condition, then_statement);
		}
		if(else_statement!=null){
			conditionalExpressions.add(condition);
			unselectedStatements.put(condition, else_statement);
		}
		
		return true;
    }

    public boolean visit(WhileStatement node) {
    	WhileStatement while_statement = (WhileStatement)node;
    	Expression condition = while_statement.getExpression();
    	Statement body = while_statement.getBody();
    	if(body!=null){
    		conditionalExpressions.add(condition);
    		selectedStatements.put(condition, body);
    	}
    	return true;
    }

    
    @Override 
    public boolean visit(DoStatement node) {
    	DoStatement do_statement = (DoStatement) node;
    	Expression condition = do_statement.getExpression();
    	Statement body = do_statement.getBody();
    	if(body!=null){
    		conditionalExpressions.add(condition);
    		selectedStatements.put(condition, body);
    	}
    	
        return true;
	}
    
	@Override 
	public boolean visit(EnhancedForStatement node) {
		EnhancedForStatement enhance_statement = (EnhancedForStatement)node;
		Expression condition = enhance_statement.getExpression();
		Statement body = enhance_statement.getBody();
		if(body!=null){
    		conditionalExpressions.add(condition);
    		selectedStatements.put(condition, body);
    	}
        return true;
	}
	
	@Override 
	public boolean visit(ForStatement node) {
		ForStatement for_statement  = (ForStatement)node;
		Expression condition = for_statement.getExpression();
		Statement body = for_statement.getBody();
		if(body!=null){
			conditionalExpressions.add(condition);
    		selectedStatements.put(condition, body);
		}
        return true;
	}
	
	/** TODO: handle <i>continue</i>*/
	@Override 
	public boolean visit(SwitchStatement node) {
		SwitchStatement switch_node = (SwitchStatement)node;
		Expression condition = switch_node.getExpression();
		return true;
	}

	
	@Override 
	public boolean visit(TryStatement node) {
		
		return true;
	}   
	
	@Override
	public boolean visit(CatchClause node) {
		
		return super.visit(node);
	}
	
	
	
	
	
}
