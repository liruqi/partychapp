package com.imjasonh.partychapp.urlinfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.imjasonh.partychapp.Configuration;

/**
 * {@link UrlInfoService} implementation that uses Embedly's oEmbed interface
 * (http://api.embed.ly/docs/oembed) to extract metadata about a URL. Embedly
 * only handles a subset of URLs, so this service should be combined with 
 * another {@link UrlInfoService} implementation via {@link
 * ChainedUrlInfoService}.
 * 
 * @author mihai.parparita@gmail.com (Mihai Parparita)
 */
public class EmbedlyUrlInfoService implements UrlInfoService {
  private static final Logger logger = Logger.getLogger(EmbedlyUrlInfoService.class.getName());

  private static final URLFetchService URL_FETCH_SERVICE =
    URLFetchServiceFactory.getURLFetchService();
  private static final FetchOptions FETCH_OPTIONS = FetchOptions.Builder
      .allowTruncate()
      .followRedirects()
      .setDeadline(60.0);

  private static final String OEMBED_REQUEST_TEMPLATE =
      "http://api.embed.ly/1/oembed?key=%s&url=%s";

  @Override
  public UrlInfo getUrlInfo(URI url) {
    String embedlyKey = Configuration.persistentConfig().embedlyKey();
    
    if (embedlyKey == null) {
      logger.warning("No Embedly key is configured");      
      return UrlInfo.EMPTY;
    }
    
    String oembedRequestUri;
    try {
      oembedRequestUri = String.format(
            OEMBED_REQUEST_TEMPLATE,
            URLEncoder.encode(embedlyKey, "UTF-8"),
            URLEncoder.encode(url.toString(), "UTF-8"));
    } catch (UnsupportedEncodingException err) {
      // UTF-8 is always supported
      throw new RuntimeException(err);
    }
    
    String oembedResponse = getUrlContents(oembedRequestUri);
    if (oembedResponse == null)
      return UrlInfo.EMPTY;
    
    try {
      JSONObject oembedJson = new JSONObject(oembedResponse);
      return new UrlInfo(
          oembedJson.optString("title"), oembedJson.optString("description"));
    } catch (JSONException err) {
      logger.log(Level.WARNING, "Could not parse oEmbed response", err);
    }
      
    return UrlInfo.EMPTY;
  }
  
  private static String getUrlContents(String url) {
    try {
      HTTPRequest request;
          request = new HTTPRequest(new URL(url), HTTPMethod.GET, FETCH_OPTIONS);
      HTTPResponse response = URL_FETCH_SERVICE.fetch(request);
      
      if (response.getResponseCode() >= 300) {
        logger.warning(response.getResponseCode() +
            " response when fetching " + url + " from Embedly");
        return null;
      }

      return new String(response.getContent(), Charset.forName("UTF-8"));
    } catch (MalformedURLException er) {
      logger.warning("Malformed URL: " + url);
    } catch (IOException err) {
      logger.log(Level.WARNING, "Could not fetch: " + url, err); 
    }
    
    return null;
  }

}
