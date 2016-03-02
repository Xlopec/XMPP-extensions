package xmpp.extensions.com.ua.max.oliynick.iq;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xmpp.extensions.com.ua.max.oliynick.provider.ChatListResponceIQProvider;
import xmpp.extensions.com.ua.max.oliynick.provider.ChatListRetrieveIQProvider;

/**
 * <p>
 * Tests {@link ChatListRetrieveRequestIQ}, {@link ChatListRetrieveResultIQ}
 * and {@link ChatListRetrieveIQProvider} classes
 * which are part of <a href="http://xmpp.org/extensions/xep-0136.html">XEP-0136</a>
 * extesion
 * </p>
 * Created 02.03.16
 * @author Max Oliynick
 * */
public final class TestChatListRetrieveIQ {

	// Default server host
	public static final String defaultHost = "localhost";
	// Default server port
	public static final int defaultPort = 5222;
	// Default server service name
	public static final String defaultServiceName = "maxlaptop";
	// Default packet responce timeout
	public static final int defaultTimeout = 3000;

	// Test xmpp connection instance
	private static XMPPTCPConnection connection = null;

	/**
	 * Initializes connection instance and allocates resources
	 * */
	@BeforeClass
	public static void prepareConnection() throws SmackException, IOException,
			XMPPException {

		// Building server connection configuration
		final XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration
				.builder();
		builder.setHost(defaultHost).setServiceName(defaultServiceName)
				.setPort(defaultPort).setCompressionEnabled(false)
				.setSendPresence(true).setConnectTimeout(defaultTimeout)
				.setDebuggerEnabled(true);

		// IQ provider registration
		ProviderManager.addIQProvider(ChatListResponceIQProvider.elementName,
				ChatListResponceIQProvider.namespace,
				new ChatListResponceIQProvider());
		ProviderManager.addIQProvider(ChatListRetrieveIQProvider.elementName,
				ChatListRetrieveIQProvider.namespace,
				new ChatListRetrieveIQProvider());
		
		connection = new XMPPTCPConnection(builder.build());
		connection.connect();
		connection.login("maxxx", "qwerty");
	}

	@Test
	public void testResponce() throws NotConnectedException, InterruptedException {
		
		final long timeout = 3000;
		final TestStanzaListener listener = new TestStanzaListener() {
			
			// Response stanza
			private Stanza stanza = null;
			
			// Exception in case of runtime exception
			private Exception exception = null;
			
			@Override
			public Stanza getStanza() {
				return stanza;
			}
			
			@Override
			public Exception getException() {
				return exception ;
			}

			@Override
			public synchronized void processPacket(final Stanza stanza) throws NotConnectedException {
				//	response class should be AvailableChatsIQ
				this.stanza = stanza;
				notify();
			}
			
			@Override
			public synchronized void processException(final Exception exception) {
				this.exception = exception;
				notify();
			}
			
		};
		
		final ChatListRetrieveRequestIQ iq = new ChatListRetrieveRequestIQ("mary@maxlaptop");
		iq.setMax(30);
		
		connection.sendIqWithResponseCallback(iq, listener, listener, timeout);
		
		synchronized(listener) {
			listener.wait();
				
			if(listener.getException() != null) {
				// an error occurred while waiting for response
				fail("Test failed with message: ".concat(listener.getException().getMessage()));
			} else {
				// response should be an instance of AvailableChatsIQ class
				assertTrue("Unknown class", listener.getStanza() instanceof ChatListRetrieveResultIQ);
				
				final ChatListRetrieveResultIQ stanza = (ChatListRetrieveResultIQ) listener.getStanza();
				
				System.out.println("\n***************************************************");
				System.out.println("\tTestResponce method\n".concat(stanza.toString()));
				System.out.println("\nXML".concat(stanza.toXML().toString()));
				System.out.println("***************************************************\n");
				
				assumeTrue("Empty responce set", stanza.getCount() > 0);
				assumeTrue("First index < 0", stanza.getFirstIndex() >= 0);
				assumeTrue("First value == null", stanza.getFirstValue() != null);
				assumeTrue("Last value == null", stanza.getLastValue() != null);
				
			}
		}
		
	}
	
	@Test
	public void testUnsuccessfulResponce() throws NotConnectedException, InterruptedException {
		
		final long timeout = 3000;
		final TestStanzaListener listener = new TestStanzaListener() {
			
			// Response stanza
			private Stanza stanza = null;
			
			// Exception in case of runtime exception
			private Exception exception = null;
			
			@Override
			public Stanza getStanza() {
				return stanza;
			}
			
			@Override
			public Exception getException() {
				return exception ;
			}

			@Override
			public synchronized void processPacket(final Stanza stanza) throws NotConnectedException {
				//	response class should be AvailableChatsIQ
				this.stanza = stanza;
				notify();
			}
			
			@Override
			public synchronized void processException(final Exception exception) {
				this.exception = exception;
				notify();
			}
			
		};
		
		final ChatListRetrieveRequestIQ iq = new ChatListRetrieveRequestIQ("non_existing_user@maxlaptop");
		iq.setMax(30);
		
		connection.sendIqWithResponseCallback(iq, listener, listener, timeout);
		
		synchronized(listener) {
			listener.wait();
				
			if(listener.getException() != null) {
				// an error occurred while waiting for response
				System.out.println("\n***************************************************");
				System.out.println("\ttestUnsuccessfulResponce method\n".concat(listener.getException().getMessage()));
				System.out.println("***************************************************\n");
				
			} else {
				fail("'item-not-found' exception expected");
			}
		}
		
	}
	
	/**
	 * Closes connection and frees resources
	 * */
	@AfterClass
	public static void tearDownConnection() {
		if(connection != null) {
			connection.disconnect();
		} else {
			fail("Connection wasn't set properly!");
		}
	}

}
