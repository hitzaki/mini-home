package com.github.hitzaki.service;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

/**
 * 网站信息获取服务
 */
@Service
@RequiredArgsConstructor
public class WebsiteInfoService {
    
    /**
     * 获取网站标题
     */
    public String getWebsiteTitle(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(5000)
                    .get();
            return doc.title();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取网站图标URL
     */
    public String getWebsiteIcon(String url) {
        try {
            URL urlObj = new URL(url);
            String baseUrl = urlObj.getProtocol() + "://" + urlObj.getHost();
            
            // 尝试获取favicon
            String[] faviconPaths = {
                baseUrl + "/favicon.ico",
                baseUrl + "/favicon.png",
                baseUrl + "/apple-touch-icon.png"
            };
            
            // 先尝试直接访问favicon.ico
            for (String faviconPath : faviconPaths) {
                if (checkUrlExists(faviconPath)) {
                    return faviconPath;
                }
            }
            
            // 尝试从HTML中解析
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(5000)
                        .get();
                
                Elements links = doc.select("link[rel~=(?i)^(shortcut )?icon$]");
                if (!links.isEmpty()) {
                    String href = links.first().attr("href");
                    if (href.startsWith("http")) {
                        return href;
                    } else if (href.startsWith("//")) {
                        return urlObj.getProtocol() + ":" + href;
                    } else {
                        return baseUrl + (href.startsWith("/") ? href : "/" + href);
                    }
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
            
            return baseUrl + "/favicon.ico";
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 检查URL是否存在
     */
    private boolean checkUrlExists(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", "Mozilla/5.0");
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return response.getStatusLine().getStatusCode() == 200;
            }
        } catch (IOException e) {
            return false;
        }
    }
}

