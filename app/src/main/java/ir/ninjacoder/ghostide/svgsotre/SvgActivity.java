package ir.ninjacoder.ghostide.svgsotre;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import ir.ninjacoder.ghostide.svgsotre.adapter.SvgAdapter;
import ir.ninjacoder.ghostide.svgsotre.databinding.ActivitySvgBinding;
import ir.ninjacoder.ghostide.svgsotre.model.SvgItem;
import ir.ninjacoder.ghostide.svgsotre.tasks.SvgExtractor;

import java.lang.ref.WeakReference;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;
import ir.ninjacoder.ghostide.svgsotre.utils.DownloadUtils;
import java.util.ArrayList;
import java.util.List;

public class SvgActivity extends AppCompatActivity implements SvgAdapter.OnItemClickListener {
  private SvgAdapter adapter;
  private ActivitySvgBinding bind;
  private static final int STORAGE_PERMISSION_CODE = 101;
  private List<SvgItem> popularItems = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bind = ActivitySvgBinding.inflate(getLayoutInflater());
    setContentView(bind.getRoot());

    setupViews();
    loadPopularIcons();
  }

  private void setupViews() {

    bind.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    adapter = new SvgAdapter(new ArrayList<>(), this);
    bind.recyclerView.setAdapter(adapter);
    bind.searchInput.setHint("search icon");
            bind.fab.setOnClickListener(c ->{
                String arg0 = bind.searchInput.getEditText().getText().toString();
                if (!arg0.toString().isEmpty()) {
                  searchIcons(arg0.toString());
                }
            });
  }

  private void loadPopularIcons() {
    bind.progressBar.setVisibility(View.VISIBLE);
    new AsyncTask<Void, Void, List<SvgItem>>() {
      @Override
      protected List<SvgItem> doInBackground(Void... voids) {
        try {
          // دریافت آیکون‌های پرطرفدار از صفحه اول
          String html = SvgExtractor.fetchSvgRepoPage("popular", 1);
          return SvgExtractor.extractSvgItems(html);
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }

      @Override
      protected void onPostExecute(List<SvgItem> result) {
        bind.progressBar.setVisibility(View.GONE);
        if (result != null && !result.isEmpty()) {
          popularItems = result;
          adapter.updateData(result);
        }
      }
    }.execute();
  }

  private void searchIcons(String query) {
    bind.progressBar.setVisibility(View.VISIBLE);
    new AsyncTask<String, Void, List<SvgItem>>() {
      @Override
      protected List<SvgItem> doInBackground(String... queries) {
        try {
          String html = SvgExtractor.fetchSvgRepoPage(queries[0], 1);
          return SvgExtractor.extractSvgItems(html);
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }

      @Override
      protected void onPostExecute(List<SvgItem> result) {
        bind.progressBar.setVisibility(View.GONE);
        if (result != null && !result.isEmpty()) {
          adapter.updateData(result);
        } else {
          // نمایش مجدد آیکون‌های پرطرفدار اگر نتیجه‌ای یافت نشد
          adapter.updateData(popularItems);
        }
      }
    }.execute(query);
  }

  @Override
  public void onItemClick(SvgItem item) {
    // عملیات دانلود یا نمایش جزئیات
    new DownloadSvgTask(this).execute(item);
  }

  // در کلاس MainActivity، متد onDownloadClick را به این صورت تغییر دهید
  @Override
  public void onDownloadClick(SvgItem item) {
    // بررسی مجوزهای ذخیره‌سازی
    if (checkStoragePermission()) {
      new DownloadSvgTask(this).execute(item);
    } else {
      requestStoragePermission();
    }
  }

  // اضافه کردن متدهای بررسی مجوز
  private boolean checkStoragePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_GRANTED;
    }
    return true;
  }

  private void requestStoragePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requestPermissions(
          new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
  }

  class DownloadSvgTask extends AsyncTask<SvgItem, Void, Boolean> {
    private WeakReference<Context> contextRef;
    private SvgItem item;

    DownloadSvgTask(Context context) {
      this.contextRef = new WeakReference<>(context);
    }

    @Override
    protected Boolean doInBackground(SvgItem... items) {
      item = items[0];
      Context context = contextRef.get();
      if (context != null) {
        DownloadUtils.downloadSvg(context, item);
        return true;
      }
      return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
      Context context = contextRef.get();
      if (context != null && !success) {
        Toast.makeText(context, "خطا در دانلود فایل", Toast.LENGTH_SHORT).show();
      }
    }
  }
}
