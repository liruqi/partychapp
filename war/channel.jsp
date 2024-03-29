<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.imjasonh.partychapp.Configuration"%>
<%@ page import="com.imjasonh.partychapp.Channel"%>
<%@ page import="com.imjasonh.partychapp.Datastore"%>
<%@ page import="com.imjasonh.partychapp.Member"%>
<%@ page import="com.imjasonh.partychapp.ppb.Reason"%>
<%@ page import="com.imjasonh.partychapp.ppb.Target"%>

<!DOCTYPE html>
<html>
<head>
<script>if (window.name == 'partychat-share') window.close();</script>
<%
  Channel channel = (Channel) request.getAttribute("channel");
  Datastore datastore = Datastore.instance();
%>
<jsp:include page="include/head.jsp">
  <jsp:param name="subtitle" value="<%=&quot;Room &quot; + channel.getName()%>"/>
</jsp:include>
</head>
<body class="channel">
  <jsp:include page="include/header.jsp">
    <jsp:param name="subtitle" value="<%=channel.getName()%>"/>
  </jsp:include>

<p>
  You're in the room <b><%=channel.getName()%></b> along with the people below.
  You can  <a href="#invite-section">invite more people</a> or
  <a href="/channel/leave?name=<%=channel.getName()%>">leave</a> the room.
</p>

<jsp:include page="include/channel-members.jsp"/>

<h3>Helpful links</h3>

<p>Send messages to this room by chatting with <a href="xmpp:<%=channel.serverJIDAsString()%>"><%=channel.serverJIDAsString()%></a>.</p>

<p>When you're on the go, you can also email them to <a href="mailto:<%=channel.mailingAddress()%>"><%=channel.mailingAddress()%></a> (as long as the account you're emailing from is in the room).</p>

<p>If you'd like to share links with this room, you can drag the <a href="javascript:window.open('http://<%=Configuration.webDomain%>/room/share?name=<%=channel.getName()%>&url='+escape(location.href),'partychat-share','scrollbars=no,width=700,height=315,top=175,left=75,status=yes,resizable=yes');void(0);
" class="bookmarklet" onclick="alert('Drag this link to your bookmarks bar');return false;">Share with <%=channel.getName()%></a> link to your bookmarks bar and press it when you're on the page you want to share.</p>

<h3>Settings</h3>

<form action="/channel/edit" method="POST">
<input type="hidden" name="name" value="<%=channel.getName()%>"/>
<table id="channel-settings-table" class="channel-form-table">
  <tr>
    <td class="label">Room type:</td>
    <td>
      <table class="radio-option-table">
        <tr>
          <td><input type="radio" name="inviteonly" value="true" <% if (channel.isInviteOnly()) {out.print("checked");} %> id="inviteonly-true"></td>
          <td><label for="inviteonly-true">Invite-only</label></td>
        </tr>
        <tr class="description">
          <td></td>
          <td>Only invited people may join</td>
        </tr>
      </table>

      <table class="radio-option-table">
        <tr>
          <td><input type="radio" name="inviteonly" value="false" <% if (!channel.isInviteOnly()) {out.print("checked");} %> id="inviteonly-false"></td>
          <td><label for="inviteonly-false">Open</label></td>
        </tr>
        <tr class="description">
          <td></td>
          <td>Anyone may join</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td class="label">Logging:</td>
    <td>
      <table class="radio-option-table">
        <tr>
          <td><input type="radio" name="logging" value="true" <% if (!channel.isLoggingDisabled()) {out.print("checked");} %> id="logging-true"></td>
          <td><label for="logging-true">Enabled</label></td>
        </tr>
        <tr class="description">
          <td></td>
          <td>Recent messages are recorded for <a href="/about/faq#search-and-replace">search-and-replace</a> and <a href="/about/faq#commands">/undo</a></td>
        </tr>
      </table>

      <table class="radio-option-table">
        <tr>
          <td><input type="radio" name="logging" value="false" <% if (channel.isLoggingDisabled()) {out.print("checked");} %> id="logging-false"></td>
          <td><label for="logging-false">Disabled</label></td>
        </tr>
        <tr class="description">
          <td></td>
          <td>No messages are logged, see <a href="/about/faq#logging">the FAQ</a> for more details on logging</td>
        </tr>
      </table>
    </td>
  </tr>

  <tr>
    <td class="buttons" colspan="2">
      <input type="submit" value="Save settings">
    </td>
  </tr>
</table>
</form>

<h3>PlusPlusBot</h3>

<div id="score-table"></div>

<h3 id="invite-section">Invite People!</h3>

<table>
  <tr>
    <td>
    <form action="/channel/invite" method="post" target="inviteResults">
    <input type="hidden" name="name" value="<%=channel.getName()%>"/>
    Email addresses you would like to invite? (separated by commas)<br>
    <textarea name="invitees" rows="4" cols="40"></textarea> <br>
    <br>
    <input type="submit" value="Invite!"></form>
    </td>
    <td><iframe frameborder=0 name="inviteResults"> </iframe></td>
  </tr>
</table>
<script>
  new partychapp.ScoreTable('<%= channel.getName() %>',
                            <%= (String) request.getAttribute("targetInfo") %>);
</script>

<jsp:include page="include/footer.jsp"/>
</body>
</html>
