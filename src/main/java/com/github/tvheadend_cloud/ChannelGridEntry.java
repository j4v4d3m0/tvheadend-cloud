package com.github.tvheadend_cloud;

import java.util.List;

public class ChannelGridEntry {
  public String uuid;
  public boolean enabled;
  public boolean autoname;
  public String name;
  public int number;
  public boolean epgauto;
  public Object epggrab;
  public long dvr_pre_time;
  public long dvr_pst_time;
  public int epg_running;
  public List<?> services;
  public List<?> tags;
  public String bouquet;
}
