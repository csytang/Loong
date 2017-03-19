package loongplugin.recommendation;

public interface IMiningStrategy {
	
	
	/**
	 * get name of this strategy
	 */
	public String getName();
	
	/**
	 * whether current strategy is active 
	 */
	public boolean isActiveStrategy();
	
	/**
	 * set current strategy as active strategy
	 */
	
	public void setCurrentAsActiveStrategy();
	
	
}
