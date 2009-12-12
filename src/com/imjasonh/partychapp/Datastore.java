package com.imjasonh.partychapp;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.users.User;
import com.google.appengine.api.xmpp.JID;
import com.imjasonh.partychapp.ppb.Reason;
import com.imjasonh.partychapp.ppb.Target;

public abstract class Datastore {
  private static Datastore instance;

  public static Datastore instance() {
    if (instance == null) {
      // We have to do this lazily because tests won't have the
      // live datastore dependencies set up
      instance = new FixingDatastore(new LiveDatastore());
    }
    return instance;
  }
  
  public static void setInstance(Datastore ds) {
    instance = new FixingDatastore(ds);
  }
  
  public abstract Channel getChannelByName(String name);

  public abstract Target getTargetByID(String key);
  
  public Target getTarget(Channel channel, String name) {
    return getTargetByID(Target.createTargetKey(name, channel));
  }

  public Target getOrCreateTarget(Channel channel, String name) {
    Target t = getTarget(channel, name);
    if (t == null) {
      t = new Target(name, channel);
    }
    return t;
  }
  
  public List<Target> getTargetsByChannel(Channel channel) {
	  return getTargetsByChannel(channel.getName());
  }
  
  public abstract List<Target> getTargetsByChannel(String channel);

  public abstract List<Reason> getReasons(Target target, int limit);

  public static class Stats {
    public int numChannels;
    public Date timestamp;
  }
  public abstract Stats getStats();
  
  public abstract void putAll(Collection<? extends Serializable> objects);
  public abstract void put(Serializable s);
  public abstract void delete(Serializable s);

  public abstract void startRequest();
  public abstract void endRequest();

  public Channel getChannelFromWeb(User user, String channelName) throws IOException {
	  Channel channel = getChannelByName(channelName);
	  if (channel == null) {
		  // resp.getWriter().write("Sorry, room name is not there");
		  return null;
	  } 

	  if (channel.getMemberByJID(new JID(user.getEmail())) == null) {
		  // resp.getWriter().write("Sorry, you're not in that room.");
		  return null;
	  }
	  return channel;
  }
}