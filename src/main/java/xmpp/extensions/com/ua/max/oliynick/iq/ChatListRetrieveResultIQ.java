package xmpp.extensions.com.ua.max.oliynick.iq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;

import xmpp.extensions.com.ua.max.oliynick.provider.ChatListRetrieveIQProvider;

/**
 * <p>
 * This class represents receiving archived
 * message history in terms of raw XML IQ or stanza
 * </p>
 * @author Max Oliynick
 * */
public final class ChatListRetrieveResultIQ extends IQ {
	
	/**
	 * Represents 'with' attribute
	 * */
	private String with = null;
	
	/**
	 * Represents 'start' attribute
	 * */
	private String start = null;
	
	/**
	 * Represents 'subject' attribute
	 * */
	private String subject = null;
	
	/**
	 * Represents 'version' attribute
	 * */
	private int version;
	
	/**
     * index attribute and value of the 'first' tag
     * */
    private int firstIndex;
    
    /**
     * value of the 'first' tag
     * */
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
	 * Defines direction of chat message
	 * */
	public static enum Direction { from, to };
	
	/**
	 * Wrapped message content
	 * */
	private static final class WrappedBody {
		
		/**
		 * Seconds since start of the conversation
		 * */
		private int secs = 0;
		
		/**
		 * Message body
		 * */
		private String body = null;
		
		/**
		 * Direction of the message. Can only be 'from' or 'to'
		 * */
		private Direction direction;
		
		/**
		 * Constructs an instance of {@link WrappedBody} 
		 * class with given parameters
		 * @param secs seconds since start of the conversation
		 * @param body message body
		 * @param direction direction of the message
		 * */
		public WrappedBody(int secs, String body, Direction direction) {
			this.secs = secs;
			this.body = body;
			this.direction = direction;
		}

		public int getSecs() {
			return secs;
		}

		public void setSecs(int secs) {
			this.secs = secs;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public Direction getDirection() {
			return direction;
		}

		public void setDirection(Direction direction) {
			this.direction = direction;
		}

		@Override
		public String toString() {
			return "secs=".concat(String.valueOf(secs)).concat(", body=").concat(body).
					concat(", direction=").concat(direction.toString().toLowerCase());
		}
		
	}
	
	private final List<WrappedBody> wrappedMessages;

	public ChatListRetrieveResultIQ() {
		super(ChatListRetrieveIQProvider.elementName, ChatListRetrieveIQProvider.namespace);
		wrappedMessages = new ArrayList<>();
		count = firstIndex = -1;
		firstValue = lastValue = null;
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getFirstIndex() {
		return firstIndex;
	}

	public void setFirstIndex(int firstIndex) {
		this.firstIndex = firstIndex;
	}

	public String getFirstValue() {
		return firstValue;
	}

	public void setFirstValue(String firstValue) {
		this.firstValue = firstValue;
	}

	public String getLastValue() {
		return lastValue;
	}

	public void setLastValue(String lastValue) {
		this.lastValue = lastValue;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void addBody(Direction direction, int secs, String body) {
		wrappedMessages.add(new WrappedBody(secs, body, direction));
	}
	
	public Collection<WrappedBody> getContent() {
		return Collections.unmodifiableCollection(wrappedMessages);
	}

	@Override
	protected IQChildElementXmlStringBuilder getIQChildElementBuilder(
			IQChildElementXmlStringBuilder xml) {
		
		/*
		 * 'with' and 'start' attributes should be 
		 * specified. At least Openfire server specifies 
		 * these attributes
		 * */
		
		if(getWith() == null) 
			throw new RuntimeException("'with' attribute wasn't specified");
		
		if(getStart() == null)
			throw new RuntimeException("'start' attribute wasn't specified");
		
		xml.attribute("with", getWith()).
		attribute("start", getStart());
		
		if(getSubject() != null) {
			xml.attribute("subject", getSubject());
		}
		
		if(getVersion() > 0) {
			xml.attribute("version", getVersion());
		}
		
		// empty collection was retrieved
		if(getCount() < 0 || getFirstValue() == null || 
				getFirstIndex() < 0 || getLastValue() == null) {
			
			xml.closeEmptyElement();
			return xml;
		}
		
		xml.rightAngleBracket();
		
		for(final WrappedBody body : getContent()) {
			
			xml.halfOpenElement(body.getDirection().toString().toLowerCase()).
			attribute("xmlns", "urn:xmpp:archive").attribute("secs", body.getSecs()).
			rightAngleBracket();
			
			xml.halfOpenElement("body").
			attribute("xmlns", "urn:xmpp:archive").
			rightAngleBracket();
			
			xml.append(body.getBody()).closeElement("body")
			.closeElement(body.getDirection().toString().
			toLowerCase());
		}
		
		xml.halfOpenElement("set").
		attribute("xmlns", "http://jabber.org/protocol/rsm").
		rightAngleBracket();
		
		xml.halfOpenElement("first").
		attribute("xmlns", "http://jabber.org/protocol/rsm").
		attribute("index", getFirstIndex()).rightAngleBracket().
		append(getFirstValue()).
		closeElement("first");
		
		xml.halfOpenElement("last").
		attribute("xmlns", "http://jabber.org/protocol/rsm").
		rightAngleBracket().
		append(getLastValue()).closeElement("last");
		
		xml.halfOpenElement("count").
		attribute("xmlns", "http://jabber.org/protocol/rsm").rightAngleBracket().
		append(String.valueOf(getCount())).closeElement("count");
		
		xml.closeElement("set");
		
		return xml;
	}

	@Override
	public String toString() {
		return "ChatListRetrieveResultIQ [with=" + with + ", start=" + start
				+ ", subject=" + subject + ", version=" + version
				+ ", firstIndex=" + firstIndex + ", firstValue=" + firstValue
				+ ", lastValue=" + lastValue + ", count=" + count 
				+ ", wrappedMessages=" + wrappedMessages + "]";
	}

}
