import org.jibble.pircbot.PircBot;


public interface BotCommand {

	static final String command="";

        static final String usage="";

	// Each BotCommand implementor will return the command name to which they respond
	public String getCommandName();

	// Each BotCommand implementor will return the a short string describing how to use it
	public String getUsage();


	// The method where each BotCommand implementor will handle the event
	public void handleMessage(InnerGarbageBot bot, String channel, String sender, String args[], String message);
}
