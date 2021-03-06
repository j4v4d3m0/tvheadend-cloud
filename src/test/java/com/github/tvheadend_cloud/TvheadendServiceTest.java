package com.github.tvheadend_cloud;

import org.junit.jupiter.api.Test;

class TvheadendServiceTest {

  private TvheadendService create() {
    return new TvheadendService().setUp();
  }

  @Test
  void testGetChannelGrid() {
    create().getChannelGrid();
  }

  @Test
  void testGetChannelList() {
    create().getChannelList();
  }

  @Test
  void testGetPlayList() {
    create().getPlayList();
  }
}
