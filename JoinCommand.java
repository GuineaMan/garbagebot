import org.jibble.pircbot.PircBot;


public class JoinCommand implements BotCommand {

  static final String command = "join";

  static final String usage = "\"join CHANNEL\" - I join a channel";


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
    String c = "";

    if (args.length > 2) c = args[2].toLowerCase();

    if (c != "")
    {
      if (!c.startsWith("#")) c = "#"+c;

      int i = bot.findChannelInList(c);

      if (i == -1)
      {
        int g  = bot.findFirstUnusedChannelIndex();
        if (g != -1)
        {
          bot._channels[g] = c;
          bot.joinChannel(bot._channels[g]);
          bot.speak (channel, "I joined " + c);
        }
        else
          bot.speak (channel, "I can\'t join " + c + " because I've already joined too many channels.  Try kicking me off one first.");
      }
      else
        bot.speak (channel, "I\'m already in " + c + "!");        
    }
  }
}


