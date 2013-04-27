package com.chart.widget;

import java.util.List;

public class FoldLineHelp
{
  public static final int MaxChannel = 11;
  public static final int MaxLevel = -30;
  public static final int MinChannel = 0;
  public static final int MinLevel = -100;
  public String BSSID;
  public String SSID;
  private boolean active = false;
  public int channel;
  public int color;
  public boolean drawflag = true;
  public int id = 0;
  public int level;
  public int prelevel;
  public List<Integer> pts;
  public int templevel;

  public FoldLineHelp()
  {
  }

  public FoldLineHelp(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, int paramInt4)
  {
    this.BSSID = paramString1;
    this.SSID = paramString2;
    this.level = paramInt1;
    this.color = paramInt2;
    this.channel = paramInt3;
    this.drawflag = paramBoolean;
    this.prelevel = paramInt4;
    this.templevel = paramInt4;
  }

  public FoldLineHelp(List<Integer> paramList, String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    this.pts = paramList;
    this.BSSID = paramString1;
    this.SSID = paramString2;
    this.level = paramInt1;
    this.color = paramInt2;
    this.prelevel = 0;
    this.templevel = 0;
  }

  public FoldLineHelp(List<Integer> paramList, String paramString1, String paramString2, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.pts = paramList;
    this.BSSID = paramString1;
    this.SSID = paramString2;
    this.level = paramInt1;
    this.color = paramInt2;
    this.drawflag = paramBoolean;
    this.prelevel = 0;
    this.templevel = 0;
  }

  public boolean GetActive()
  {
    return this.active;
  }

  public int GetChannel()
  {
    return this.channel;
  }

  public void SetActive(boolean paramBoolean)
  {
    this.active = paramBoolean;
  }

  public void SetChannel(int paramInt)
  {
    this.channel = paramInt;
  }

  public void SetLevel(int paramInt)
  {
    this.prelevel = this.level;
    this.templevel = this.prelevel;
    this.level = paramInt;
  }

  public String toString()
  {
    return "color" + this.color + " pts" + this.pts.toString();
  }
}

/* Location:           E:\android\安装apk\jdgui\classes_dex2jar.jar
 * Qualified Name:     com.chart.widget.FoldLineHelp
 * JD-Core Version:    0.5.4
 */