package ir.ninjacoder.ghostide.svgsotre.tasks;


import ir.ninjacoder.ghostide.svgsotre.model.SvgItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SvgExtractor {

    public static List<SvgItem> extractSvgItems(String html) {
        Document doc = Jsoup.parse(html);
        Elements anchors = doc.select("a[itemType='http://schema.org/ImageObject']");
        
        List<CompletableFuture<SvgItem>> futures = new ArrayList<>();
        OkHttpClient client = HttpUtils.client;
        
        for (Element anchor : anchors) {
            Element img = anchor.selectFirst("img[itemProp=contentUrl]");
            if (img == null) continue;
            
            String rawTitle = anchor.attr("title");
            String name = rawTitle.replace("Show ", "").replace(" SVG File", "").trim();
            String url = img.attr("src");
            
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .header("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                            .build();
                    
                    try (Response response = client.newCall(request).execute()) {
                        if (response.isSuccessful() && response.body() != null) {
                            String svgContent = response.body().string();
                            return new SvgItem(name, url, svgContent, getLastPart(url));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }));
        }
        
        return futures.stream()
                .map(CompletableFuture::join)
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    public static String fetchSvgRepoPage(String searchName, int pageNumber) throws Exception {
        String encodedSearch = URLEncoder.encode(searchName, "UTF-8");
        String url = "https://www.svgrepo.com/vectors/" + encodedSearch + "/" + pageNumber;
        
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                .build();
        
        try (Response response = HttpUtils.client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new Exception("HTTP " + response.code());
            }
        }
    }

    private static String getLastPart(String url) {
        String trimmedUrl = url.trim().replaceAll("/+$", "");
        return trimmedUrl.substring(trimmedUrl.lastIndexOf('/') + 1);
    }
}