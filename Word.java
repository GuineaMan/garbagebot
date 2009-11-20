import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;


public class Word extends BaseWord
{
  private ArrayList adjacentWords;

  Word()
  {
    super();
    adjacentWords = new ArrayList<BaseWord>();
  }

  Word (String _word)
  {
    super(_word);
    adjacentWords = new ArrayList<BaseWord>();
  }


  public void addAdjacentWord(String aw)
  {
    int i=seenWord(aw);

    if (i == -1)
    {
      //System.out.println("Word["+word+"] Putting a new key for word " + aw);
      adjacentWords.add(new BaseWord(aw));
    }
    else
    {
      BaseWord bw = (BaseWord)adjacentWords.get(i);
      bw.occurences++;            
      //System.out.println("Word["+bw+"] Incremented word " + bw.occurences);
    }
    Collections.sort(adjacentWords);
  }

  public String getBestAdjacentWord()
  {
    
    String result = "";
    
    if (adjacentWords.size() > 0)
    {
      int randIndex = (int)(System.currentTimeMillis() % Math.ceil(adjacentWords.size()*0.1));
      int ctr=0;  

      result = (BaseWord)adjacentWords.get(randIndex)+"";
    }
    return result;
  }



  public void printTopFiveWords()
  {
    int ctr=0;  


    Iterator iter = adjacentWords.iterator();
    while (iter.hasNext() && (ctr < 5)) 
    {
      ctr++;
      System.out.println("Word("+this + "): top word[" +ctr+"]=" + iter.next());
    }
  }


  //returns index of this word if found
  public int seenWord(String wrd)
  {
    int ctr=-1;
    boolean found=false;
    

    Iterator iter = adjacentWords.iterator();
    while (iter.hasNext() && !found) 
    {
      ctr++;
      BaseWord bw = (BaseWord)iter.next();
      if (bw.word.equals(wrd)) found = true;
    }
    
    if (!found) ctr = -1;
    
    return ctr;
  }


  public String toString()
  {
    return word + ":" + occurences;
  }

}

