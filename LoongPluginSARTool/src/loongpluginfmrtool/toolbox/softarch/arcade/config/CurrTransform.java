package loongpluginfmrtool.toolbox.softarch.arcade.config;

import loongpluginfmrtool.toolbox.softarch.arcade.classgraphs.ClassGraphTransformer;
import loongpluginfmrtool.toolbox.softarch.arcade.config.datatypes.TransformOptions;
import loongpluginfmrtool.toolbox.softarch.arcade.fieldaccess.FieldAccessTransformer;
import soot.PackManager;
import soot.Transform;


/**
 * @author joshua
 *
 */
public class CurrTransform {
	public static TransformOptions transform = TransformOptions.ClassGraph;

	public static void selectTransformation() {
		if (transform
				.equals(TransformOptions.FieldAccess)) {
			PackManager.v().getPack("wjtp")
					.add(
							new Transform("wjtp.myTrans",
									new FieldAccessTransformer()));
		} else if (transform
				.equals(TransformOptions.ClassGraph)) {
			PackManager.v().getPack("wjtp").add(
					new Transform("wjtp.myTrans", new ClassGraphTransformer()));
		}
	}
}
