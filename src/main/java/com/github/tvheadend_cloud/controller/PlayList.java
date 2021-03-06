package com.github.tvheadend_cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.tvheadend_cloud.TvheadendService;

@RestController
public class PlayList {

  @Autowired private TvheadendService tvheadendService;

  @GetMapping("/getPlayList")
  @ResponseBody
  public com.github.tvheadend_cloud.PlayList get() {
    return tvheadendService.getPlayList();
  }
}
