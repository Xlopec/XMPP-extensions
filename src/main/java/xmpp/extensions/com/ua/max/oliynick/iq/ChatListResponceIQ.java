package xmpp.extensions.com.ua.max.oliynick.iq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.jivesoftware.smack.packet.IQ;

import xmpp.extensions.com.ua.max.oliynick.provider.ChatListResponceIQProvider;

/**
 * <p>
 * This IQ represents response
 * on {@link ChatListRequestIQ}
 * </p>
 * @author Max Oliynick
 */
public final class ChatListResponceIQ extends IQ {

	/**
	 * 'chat' tag
	 * */
    private static final String chatStr = "<chat xmlns='urn:xmpp:archive' with='?' start='?'/>";
    /**
     * Substitution pattern
     * */
    private static final Pattern pattern = Pattern.compile("\\?");

    /**
     * Contains 'chat' tags
     * */
    private final StringBuilder chatsBuffer;
 
    /**
     * index attribute and value of the 'first' tag
     * */
    private int firstIndex;
    private String firstValue;
    
    /**
     * last value of the 'last' tag
     * */
    private String lastValue;
    
    /**
     * value of the 'count' tag
     * */
    private int count;
    
    /**
     * set of available chat dates
     * */
    private final List<String> startDate;

    /**
     * Constructs an empty {@link ChatListResponceIQ} instance
     * */
    public ChatListResponceIQ() {
        super(ChatListResponceIQProvider.elementName, ChatListResponceIQProvider.namespace);
        chatsBuffer = new StringBuilder();
        startDate = new ArrayList<>(15);
        firstIndex = -1;
        firstValue = lastValue = null;
    }

    /**
     * Appends 'chat' tag
     * @param with user jid
     * @param start start date. See XEP-0106
     * */
    public void appendChat(String with, String start) {

        if(with == null)
            throw new IllegalArgumentException("with == null");
        
        if(start == null)
        	throw new IllegalArgumentException("start == null");

        chatsBuffer.append(chatStr.replaceFirst(pattern.pattern(), with).replaceFirst(pattern.pattern(), start));
        
        startDate.add(start);
        count++;
    }

    public void setLast(String last) {
        lastValue = last;
    }

    public void setFirst(int index, String value) {
        firstIndex = index;
        firstValue = value;
    }

    public void clearBuffers() {
        chatsBuffer.setLength(0);
        count = 0;
        firstIndex = -1;
        firstValue = lastValue = null;
    }
    
    public int getFirstIndex() {
    	return firstIndex;
    }
    
    public String getFirstValue() {
    	return firstValue;
    }
    
    public String getLastValue() {
    	return lastValue;
    }
    
    public int getCount() {
    	return count;
    }
    
    public Collection<String> getStartDates() {
    	return Collections.unmodifiableCollection(startDate);
    }

    @Override
	public String toString() {
		return "ChatListResponceIQ [firstIndex=" + firstIndex + ", firstValue=" + firstValue
				+ ", lastValue=" + lastValue + ", count=" + count
				+ ", startDate=" + startDate + "]";
	}
    
    

	@Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
    	
    	if(getFirstIndex() < 0 || getFirstValue() == null ||
    			getLastValue() == null || getCount() < 0) {
    		
    		xml.rightAngleBracket();
    		return xml;
    	}
    	
    	xml.rightAngleBracket().
    	append(chatsBuffer.toString()).
    	halfOpenElement("set").
        attribute("xmlns", "http://jabber.org/protocol/rsm").
        rightAngleBracket();
    	
    	// adds 'first', 'last' and 'count' tags if they were specified
    	if(getFirstIndex() >= 0 && getFirstValue() != null 
    			&& getLastValue() != null) {
    		
    		xml.halfOpenElement("first").
            attribute("index", getFirstIndex()).rightAngleBracket().
            append(String.valueOf(getFirstValue())).
            closeElement("first");
            
            xml.halfOpenElement("last").rightAngleBracket().
            append(String.valueOf(getLastValue())).
            closeElement("last");
            
            xml.halfOpenElement("count").rightAngleBracket().
            append(String.valueOf(getCount())).
            closeElement("count");
    		
    	} else {
    		// an empty result set
    		xml.halfOpenElement("count").
    		attribute("xmlns", "http://jabber.org/protocol/rsm").
    		rightAngleBracket().
            append(String.valueOf(getCount())).
            closeElement("count");
    		
    	}

        xml.closeElement("set");

        return xml;
    }

}


