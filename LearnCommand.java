import org.jibble.pircbot.PircBot;


public class LearnCommand implements BotCommand {

	static final String command = "learn";

        static final String usage="\"learn: A PHRASE\" - makes me remember a witty response";

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
	
	}
}
