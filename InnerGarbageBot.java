/**
 * Garbage-spewing bot.
 *
 * @author evan raskob http://lowfrequency.org 
 */

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.jibble.pircbot.*;
import processing.core.*;


public class InnerGarbageBot extends PircBot implements BotListener
{

  public static final int MAX_QUOTES = 128;
  public int MAX_RESPONSES = 800;
  public int MAX_WORDS = 10000;

  PApplet parent;

  HashMap words = null;
  HashMap peoples = null;

  String[] wordsIndex;
  long[][] leftWords = null;
  long wordsSeen = 0;
  HashMap logins = null;
  ArrayList garbledWords = null;
  LinkedList responses = null;
  ArrayList prevMsgs = null;
  ArrayList nextMsgs = null;
  LinkedList _quotes = null;
  File _outputDirectory;
  String _helpString;
  String[] _channels;
  String _server = "irc.goto10.org";
  String _nick;
  String nickBase = "garbagebot";
  boolean connecting = false;

  String currentMessage = null;
  String parsedMessage[] = null;

  long starttime;
  long lastResponseTime, lastMsgTime, interval;

  // threshold above which garbage will be spewed to the irc channel in the form of a message
  // works off perlin noise generated off the current system time - see spewGarbage()
  float spewThreshold = 0.6f;
  
  Random generator;

  ArrayList _listeners;  //objects getting cmds from this
  HashMap commands;
  HashMap messages;

  boolean debug = true;


  public InnerGarbageBot(PApplet _parent, String channel)
  {

    parent = _parent;

    // makes it add number to nickname automatically instead of failing to connect
    // if nick in use 
    setAutoNickChange(true); 

    wordsSeen = 0;

    starttime = System.currentTimeMillis();
    lastMsgTime=starttime;
    lastResponseTime = 0;
    interval = 120000; //ms

    this._channels = new String[10];
    for (int x=0; x<_channels.length;x++)
      _channels[x] = null;

    this._channels[0] = channel;
    this.setVerbose(true);

    responses = new LinkedList();
    _quotes = new LinkedList();

    prevMsgs = new ArrayList();
    nextMsgs = new ArrayList();

    words = new HashMap(MAX_WORDS);
    leftWords = new long[MAX_WORDS][2];
    wordsIndex = new String[MAX_WORDS];

    logins = new HashMap();
    peoples = new HashMap();
    messages = new HashMap();

    boolean result = true;

    commands = new HashMap();
    _listeners = new ArrayList();
    _listeners.add(this);  // we are our own listener...

    // default keywords
    commands.put("help", new HelpCommand());
    //commands.put("learn", new LearnCommand());
    commands.put("join", new JoinCommand());
    commands.put("progress", new ProgressCommand());
    commands.put("uptime", new UptimeCommand());
    commands.put("tell", new TellCommand());
    commands.put("threshold", new ThresholdCommand());

    try 
    {
      result &= responses.add("whaddyawant?");
      result &= responses.add("the dude abides.");
      //result &= responses.add("/me is the true garbage-spewing bot");
      //result &= responses.add("all bad poetry springs from genuine feeling.");
      //result &= responses.add("I'm kind of stuck on monad transformers.");
      //result &= responses.add("A gentleman is one who never hurts anyone's feelings unintentionally.");
      //result &= responses.add(":)");
      //result &= responses.add(";)");
      //result &= responses.add(":(");
      //result &= responses.add("Say what?");
      //result &= responses.add("/topic I am the REAL garbageBot!");
    }
    catch (Exception e)
    {
      System.out.println("Exception thrown: " + e);
      e.printStackTrace(System.out);
    }

    if (!result) System.out.println("********** Error: failed to add one or more responses");

    generator = new Random();
  }


  public boolean tryConnect() throws org.jibble.pircbot.NickAlreadyInUseException, org.jibble.pircbot.IrcException, java.io.IOException 
  {
    boolean goodNick = false;
    int attempts = 1;
    String nick = nickBase;

    while (attempts <= 20 && !goodNick && !isConnected())
    {

      try
      {
        setName(nick);
        setLogin(nick);
        _nick = nick;
        //connect("irc.goto10.org");
        connect(_server);
        //        connect("irc.freenode.net");
        joinChannel(_channels[0]);
        goodNick = true;
      }
      catch (org.jibble.pircbot.NickAlreadyInUseException nie)
      {

        nick = nickBase + attempts;
        goodNick = false;
      }
      attempts++;
    }

    if (attempts >=20) 
    {
      throw new org.jibble.pircbot.NickAlreadyInUseException("garbageBot_"+attempts);
    }

    return (goodNick || isConnected());

  }

  public void setInterval(long iv)
  {
    this.interval = iv;
  }


  public void speak(String channel, String msg)
  {
    int mode = 0; //0=regular, 1=action, 2=topic, 3=kick  
    lastResponseTime = System.currentTimeMillis();
    int start=0;
    int len = 200;
    int end = len;
    int mits = 0; //safety net...

    int slash = msg.indexOf("/"); 
    if ( slash >= 0)
    {
      if (msg.startsWith("/me"))
      {
        mode = 1;
        start = ("/me").length()-1;
      } 
      if (msg.startsWith("/topic"))
      {
        mode = 2;
        start = ("/topic").length()-1;
      }
      if (msg.startsWith("/kick"))
        mode = 3;
      start = ("/kick").length()-1;
    }  

    if (start < 0) { 
      if (debug) System.out.println("why is start ["+start+"] < 0???"); 
      start=0; 
    }


    // speak the message but split the string into smaller bits if it is too big
    // and split it along whitespace

    ArrayList msgs = new ArrayList();

    Pattern p = Pattern.compile("\\s"); //whitespace char
    Matcher m;

    while (start < msg.length() && (mits < 2000) )
    {

      mits++;

      if (end >= msg.length()) 
      {
        msgs.add(msg.substring(start));
        start = end;
      }
      else 
      {
        m = p.matcher(msg.substring(end-1,end));

        if (m.matches())
        {

          msgs.add(msg.substring(start,end));
          start = end;
          end += len;
        }
        else
        {
          end--;
        }
        // if too long add a hyphen
        if (end < (start+20))
        {
          msgs.add(msg.substring(start,start+len-1) + "-");
          start = start+len-1;
          end = start+len;
        }
      } 
    }


    for (int i=0; i< msgs.size(); i++)
    {
      delayMs(((String)msgs.get(i)).length()*30);
      switch(mode)
      {
      case 0: 
        //if (debug) System.out.println("SENDING TO " + channel);
        //if (debug) System.out.println("SENDING::: " + msgs.get(i));
        sendMessage(channel, (String)msgs.get(i));
        break;

      case 1: 
        sendAction(channel, (String)msgs.get(i));
        break;

      case 2: 
        setTopic(channel, (String)msgs.get(i));
        break;

      case 3: 
        kick(channel, (String)msgs.get(i));
        break;
      }
    }
  }


  /*
   * Find a channel by matching the string.
   * returns -1 if not found.
   */

  public int findChannelInList(String c)
  {
    int i = -1;
    boolean found = false;

    while ( i < (_channels.length-1) && !found)
    {
      i++;
      if (_channels[i] != null)
        if (_channels[i].equals(c))
        {
          found = true;
        }
    }

    if (!found) i = -1;

    return i;
  }


  /*
   * Find first unused channel slot in arrayt.
   * returns -1 if all full (10)
   */

  public int findFirstUnusedChannelIndex()
  {
    int i = 0;
    boolean found = false;

    while ( i < _channels.length && !found)
    {
      if (_channels[i] == null || _channels[i].equals(""))
        found = true;
      else
        i++;
    }
    if (!found) i = -1;

    return i;
  }




  public void speak(String msg)
  {
    int i=-1;
    boolean found = false;

    while ( i < _channels.length && !found)
    {
      i++;
      if (_channels[i] != null && !_channels[i].equals(""))
        found = true;
    }
    if (found)
      speak(_channels[i], msg);
    else
      if (debug) System.out.println("ERROR: No channels to send to !!!");
  }


  public void onDisconnect()
  {
    int attempts = 0;
    while (!isConnected() && attempts < 15)
    {
      attempts++;
      delayMs(4000);
      try{
        tryConnect();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

    }
  }


  public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) 
  {
    delayMs(5000);
    joinChannel(channel);

  }

  public void onJoin(String channel, String sender, String login, String hostname) 
  {

    // Convert the nickname to lowercase to get the HashMap key.
    String key = sender.toLowerCase( );
    ArrayList list = (ArrayList) messages.get(key);
    if (list != null) {
      // Send all messages to the user.
      for (int i = 0; i < list.size( ); i++) {
        String message = (String) list.get(i);
        sendMessage(channel, sender + ", " + message);
      }
      // Now erase all messages for this user.
      messages.put(key, null);
    }

    //    spewGarbage(channel);
    //respond(channel, sender);
  }


  public void onMessage(String channel, String sender, String login, String hostname, String message)     
  {
    lastMsgTime = System.currentTimeMillis();
    processMessage(channel, sender, message);

  } 

  public void onAction(String sender, String login, String hostname, String target, String action) 
  {
    // how come there is no channel associated, here? we'll just process the message using the first channel...

    int i=-1;
    boolean found = false;

    while ( i < _channels.length && !found)
    {
      i++;
      if (_channels[i] != null || !_channels[i].equals(""))
        found = true;

    }
    if (found)
    {
      processMessage(_channels[i], sender, action);
    }
  }


  public void addResponse(String r)
  {
    responses.add(r);
    if (responses.size() > MAX_RESPONSES) responses.removeFirst();
  }


  public void processMessage(String channel, String sender, String message)
  {
    int tempIndex = -1;
    // convert string to lowercase
    String origMsg = message.trim();

    // replace puctuation at end or beginning
    String lowMsg = origMsg;
    lowMsg = lowMsg.replaceAll("^\\p{Punct}*|\\p{Punct}*$", "");

    String regx ="\\p{Punct}*\\s+\\p{Punct}*";

    String[] tmp = lowMsg.split(regx);

    long cnt=1;


    // handle middle of pack of words
    for (int x=0; x < tmp.length; x++)
    {

      String w = tmp[x].trim();

      wordsSeen++;
      // see if we've seen this word - then increment. otherwise store it
      if (words.containsKey(w))
      {
        Word tmpWrd = ((Word)words.get(w));
        tmpWrd.occurences++;
        if (debug) System.out.println("found word:" + tmpWrd);

        if (x < (tmp.length-1)) tmpWrd.addAdjacentWord(tmp[x+1]);

      }
      else
      {
        Word tmpWrd = new Word(w); 

        if (debug) System.out.println("adding new word:" + tmpWrd);

        if (x < (tmp.length-1)) tmpWrd.addAdjacentWord(tmp[x+1]);

        words.put(w, tmpWrd);

      }
    }

    if (_quotes.size() > 3 && parent.noise(parent.millis()/10000) > spewThreshold)
    {
      spewGarbage(channel);
      //_quotes.clear();
    }

    //    if (debug) System.out.println("There was " + (lastMsgTime - lastResponseTime) + "ms between last resonse and this msg");
    //  if ( ((lastMsgTime % 10) / 3f) > 0.66) spewGarbage(channel);
    //  else if ((lastMsgTime - lastResponseTime) < interval) spewGarbage(channel);


    // now jump thru keywords and see if we have a match
    // if so invoke method on listeners
    if (tmp[0].equals(getNick()))
    {
      //      if (debug) System.out.println("SENDING TO " + channel);
      //      if (debug) System.out.println("SENDING::: " + sender);
      for (int x=0; x < _listeners.size(); x++)
      {
        ((BotListener)_listeners.get(x)).event(channel, sender, tmp, origMsg);
      }
    }
    else
    {
      if (message.indexOf(getNick()) > 1)  
        spewGarbage(channel);
      //respond(channel, sender);

      queueMessage(tmp);
    }
  }


  /*
   * respond to a person saying our name
   */
  public void respond(String channel, String sender)
  {
    int index = generator.nextInt(responses.size());
    delayMs( ((String)responses.get(index)).length()*30);

    String s = sender + ": ";
    String r = (String)responses.get(index);

    if (!r.startsWith("/"))
      r = s + r;

    if (debug) System.out.println("RESPONDING*****" + r + "******");

    speak(channel, r);
  }


  /*
  * add bit of message array to queue of garbage to spew
   */
  public void queueMessage(String[] message)
  {

    ArrayList usedIndices = new ArrayList();
    boolean doneFinding=false;
    
    int wordsToFind = (int)(0.5*(message.length-1.0)*parent.random(message.length))+1;
    
    if (debug) System.out.println("Finding " + wordsToFind + " words");
    int iters=0;

    do {

      iters++;
      if (iters > 32) doneFinding = true;

      //      int randIndex = (int)(message.length * noise());

      int randIndex = (int)parent.random(message.length);

      Integer newIndex = new Integer(randIndex);

      if (debug) System.out.println("used " + usedIndices.size() + " indices [" + iters+"] (" + randIndex + "/" + newIndex);

      if (!usedIndices.contains(newIndex))
      {

        usedIndices.add(newIndex);
        if (usedIndices.size() >=  wordsToFind) 
        {
          if (debug) System.out.println("used " + usedIndices.size() + " indices");
          doneFinding = true;
        }

        String newQuote = message[randIndex];

        // if this was a person's name, try another word...
        //if (newQuote.endsWith(":")) newQuote = message[(randIndex + 1) % message.length];
        // if it is again, then just strip off the ":"
        if (newQuote.endsWith(":")) newQuote = newQuote.substring(0,newQuote.length()-2);

        if (newQuote.length() > 0)
        {
          try
          {
            _quotes.add(newQuote);
            if (debug) System.out.println("Stored " + _quotes.size() + " quotes / " + newQuote);
          }

          catch (IndexOutOfBoundsException ioe)
          {
            ioe.printStackTrace(System.out);
          }
        }
        if (_quotes.size() > MAX_QUOTES) {
          _quotes.removeFirst();
        }
        newQuote = null;
      }
    } 
    while (!doneFinding);

  }


  /*
   * send garbage to irc channel
   */
  public void spewGarbage(String channel)
  {

    if (_quotes.size() > 1)
    {
      // queue garbage
      StringBuffer msgToSend = new StringBuffer();

      // sort of a random start...
      int startIndex = (int)(System.currentTimeMillis() % (_quotes.size()-1));

      if (debug) System.out.println("starting from index " + startIndex);

      int numWords = (int)(System.currentTimeMillis() % 12) + 1;

      if (debug) System.out.println("finding number of words: " + numWords);

      int ctr=1;
      boolean problem = false;

      Word startWord = (Word)words.get(_quotes.get(startIndex));

      if (debug) System.out.println("Start word: " + startWord);

      msgToSend.append(startWord.word);


      while( ctr < numWords && !problem)
      {
        String nextWord = startWord.getBestAdjacentWord();

        if (debug) System.out.println("["+ctr+"] next word: " + nextWord);

        if (!nextWord.equals("")) 
        {        
          msgToSend.append(" " + nextWord);

          try
          {
            startWord = (Word)words.get(nextWord);
          }
          catch(Exception e)
          {
            e.printStackTrace();
            problem=true;
          }
        } 
        else problem=true;

        ctr++;
      }


      /*
    int startIndex = generator.nextInt(max((_quotes.size()/2-1),1));
       if (startIndex < 0 ) startIndex=0;
       
       int endIndex = startIndex + generator.nextInt(max(_quotes.size() - startIndex + 1,0));
       if (endIndex > _quotes.size()) endIndex = _quotes.size(); 
       
       // if (debug) System.out.println("********Sending msg: " + startIndex + "/" + endIndex + " *************");
       
       for (int i = startIndex; i < endIndex; i++) {
       try 
       {
       msgToSend = msgToSend.concat((String)_quotes.get(i) + " ");	
       }
       catch (IndexOutOfBoundsException ioe)
       {
       if (debug) System.out.println("Exception thrown: " + ioe);
       
       ioe.printStackTrace(System.out);
       }
       }
       */
      delayMs(msgToSend.length()*40);

      speak(channel, msgToSend.toString());

      addResponse(msgToSend.toString());
    }
  }


  public void garbage(String channel)
  { 
    String crap = "";

    Vector v = new Vector(words.keySet());

    int nextIndex = generator.nextInt(v.size());

    //if (debug) System.out.println("next index is " + nextIndex);

    //Collections.sort(v);
    //it = v.iterator();
    //while (it.hasNext())
    //{

    int m = generator.nextInt(20) + 2;
    //((int)System.currentTimeMillis() % 15 + 2);

    if (debug) System.out.println("m is " + m);

    String word;

    for (int j= 0; j < m; j++)
    {
      word = (String)v.get(nextIndex);
      crap += (" " + word);

      nextIndex += ((Long)words.get(word)).intValue();
      nextIndex %= v.size();

      //if (debug) System.out.println("(LOOP + "+ j +")next index is " + nextIndex);
    }

    speak(channel, crap);

    // if ((System.currentTimeMillis() % sz) == 0)
    // {


  }



  public void event(String channel, String sender, String[] args, String origMsg)
  {
    if (args.length > 1)
    {
      String cmd = args[1].toLowerCase();

      if (debug) System.out.println("cmd=" + cmd);
      if (debug) System.out.println("orig msg=" + origMsg);

      int cmdStartIndex = origMsg.indexOf(args[1]) + args[1].length()+1; //allow for trailing space

      String msgBody;

      if (cmdStartIndex < 0 || cmdStartIndex >= origMsg.length()) cmdStartIndex=0;

      if (debug) System.out.println("cmd start index=" + cmdStartIndex);

      msgBody = origMsg.substring(cmdStartIndex);



      if (debug) System.out.println("CMD:::::"+ cmd + ":::::");

      BotCommand bc = (BotCommand)commands.get(cmd);
      if (bc != null)
      {
        bc.handleMessage(this, channel, sender, args, msgBody);
      }

      if(cmd.equals("responses"))
      {
        ListIterator li = responses.listIterator();
        String responsesString = "";

        while (li.hasNext())
        {
          responsesString = responsesString + li.next();
          if (li.hasNext()) responsesString = responsesString + "; ";
        }
        speak(channel, sender + ": I have these witty responses stored: " + responsesString);

      } 
      else if(cmd.equals("debug"))
      {
        debug = !debug;
        speak(channel, "Debugging is " + debug);
      }

      else if(cmd.equals("person"))
      {
        String c = "";
        if (args.length > 2) c = args[2].toLowerCase();
      }
      else if(cmd.equals("leave"))
      {
        String c = "";

        if (args.length > 2) c = args[2].toLowerCase();

        if (c != "")
        {
          if (!c.startsWith("#")) c = "#"+c;

          int i = findChannelInList(c);

          if (i == -1)
            speak(channel, "I can\'t leave " + c + " because I\'m not in it!");
          else
          {
            partChannel(c, sender + " told me to leave");
            _channels[i] = null;
          }       
        }
      }

      else if(cmd.equals("channels"))
      {
        int i=0;

        while ( i < _channels.length)
        {
          if (_channels[i] != null&& !_channels[i].equals(""))
            speak(channel, "Connected to channel["+i+"]: "+_channels[i]);
          i++;
        }
      }
      else if(cmd.equals("interval"))
      {
        this.interval = (new Integer(args[2])).intValue();
        speak(channel, "my response interval is now "+interval + "ms");
      }

      else if(cmd.equals("count"))
      {

        if (args.length > 1) 
        {
          String c = args[2].toLowerCase();

          long maxVal = 0;

          if (words.containsKey(c))
          {
            maxVal = ((Long)words.get(c)).longValue();
            speak(channel, "I\'ve seen \"" + c + "\" appear " + maxVal + " times out of " + wordsSeen);
          }

        }
      }

      else if(cmd.equals("report"))
      {
        speak(channel, "So you want to see all the words I\'ve kept track of?");
        speak(channel, "TOO BAD.");
        //speak(channel, words.toString());
        if (debug) System.out.println(words.toString());

        String report = buildReport();
        speak(channel, report);

      }  
      else if(cmd.equals("garbage"))
      {
        //garbage(channel);
        spewGarbage(channel);
      }

      else
      {
        spewGarbage(channel);
        //respond(channel, sender);
      }
    }
  }



  private String buildReport()
  {
    StringBuffer result=new StringBuffer();

    Collection c = words.values();

    Iterator iter = c.iterator();
    while (iter.hasNext()) 
    {
      Word w = (Word)iter.next();
      w.printTopFiveWords();
      result.append(w);
      if (iter.hasNext()) result.append(", ");
    }

    return result.toString();
  }





  private void delayMs(long ms)
  {
    Date d = new Date();
    Date e;
    long cTime = d.getTime();
    long tTime;

    do
    {
      e = new Date();
      tTime = e.getTime();

    }  
    while(tTime - cTime <= ms);

    return;
  }
}

