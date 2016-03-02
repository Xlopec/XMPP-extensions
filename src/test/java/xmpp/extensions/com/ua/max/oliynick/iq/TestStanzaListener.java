package xmpp.extensions.com.ua.max.oliynick.iq;

import org.jivesoftware.smack.ExceptionCallback;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Stanza;

/**
 * Custom listener interface for testing purposes
 * */
interface TestStanzaListener extends StanzaListener, ExceptionCallback {

	/**
	 * Returns last stanza which 
	 * was received by {@link StanzaListener}
	 * */
	public Stanza getStanza();
	
	/**
	 * Returns last exception which 
	 * was received by {@link ExceptionCallback}
	 * */
	public Exception getException();

}
