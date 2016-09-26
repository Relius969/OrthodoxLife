package org.telegram.pravzhizn.location.log;

import android.text.TextUtils;

public abstract class Logger
{
  protected String tag = "MYTELEGRAM";

  protected Logger() {}

  protected Logger(String tag)
  {
    this.tag = tag;
  }

  static protected String join(Object... args)
  {
    return (args != null ? TextUtils.join(", ", args) : "");
  }

  public abstract void d(String message);

  public abstract void e(String message);

  public abstract void d(String message, Object... args);

  public abstract void e(String message, Object... args);
}
