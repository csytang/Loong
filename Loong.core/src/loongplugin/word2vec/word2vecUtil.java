package loongplugin.word2vec;

import java.io.File;

public class word2vecUtil {
	// 声明自定义本地库方法接口
    native public static void word2vec(int argc,String[] argv);
    native public static void distance(int argc,String[] argv);
    // 
    static{
        System.load("./libword2vec.jnilib");
        System.load("./libdistance.jnilib");
    }
    public word2vecUtil(String filestring){
    	String[] argv = {"word2vec",
				"-train",filestring,
				"-output","vectors.log",
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
		
    }
}
