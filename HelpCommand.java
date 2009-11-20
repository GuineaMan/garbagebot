import org.jibble.pircbot.PircBot;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;


public class HelpCommand implements BotCommand {

  static final String command = "help";
  static final String usage ="Get a list of commands and usage by typing \"help\"";


  // Each BotCommand implementor will return the a short string describing how to use it
  public String getUsage()
  {
    return usage;
  }

  // Each BotCommand implementor will return the command name to which they respond
  public String getCommandName()
  {
    return command;
  }

  // The method where each BotCommand implementor will handle the event
  public void handleMessage(InnerGarbageBot bot, String channel, String sender, String args[], String message)
  {
    bot.speak(channel, sender + ": I am " + bot.getNick() + ", a garbage-spewing chatbot.  I listen for bits of random phrases and occasionally spew them into the " + channel + " IRC channel.");
    bot.speak(channel, sender + ": I know a few commands:");

    Set cmds = bot.commands.keySet();
    Iterator It = cmds.iterator();
    while (It.hasNext()) 
    {
      String cmdName = (String)(It.next());
      BotCommand bc = (BotCommand)bot.commands.get(cmdName);
      bot.speak(channel, sender + ": " + bc.getUsage());                
    }

    /*
        bot.speak(channel, sender + ": \"ANYTHING\" - I remember random words from what you say, and spew out garbage poetry occasionally");
     bot.speak(channel, sender + ": \"learn: A PHRASE\" - makes me remember a witty response");
     bot.speak(channel, sender + ": \"garbage: spew random garbage sentence");
     bot.speak(channel, sender + ": \"uptime\" - I tell you the how long I've been alive for");
     bot.speak(channel,  sender + ": \"progress\" - I report how many stored quotes I have");
     bot.speak(channel,  sender + ": \"responses\" - I tell you what responses I have stored");
     bot.speak(channel,  sender + ": \"leave CHANNEL\" - I leave a channel");
     bot.speak(channel,  sender + ": \"join CHANNEL\" - I join a channel");
     bot.speak(channel,  sender + ": that\'s all for now.");
     */
  }

}

