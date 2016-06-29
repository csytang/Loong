package loongplugin.configfeaturemodeleditor.serializer;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import loongplugin.configfeaturemodeleditor.model.AbstractModel;
import loongplugin.uml.model.RootModel;



public class DiagramSerializer {
	
	public static InputStream serialize(AbstractModel model) throws UnsupportedEncodingException {
		return XStreamSerializer.serializeStream(model, DiagramSerializer.class.getClassLoader());
	}
	
	public static AbstractModel deserialize(InputStream in) throws UnsupportedEncodingException {
		return (AbstractModel)XStreamSerializer.deserialize(in, DiagramSerializer.class.getClassLoader());
	}
}
