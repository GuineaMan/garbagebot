import org.jibble.pircbot.PircBot;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;


public class ThresholdCommand implements BotCommand {

  static final String command = "threshold";
  static final String usage ="\""+command+"\": set the threshold for my random responses (0 to 1, where 1 is less likely)";


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
	bot.spewThreshold = (new Float(args[2])).floatValue();
  }

}

