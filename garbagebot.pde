/* 
 by evan.raskob
 evan@lowfrequency.org
 */

import java.io.*;
import java.util.*;
import org.jibble.pircbot.*;


InnerGarbageBot bot;
String channel = "#openlab";
//String channel = "#placard";
//String channel = "#dorkbot";
//String channel = "#test";


void setup()
{
  size(100, 100);
  /*
Properties p = new Properties();
   p.load(new FileInputStream(new File("bot.ini")));
   File outputDirectory = new File(p.getProperty("outputDirectory"));
   if (!outputDirectory.isDirectory()) {
   System.out.println("Output directory must be a valid directory, not " + outputDirectory.toString());
   System.exit(1);
   }
   
   String channel = p.getProperty("channel");
   */


  //GarbageBot bot = new GarbageBot(outputDirectory, p.getProperty("helpString"), channel);

  bot = new InnerGarbageBot(this, channel);
  bot._server = "irc.goto10.org";

  try {
    bot.tryConnect();  
  }
  catch (Exception e)
  {
    System.out.println("Exception thrown: " + e);
    e.printStackTrace(System.out);
  }

}


void draw()
{

}


void keyPressed()
{
  if (key == 'c') 
  {
    System.out.println("connecting to " + channel);
    try {
      bot.tryConnect();  
    }
    catch (Exception e)
    {
      System.out.println("Exception thrown: " + e);
      e.printStackTrace(System.out);
    }
  } 
}

