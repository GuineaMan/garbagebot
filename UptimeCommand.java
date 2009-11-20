import org.jibble.pircbot.PircBot;


public class UptimeCommand implements BotCommand {

	static final String command = "uptime";

        static final String usage="\"uptime\" - I tell you the how long I've been alive for";

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
        bot.speak (channel, "I have been running for " + (System.currentTimeMillis() - bot.starttime) + "ms");
	}

}
