package com.imjasonh.partychapp.server.command;

import junit.framework.TestCase;

import com.imjasonh.partychapp.Channel;
import com.imjasonh.partychapp.Datastore;
import com.imjasonh.partychapp.FakeDatastore;
import com.imjasonh.partychapp.Message;
import com.imjasonh.partychapp.MockMailService;
import com.imjasonh.partychapp.MockXMPPService;
import com.imjasonh.partychapp.server.MailUtil;
import com.imjasonh.partychapp.server.SendUtil;

public class SummonHandlerTest extends TestCase {
  SummonHandler handler = new SummonHandler();
  MockXMPPService xmpp = new MockXMPPService();
  MockMailService mailer = new MockMailService();
  
  public void setUp() {
    Datastore.setInstance(new FakeDatastore());
    SendUtil.setXMPP(xmpp);
    MailUtil.setMailService(mailer);
  }
  
  public void testMatches() {
    assertTrue(handler.matches(Message.createForTests("/summon jason")));
    assertTrue(handler.matches(Message.createForTests(" /summon ")));
    assertTrue(handler.matches(Message.createForTests("/summon jason anything")));
  }
  
  public void testSummonSomeone() {
    handler.doCommand(Message.createForTests("/summon jason"));
    assertEquals(2, xmpp.messages.size());
    assertEquals("[neil] /summon jason", xmpp.messages.get(0).getBody());
    assertEquals("_neil summoned jason_", xmpp.messages.get(1).getBody());
    
    assertEquals(1, mailer.sentMessages.size());
    assertEquals("neil has summoned you to 'pancake'.",
                 mailer.sentMessages.get(0).getTextBody());
    assertEquals("You have been summoned to 'pancake'",
                 mailer.sentMessages.get(0).getSubject());
    assertEquals("partychat@gmail.com",
                 mailer.sentMessages.get(0).getSender());
  }
  
  public void testSummonUnknownAlias() {
    handler.doCommand(Message.createForTests("/summon fdsakfj"));
    assertEquals(2, xmpp.messages.size());
    assertEquals("[neil] /summon fdsakfj", xmpp.messages.get(0).getBody());
    assertEquals("Could not find member with alias 'fdsakfj.'", xmpp.messages.get(1).getBody());
    
    assertEquals(0, mailer.sentMessages.size());
  }
  
  public void testDidYouMean1() {
    handler.doCommand(Message.createForTests("/summon jaso"));
    assertEquals(2, xmpp.messages.size());
    assertEquals("[neil] /summon jaso", xmpp.messages.get(0).getBody());
    assertEquals("Could not find member with alias 'jaso.' Maybe you meant to /summon jason.", xmpp.messages.get(1).getBody());
    
    assertEquals(0, mailer.sentMessages.size());
  }

  public void testDidYouMean2() {
    Channel c = FakeDatastore.instance().fakeChannel();
    c.getMemberByAlias("jason").setAlias("intern");
    c.put();
    
    handler.doCommand(Message.createForTests("/summon jason"));
    assertEquals(2, xmpp.messages.size());
    assertEquals("[neil] /summon jason", xmpp.messages.get(0).getBody());
    assertEquals("Could not find member with alias 'jason.' Maybe you meant to /summon intern.", xmpp.messages.get(1).getBody());
    
    assertEquals(0, mailer.sentMessages.size());
  }
  
  public void testException() {
    mailer.setThrowException();
    handler.doCommand(Message.createForTests("/summon jason"));
    assertEquals(2, xmpp.messages.size());
    assertEquals("[neil] /summon jason", xmpp.messages.get(0).getBody());
    assertEquals("Error while sending mail to 'jason@gmail.com'. Email may not have been sent.",
                 xmpp.messages.get(1).getBody());
  }
}