package ir.ninjacoder.ghostide.svgsotre.tasks;

import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class HttpUtils {
  private static final int MAX_CONNECTIONS = 50;
  private static final long TIMEOUT = 5; // 5 seconds

  public static final OkHttpClient client =
      new OkHttpClient.Builder()
          .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
          .readTimeout(TIMEOUT, TimeUnit.SECONDS)
          .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
          .followRedirects(true)
          .followSslRedirects(true)
          .build();
}
