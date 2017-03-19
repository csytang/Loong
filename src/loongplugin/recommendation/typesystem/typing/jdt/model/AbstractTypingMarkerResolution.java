package loongplugin.recommendation.typesystem.typing.jdt.model;


public abstract class AbstractTypingMarkerResolution implements ITypingMarkerResolution {
	
	private int rel;

	public void setRelevance(int rel) {
		this.rel = rel;
	}

	public int getRelevance() {
		return rel;
	}

	public int compareTo(ITypingMarkerResolution o) {
		if (o.getRelevance() > this.getRelevance())
			return 1;
		if (o.getRelevance() < this.getRelevance())
			return -1;
		return 0;
	}


}
