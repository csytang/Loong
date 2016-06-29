package loongplugin.nlp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.util.TypesafeMap.Key;

public class NLPTokenPipeline {
	/**
	 * A NLP module to process the token in source file and clustering
	 */
	
	private File fileInput;
	private String snapString;
	
	private List<String> snaplistUnified = new LinkedList<String>();
	enum INPUTCONTEXT{INPUTSTEAM,STRING};
	private INPUTCONTEXT inputContext;
	private Map<String,Integer> snapToOccurrences = new HashMap<String,Integer>();
	
	private StanfordCoreNLP pipeline;
	
	public NLPTokenPipeline(File pfileInput){
		this.fileInput = pfileInput;
		this.inputContext = INPUTCONTEXT.INPUTSTEAM;
		
	}
	
	public NLPTokenPipeline(String psnapString){
		this.snapString = psnapString;
		this.inputContext = INPUTCONTEXT.STRING;
		
	}
	
	
	
	
	
	public void split(){
		switch(this.inputContext){
			
			case INPUTSTEAM:{
				PTBTokenizer<CoreLabel> ptbt;
				try {
					ptbt = new PTBTokenizer<>(new FileReader(fileInput),new CoreLabelTokenFactory(), "");
					while(ptbt.hasNext()){
						CoreLabel label = ptbt.next();
						if(label.word()!=null)
							snaplistUnified.add(label.word());
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			}
			case STRING:{
				PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(snapString),new CoreLabelTokenFactory(), "");
				while(ptbt.hasNext()){
					CoreLabel label = ptbt.next();
					if(label.word()!=null)
						snaplistUnified.add(label.word());
				}
				break;
			}
		}
	}
	
	public void stemmingAndMerge(String text){
		Properties props = new Properties(); 
        props.put("annotators", "tokenize, ssplit, pos, lemma"); 
        pipeline = new StanfordCoreNLP(props, false);
        Annotation document = pipeline.process(text);  

        for(CoreMap sentence: document.get(SentencesAnnotation.class))
        {    
            for(CoreLabel token: sentence.get(TokensAnnotation.class))
            {       
                String word = token.get(TextAnnotation.class);      
                String lemma = token.get(LemmaAnnotation.class); 
                if(snapToOccurrences.containsKey(lemma)){
                	int frequency = snapToOccurrences.get(lemma)+1;
                	snapToOccurrences.put(lemma, frequency);
                }else{
                	snapToOccurrences.put(lemma, 1);
                }
            }
        }
	}
}
