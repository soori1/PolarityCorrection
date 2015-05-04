/**
 * A sentiment polarity correction class to remove sentiment
 * polarity inconsistency from a given document.
 * The polarity correction is tested on online product reviews 
 * and should be applicable to other documents that contain sentiments
 * expressed with sentence boundaries.
 * 
 * The PolarityClassification class in the constructor should be replaced by 
 * any classifier object.
 * 
 * @author      soori
 * @version     1.1
 * @since       2014
 */


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PolarityCorrection {

    private final List<String> sentences;
    private final PolarityClassification sentenceClassifier;
    private List<String> consistentSentence;
    
    
    
    public PolarityCorrection(String text,  PolarityClassification sentenceClassifier){
        this.sentences =  Arrays.asList(text.split("\n"));
        this.sentenceClassifier = sentenceClassifier; 
        consistentShiftPattern();
    }

    /**
     * Classifies each sentence in the given document using a naive classifier
     * provided through the constructor. 
     * The method sends the list of classified sentences to prepare a list
     * of consistent sentiment polarities.
     * 
     * The method also uses an inner class SentencePolarity.
     * @see         SentencePolarity
     */

    private void consistentShiftPattern(){
        
        List<SentencePolarity> shiftPolarityList = new ArrayList<SentencePolarity>();
        int i=0;
            for(String sentence : sentences){

                int polarity=-1; // the default is negative polarity

                 //positive polarity   
                   if(sentenceClassifier.classify(sentence).equals("positive")) {
                      polarity=1;
                   }
                   
                //build the polarity object
                SentencePolarity sentencePolarity = new SentencePolarity(sentence,polarity); 

                shiftPolarityList.add(sentencePolarity);
            }
        
        // prepare consistent pattern
         consistentSentence = polarityShiftPattern(shiftPolarityList);

       }
       
    
    /**
     * Returns a list of consistent sentiments in sentences.
     * Consistent sentiment is identified by checking that previous and 
     * subsequent polarities are the same. Otherwise a polarity is deem
     * inconsistent.
     * 
     * @param List<SentencePolarity> a list of classified sentences as SentencePolarity objects
     * @return List of consistent sentiments
     * @see   SentencePolarity
     */
       
    public List<String> polarityShiftPattern(List<SentencePolarity> shiftsPolarityList){
        
        List<String> default_shifts = new ArrayList();
        List<Integer> remove_list = new ArrayList();
        List<String> consistent_sentence = new ArrayList();

        int last_class=0;       

        //iterate the polarity list to note inconsistency
        for(int i=0; i<shiftsPolarityList.size();++i){
           
            //set the class for the first sentence (prior)
            if(i==0) {
                last_class = shiftsPolarityList.get(i).polarity_class;
                                continue;
            }


            //if this is the last polarity
            if( i == shiftsPolarityList.size()-1) {
                

                    if(shiftsPolarityList.get(i).polarity_class==last_class) {
                        last_class = shiftsPolarityList.get(i).polarity_class;
                        
                    }
                    else{
                       last_class = shiftsPolarityList.get(i).polarity_class;
                       
                       remove_list.add(i);
                    }

            }

           
            // travelling other polarity and taking note of inconsistent polarity to be removed
            if(shiftsPolarityList.get(i).polarity_class==last_class){
                last_class = shiftsPolarityList.get(i).polarity_class;
               
                continue;
            }
            else{

                if(shiftsPolarityList.get(i).polarity_class==shiftsPolarityList.get(i+1).polarity_class){
                     last_class = shiftsPolarityList.get(i).polarity_class;
                     
                     continue;
                }
                else{
                    last_class = shiftsPolarityList.get(i).polarity_class;                   
                    remove_list.add(i);
                }
               
            }

        }


        //take and store the consistent senstences now  
        for(int j=0; j<shiftsPolarityList.size();++j){
            if(remove_list.contains(j)){            
                continue;
            }
            consistent_sentence.add(shiftsPolarityList.get(j).sentence);          
        }
        
        
        //return default pattern if no consistency observed
        if(consistent_sentence.isEmpty()) {
            
            for(SentencePolarity sentPol:shiftsPolarityList){
                default_shifts.add(sentPol.sentence);
            }
            
            return default_shifts;
        }
              
   
        return consistent_sentence;
    }

       
    /**
     * Returns a readily prepared list of consistent sentiments in sentences.
     * @return List of consistent sentiments as sentences.
     */   
    public List<String> consistentSentenceShift(){
           return consistentSentence;
       }


    /**
     * Returns the entire list of consistent sentiments as text.
     *  The list of consistent sentiments is combined as a single text 
     * 
     * @param List of consistent sentiments in sentences
     * @return String a combine text.
     */   
    public String SentencePatterntoText(List<String> consistentSentencePattern){
           String text="";
           for(int i=0; i<consistentSentencePattern.size();++i)
               text=text+consistentSentencePattern.get(i)+"\n";
           return text;
       }
       
    /**
	* inner class for SentencePolarity object.
	*/
    private class SentencePolarity{
        
        public final String sentence;
        public final int polarity_class;

            public SentencePolarity(String sentence, int polarity_class){
                this.sentence = sentence;
                this.polarity_class = polarity_class;
            }
       }
       

}

