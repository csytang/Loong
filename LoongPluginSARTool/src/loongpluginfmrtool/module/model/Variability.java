package loongpluginfmrtool.module.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Variability {
	/**
	 * 算一个varability
	 * 然后再算每一个configuration 的比重
	 */
	private Module module;
	private int totalValidConfig = 0;
	private Set<Configuration>configurations;
	private Set<ConfigurationOption>options;
	private ConfigurationOptionTree tree;
	private Queue<ConfigurationOption>option_queue;
	private Set<ConfigurationOption> roots;
	private Set<ConfigurationOption> visited = new HashSet<ConfigurationOption>();
	public Variability(Module pmodule){
		this.module = pmodule;
		this.configurations = new HashSet<Configuration>();
		this.options = this.module.getAllConfigurationOptions();
		option_queue = new LinkedList<ConfigurationOption>();
	}
	
	protected void Collect(ConfigurationOptionTree ptree){
		this.tree = ptree;
		this.roots = this.tree.getRoots();
		for(ConfigurationOption root:roots){
			option_queue.clear();
			option_queue.add(root);
			BFEncoding(option_queue);
		}
		totalValidConfig = options.size();
	}
	
	/**
	 * 从根节点到叶子节点
	 * @param queue
	 */
	protected void BFEncoding(Queue<ConfigurationOption>queue){
		while(!queue.isEmpty()){
			 ConfigurationOption option_top = queue.poll();
			 if(visited.contains(option_top)){
				 continue;
			 }
			 createAConfiguration(visited,option_top);
			 Set<ConfigurationOption>childrens = tree.getChildren(option_top);
			 if(childrens!=null){
				 for(ConfigurationOption sub_option:childrens){
					 if(!visited.contains(option_top)){
						 queue.add(sub_option);
					 }
				 }
			 }
			 visited.add(option_top);
			 if(queue.isEmpty()){
				 createAConfiguration(visited);
			 }
		}
	}

	private void createAConfiguration(Set<ConfigurationOption> visited) {
		// TODO Auto-generated method stub
		Map<ConfigurationOption,Boolean>pconfigurationlist = new HashMap<ConfigurationOption,Boolean>();
		for(ConfigurationOption option:visited){
			pconfigurationlist.put(option, true);
		}
		Configuration configuration = new Configuration(module,pconfigurationlist);
		this.configurations.add(configuration);
	}

	private void createAConfiguration(Set<ConfigurationOption> visited,
			ConfigurationOption option_top) {
		// TODO Auto-generated method stub
		Map<ConfigurationOption,Boolean>pconfigurationlist = new HashMap<ConfigurationOption,Boolean>();
		for(ConfigurationOption option:visited){
			pconfigurationlist.put(option, true);
		}
		pconfigurationlist.put(option_top, false);
		Configuration configuration = new Configuration(module,pconfigurationlist);
		this.configurations.add(configuration);
	}
	
	public boolean hasConflict(Variability othervariability){
		boolean conflict = false;
		for(Configuration config:configurations){
			for(Configuration other_config:othervariability.configurations){
				if(config.conflictwith(other_config)){
					return true;
				}
			}
		}
		
		return conflict;
	}

	public int getValidConfigurationCount() {
		// TODO Auto-generated method stub
		return totalValidConfig;
	}

	public Module getModule() {
		// TODO Auto-generated method stub
		return module;
	}
	
}
