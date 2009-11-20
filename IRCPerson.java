import org.jibble.pircbot.User;


// http://www.jibble.org/javadocs/pircbot/org/jibble/pircbot/User.html

class IRCPerson
{
    String channel = null;
    long lastTimeSpoke = -1;
    long lastTimeSeen = -1;
    long timeJoined = -1;
    User user;    
        
        
        
    void spoke()
    {
        lastTimeSpoke = System.currentTimeMillis();
    }
    
    void seen()
    {
        lastTimeSeen = System.currentTimeMillis();
    }
 
}
