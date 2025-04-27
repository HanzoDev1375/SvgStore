package ir.ninjacoder.ghostide.svgsotre.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.widget.Toast;
import ir.ninjacoder.ghostide.svgsotre.model.SvgItem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadUtils {

  public static void downloadSvg(Context context, SvgItem svgItem) {
    try {
      // تعیین مسیر سفارشی
      File customDir = new File("/storage/emulated/0/GhostWebIDE/.icon/");

      // ایجاد پوشه اگر وجود نداشته باشد
      if (!customDir.exists()) {
        if (!customDir.mkdirs()) {
          Toast.makeText(context, "خطا در ایجاد پوشه مقصد", Toast.LENGTH_SHORT).show();
          return;
        }
      }

      // ایجاد نام فایل با پسوند .svg اگر وجود نداشته باشد
      String fileName =
          svgItem.getFileName().endsWith(".svg")
              ? svgItem.getFileName()
              : svgItem.getFileName() + ".svg";

      File svgFile = new File(customDir, fileName);

      // نوشتن محتوای SVG در فایل
      try (FileOutputStream outputStream = new FileOutputStream(svgFile)) {
        outputStream.write(svgItem.getSvgContent().getBytes());
        outputStream.flush();

        // اطلاع رسانی به سیستم درباره فایل جدید
        MediaScannerConnection.scanFile(
            context,
            new String[] {svgFile.getAbsolutePath()},
            null,
            (path, uri) -> {
              // نمایش پیام موفقیت آمیز بودن دانلود
              Toast.makeText(context, "فایل با موفقیت ذخیره شد:\n" + path, Toast.LENGTH_LONG)
                  .show();
            });
      }
    } catch (IOException e) {
      e.printStackTrace();
      Toast.makeText(context, "خطا در ذخیره فایل: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }
}
