package loongpluginsartool.toolbox.acdc;
import javax.swing.tree.DefaultMutableTreeNode;

public interface OutputHandler
{
	public void writeOutput(String outputName, DefaultMutableTreeNode root);
}
