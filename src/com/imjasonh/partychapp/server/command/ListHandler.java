package com.imjasonh.partychapp.server.command;

import java.util.Collections;
import java.util.List;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.imjasonh.partychapp.Member;
import com.imjasonh.partychapp.Message;
import com.imjasonh.partychapp.Member.SnoozeStatus;
import com.imjasonh.partychapp.server.SendUtil;

public class ListHandler extends SlashCommand {
  
  public ListHandler() {
    super("list", "names");
  }
	
  public void doCommand(Message msg, String argument) {
    // TODO: Reject or act on non-null argument
    
    List<Member> members = Lists.newArrayList(msg.channel.getMembers());
    Collections.sort(members, new Member.SortMembersForListComparator());
    StringBuilder sb = new StringBuilder()
        .append("Listing members of '")
        .append(msg.channel.getName())
        .append("'");
    for (Member m : members) {
      sb.append('\n')
          .append("* ")
          .append(m.getAlias())
          .append(" (")
          .append(m.getJID())
          .append(")");
      if (SendUtil.getPresence(new JID(m.getJID()), msg.channel.serverJID())) {
        sb.append(" (online)");
      }
      if (m.getSnoozeStatus() == SnoozeStatus.SNOOZING) {
        sb.append(" _snoozing_");
      }
      if (m.user().phoneNumber() != null) {
        sb.append(" (");
        sb.append(m.user().phoneNumber());
        if (m.user().carrier() != null) {
          sb.append(" @ " + m.user().carrier().shortName);
        }
        sb.append(")");
      }
    }

    if (msg.channel.isInviteOnly()) {
      sb.append("\nRoom is invite-only.");
    }
    for (String invitee : msg.channel.getInvitees()) {
      sb.append("\nInvited: ").append(invitee);
    }
    msg.channel.sendDirect(sb.toString(), msg.member);
  }
  
  public String documentation() {
	  return "/list - show members of room";
  }
}
