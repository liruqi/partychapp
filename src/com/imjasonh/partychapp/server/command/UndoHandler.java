package com.imjasonh.partychapp.server.command;

import java.util.List;

import com.imjasonh.partychapp.Message;
import com.imjasonh.partychapp.Message.MessageType;

public class UndoHandler extends SlashCommand {
  private PPBHandler ppbHandler = new PPBHandler();
  
  public UndoHandler() {
    super("undo");
  }
  
  @Override
  void doCommand(Message msg, String argument) {
    msg.channel.broadcast(msg.member.getAliasPrefix() + msg.content, msg.member);
    List<String> lastMessages = msg.member.getLastMessages();
    if (lastMessages.isEmpty()) {
      String reply = "no last message to undo!";
      msg.channel.broadcastIncludingSender(reply);
      return;
    }

    String toUndo = lastMessages.get(0);
    Message originalMsg = new Message(toUndo, msg.userJID,
                                      msg.serverJID, msg.member, msg.channel, null, MessageType.XMPP);
    ppbHandler.undoEarlierMessage(originalMsg);
  }

  public String documentation() {
    return "/undo - undo the pluspluses and minusminuses from your last message";
  }
}