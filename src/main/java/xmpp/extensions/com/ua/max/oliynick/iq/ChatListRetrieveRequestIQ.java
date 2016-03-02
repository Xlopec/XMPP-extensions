package xmpp.extensions.com.ua.max.oliynick.iq;

import org.jivesoftware.smack.packet.IQ;

/**
 * <p>
 * This IQ stanza should be send in order to get
 * a result set of archived conversations
 * </p>
 * @author Max Oliynick
 * */
public final class ChatListRetrieveRequestIQ extends IQ {
	
	public static final String elementName = "retrieve";
	public static final String namespace = "urn:xmpp:archive";
	
	/**
	 * Represents 'with' attribute
	 * */
	private String with = null;
	
	/**
	 * Represents 'start' attribute
	 * */
	private String start = null;
	
	/**
	 * Represents 'end' attribute
	 * */
	private String end = null;
	
	/**
	 * Represents 'max' tag value
	 * */
	private int max = 0;
	
	/**
	 * Represents 'after' tag value
	 * */
	private String after = null;

	/**
     * Constructs an empty {@link ChatListRetrieveRequestIQ} instance
     * */
	ChatListRetrieveRequestIQ() {
		super(elementName, namespace);
	}
	
	public ChatListRetrieveRequestIQ(final String with) {
		this();
		setWith(with);
	}

	public String getWith() {
		return with;
	}

	public void setWith(String with) {
		this.with = with;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getAfter() {
		return after;
	}

	public void setAfter(String after) {
		this.after = after;
	}

	@Override
	protected IQChildElementXmlStringBuilder getIQChildElementBuilder(
			IQChildElementXmlStringBuilder xml) {
		
		if(getWith() != null) {
			xml.attribute("with", getWith());
		}
		
		if(getStart() != null) {
			xml.attribute("start", getStart());
		}
		
		if(getEnd() != null) {
			xml.attribute("end", getEnd());
		}
		
		xml.rightAngleBracket().
		halfOpenElement("set").
		attribute("xmlns", "http://jabber.org/protocol/rsm").
		rightAngleBracket();
		
		if(getMax() > 0) {
			xml.openElement("max").
			append(String.valueOf(getMax())).
			closeElement("max");
		}
		
		if(getAfter() != null) {
			xml.openElement("after").
			append(getAfter()).
			closeElement("after");
		}
		
		xml.closeElement("set");
		
		return xml;
	}

	@Override
	public String toString() {
		return "ChatListRetrieveRequestIQ [with=" + with + ", start=" + start
				+ ", end=" + end + ", max=" + max + ", after=" + after + "]";
	}
	
}
