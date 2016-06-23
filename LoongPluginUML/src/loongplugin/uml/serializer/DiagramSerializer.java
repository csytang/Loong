package loongplugin.uml.serializer;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import loongplugin.uml.model.RootModel;



public class DiagramSerializer {
	
	public static InputStream serialize(RootModel model) throws UnsupportedEncodingException {
		return XStreamSerializer.serializeStream(model, DiagramSerializer.class.getClassLoader());
	}
	
	public static RootModel deserialize(InputStream in) throws UnsupportedEncodingException {
		return (RootModel)XStreamSerializer.deserialize(in, DiagramSerializer.class.getClassLoader());
	}
}
