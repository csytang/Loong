package loongplugin.word2vec;

import java.io.File;
import java.util.Map;

public class word2vecUtil {
	// 声明自定义本地库方法接口
    native public static void word2vec(int argc,String[] argv);
    native public static Map<String,Float> distance(int argc,String[] argv,String inputstr);
    // 
    static{
    	
    	String s = word2vecUtil.class.getName();
    	int i = s.lastIndexOf(".");
    	if(i > -1) s = s.substring(i + 1);
    	s = s + ".class";
    	String projectPath = word2vecUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    	projectPath += "jnilibs/"; 
    	String libLocationDis = projectPath+"libdistance.jnilib";
    	String libLocationWord2vec = projectPath+"libword2vec.jnilib";
    	
    	System.load(libLocationDis);
    	System.load(libLocationWord2vec);
    }
    
    public word2vecUtil(String filePath,String fileoutPath){
    	
    	String[] argv = {"word2vec",
				"-train",filePath,
				"-output",fileoutPath,
				"-cbow","1",
				"-size","200",
				"-window","8",
				"-negative","25",
				"-hs","0",
				"-sample","1e-4",
				"-threads","20",
				"-binary","1",
				"-iter","15"
		};
		int size = argv.length;
		word2vec(size,argv);
		String[] argv2 ={"distance",fileoutPath};
		int sizeargv2 = argv2.length;
		//distance(sizeargv2,argv2);
    }
}
