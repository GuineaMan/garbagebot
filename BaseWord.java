class BaseWord extends Object implements Comparable<BaseWord>
{
  public String word;
  public int occurences;

  BaseWord()
  {
    word = "";
    occurences = 0;
  }

  BaseWord (String _word)
  {
    word = _word;
    occurences = 1;
  }


  // this is backwards so the arrays sort in DESCENDING order
  public int compareTo(BaseWord w2)
  {
    // returns -, 0, or +, for less than, equals, greater than, respectively
    if (occurences > w2.occurences)
      return -1;
    else if (occurences < w2.occurences)
      return 1;
    else
      return 0;

  }

  public String toString()
  {
    return word;
  }

}

