import org.jibble.pircbot.PircBot;
import java.util.ArrayList;
import java.util.HashMap;


public class TellCommand implements BotCommand {

	static final String command = "tell";

    static final String usage="\"tell NICK: A PHRASE\" - tell someone a phrse next time they join";

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
		String key = args[2].toLowerCase();

		ArrayList list = (ArrayList) bot.messages.get(key);
		if (list == null) {
			// Create a new ArrayList if the HashMap entry is empty.
			list = new ArrayList( );
			bot.messages.put(key, list);
		}
		
		// Add the message to the list for the target nickname.
		list.add(sender + " asked me to tell you " + message);
		bot.speak(channel, sender + ": Okay, telling "+key+" "+message+" next time I see them");
	}
}
