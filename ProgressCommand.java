import org.jibble.pircbot.PircBot;


public class ProgressCommand implements BotCommand {

	static final String command = "progress";

        static final String usage="\"progress\" - I report how many stored quotes I have";

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
        bot.speak(channel, sender + ": Stored " + bot._quotes.size() + " quotes out of " + bot.MAX_QUOTES);
	}

}
