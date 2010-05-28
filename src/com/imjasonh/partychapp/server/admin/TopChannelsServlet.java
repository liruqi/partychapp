package com.imjasonh.partychapp.server.admin;

import com.imjasonh.partychapp.ChannelStats;
import com.imjasonh.partychapp.ChannelStats.ChannelStat;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Dumps top channels by bytes sent.
 * 
 * @author mihai.parparita@gmail.com (Mihai Parparita)
 */
public class TopChannelsServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setContentType("text/html");
    Writer writer = resp.getWriter();

    ChannelStats stats = ChannelStats.getCurrentStats();
    
    if (stats == null) {
      writer.write("No stats found");
      return;
    }

    writer.write("Since: " + stats.getCreationDate() + "<br>");
    writer.write("As of: " + stats.getLastUpdateDate() + "<br>");
    writer.write("Total byte count: " + stats.getTotalByteCount() + "<br>");
    
    writer.write("<table>");
    writer.write("<tr><th>Channel Name</th><th>Outgoing byte count</th></tr>");
    
    for (ChannelStat stat : stats.getTopChannels()) {
      String htmlChannelName = stat.getChannelName()
          .replaceAll("&", "&amp;")
          .replaceAll("<", "&lt;")
          .replaceAll(">", "&gt;");
      writer.write("<tr>");
      
      writer.write("<td>");
      writer.write("<a href=\"/admin/channel/" + 
          htmlChannelName + "\">" + htmlChannelName + "</a>");
      writer.write("</td>");
      
      writer.write("<td>");
      writer.write(Integer.toString(stat.getByteCount()));
      writer.write("</td>");
      
      writer.write("</tr>");
    }
    
    writer.write("</table>");
  }
}