package com.github.tvheadend_cloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TvheadendService {

  private RestTemplate restTemplate;
  private RestTemplate basicRestTemplate;
  
  private String host =  "http://192.168.1.3:9981";
  private String username = "test";
  private String password = "test";

  public ChannelList getChannelList() {
    return new ObjectMapper()
        .convertValue(
            restTemplate.getForEntity(getChannelListUrl(), Object.class).getBody(),
            ChannelList.class);
  }

  private String getChannelListUrl() {
    return host + "/api/channel/list";
  }

  public ChannelGrid getChannelGrid() {
    return new ObjectMapper()
        .convertValue(
            restTemplate.getForEntity(getChannelGridUrl(), Object.class).getBody(),
            ChannelGrid.class);
  }

  private String getChannelGridUrl() {
    return host + "/api/channel/grid";
  }

  public PlayList getPlayList() {
    return convertToPlayList(
        basicRestTemplate.getForEntity(getPlayListUrl(), String.class).getBody());
  }

  private String getPlayListUrl() {
    return host + "/playlist/channels";
  }

  private PlayList convertToPlayList(String m3u) {
    PlayList playList = new PlayList();
    playList.urls = new HashMap<String, String>();
    ChannelList channels = getChannelList();
    try (Scanner scanner = new Scanner(m3u)) {
      while (scanner.hasNext()) {
        String line = scanner.nextLine();
        if (line.startsWith("#EXTINF:")) {
          channels
              .entries
              .stream()
              .filter(c -> c.key.equals(line.split("\",")[0].split("tvg-id=\"")[1]))
              .findFirst()
              .ifPresent(channel -> playList.urls.put(channel.val, scanner.nextLine()));
        }
      }
    }
    return playList;
  }

  @PostConstruct
  public TvheadendService setUp() {
    restTemplate = new RestTemplate(getClientHttpRequestFactory());
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(createObjectMapper());
    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
    messageConverters.add(converter);
    restTemplate.setMessageConverters(messageConverters);

    basicRestTemplate = new RestTemplate(getClientHttpRequestFactory());
    return this;
  }

  private ObjectMapper createObjectMapper() {

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

    return objectMapper;
  }

  private HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
        new HttpComponentsClientHttpRequestFactory();

    clientHttpRequestFactory.setHttpClient(httpClient());

    return clientHttpRequestFactory;
  }

  private HttpClient httpClient() {
    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    credentialsProvider.setCredentials(
        AuthScope.ANY, new UsernamePasswordCredentials(username, password));

    HttpClient client =
        HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
    return client;
  }
}
