package loongplugin.color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

import loongplugin.feature.Feature;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


public class ColorHelper {
	/**
	 * calculates the string representation as known from HTML
	 * 
	 * @param rgb
	 *            color
	 * @return string representation (7 characters)
	 */
	public static String rgb2str(RGB rgb) {
		if (rgb == null)
			return "#000000";

		String r = Integer.toHexString(rgb.red).toUpperCase();
		if (r.length() == 1)
			r = "0" + r;
		String g = Integer.toHexString(rgb.green).toUpperCase();
		if (g.length() == 1)
			g = "0" + g;
		String b = Integer.toHexString(rgb.blue).toUpperCase();
		if (b.length() == 1)
			b = "0" + b;
		return "#" + r + g + b;
	}
	
	
	
	
	public static List<Feature> sortFeatures(Collection<Feature> features) {
		List<Feature> result = new ArrayList<Feature>(features);
		Collections.sort(result);
		return result;
	}
}
