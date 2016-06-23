package loongplugin.featuremodeleditor.event;

import java.util.EventObject;

import loongplugin.color.ColorManager;
import loongplugin.feature.FeatureModel;

public class FeatureModelChangedEvent extends EventObject{
	
	private final FeatureModel afeaturemodel;
	private final long atimeHash;
	public FeatureModelChangedEvent(Object source,FeatureModel pfeaturemodel,long ptimeHash) {
		super(source);
		// TODO Auto-generated constructor stub
		afeaturemodel = pfeaturemodel;
		atimeHash = ptimeHash;
	}
	
	public FeatureModel getFeatureModel(){
		return afeaturemodel;
	}
	
	public long getTimeHash(){
		return atimeHash;
	}
	
}
