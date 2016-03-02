package xmpp.extensions.com.ua.max.oliynick.provider;

import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import xmpp.extensions.com.ua.max.oliynick.iq.ChatListResponceIQ;

/**
 * <p>
 * Custom provider that parsers XEP-0136 result history into
 * {@link ChatListResponceIQ} packets
 * </p>
 * @author Max Oliynick
 * */
public final class ChatListResponceIQProvider extends IQProvider<ChatListResponceIQ> {
	
	public static final String elementName = "list";
	public static final String namespace = "urn:xmpp:archive";
	
	private static final String chatTag = "chat";
	private static final String firstTag = "first";
	private static final String lastTag = "last";
	
	@Override
	public ChatListResponceIQ parse(XmlPullParser parser, int depth)
			throws XmlPullParserException, IOException, SmackException {
	
		final ChatListResponceIQ iq = new ChatListResponceIQ();
			
		do {

			final String name = parser.getName();

				switch (parser.getEventType()) {
					case XmlPullParser.START_TAG : {
						
						if (name.equals(chatTag)) {
							iq.appendChat(parser.getAttributeValue(0), parser.getAttributeValue(1));
							
						} else if(name.equals(firstTag)) {
							iq.setFirst(Integer.valueOf(parser.getAttributeValue(0)), parser.nextText());
							
						} else if(name.equals(lastTag)) {
							iq.setLast(parser.nextText());
							
						}
						
						break;
					}
				}
				
			parser.next();
								
		} while(parser.getDepth() != depth);
		
		return iq;
	}

}
