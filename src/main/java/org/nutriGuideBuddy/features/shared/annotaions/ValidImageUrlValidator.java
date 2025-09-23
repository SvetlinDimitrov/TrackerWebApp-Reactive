package org.nutriGuideBuddy.features.shared.annotaions;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ValidImageUrlValidator implements ConstraintValidator<ValidImageUrl, String> {

  private static final Set<String> IMAGE_EXTS =
      Set.of("png", "jpg", "jpeg", "gif", "bmp", "svg", "webp", "avif", "heic", "heif");

  // Extension-less image CDNs that serve images by opaque IDs (no file extension)
  // Add more if you need them later.
  private static final Set<String> EXTENSIONLESS_HOSTS =
      Set.of(
          // Syndigo
          "assets.syndigo.cloud",
          "assets.syndigo.com",

          // Unsplash
          "images.unsplash.com",
          "plus.unsplash.com",

          // Google (Photos/Drive/Avatar caches)
          "lh3.googleusercontent.com",
          "lh4.googleusercontent.com",
          "lh5.googleusercontent.com",
          "lh6.googleusercontent.com",
          "googleusercontent.com", // catch-all fallback
          "drive.google.com", // /uc?id=... images
          "docs.google.com", // embedded images

          // Contentful
          "images.ctfassets.net",
          "downloads.ctfassets.net",

          // Prismic
          "images.prismic.io",

          // Hygraph (GraphCMS)
          "media.graphassets.com",
          "media.graphcms.com",

          // Twitter/X & Instagram/Facebook
          "pbs.twimg.com",
          "abs.twimg.com",
          "scontent.cdninstagram.com",
          "scontent.xx.fbcdn.net",
          "graph.facebook.com", // /{id}/picture

          // GitHub avatars
          "avatars.githubusercontent.com",

          // WordPress Jetpack CDN
          "i0.wp.com",
          "i1.wp.com",
          "i2.wp.com",

          // Wix
          "static.wixstatic.com",

          // LinkedIn
          "media.licdn.com");

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) return true; // optional field

    final URI uri;
    try {
      uri = new URI(value);
    } catch (URISyntaxException e) {
      return false;
    }

    // Scheme: only http/https
    final String scheme = lower(uri.getScheme());
    if (!("http".equals(scheme) || "https".equals(scheme))) return false;

    // Host must exist; normalize to ASCII (supports internationalized domains)
    final String host = uri.getHost();
    if (host == null || host.isBlank()) return false;
    final String asciiHost;
    try {
      asciiHost = IDN.toASCII(host);
    } catch (Exception e) {
      return false;
    }

    // If host is known extension-less CDN, accept
    if (EXTENSIONLESS_HOSTS.contains(asciiHost)) {
      return true;
    }

    // If path ends with a known image extension -> accept
    String path = uri.getPath();
    if (path != null) {
      int dot = path.lastIndexOf('.');
      if (dot >= 0 && dot < path.length() - 1) {
        String ext = lower(path.substring(dot + 1));
        if (IMAGE_EXTS.contains(ext)) return true;
      }
    }

    // Otherwise, check query params like ?format=webp or ?ext=png
    // (Also tolerates format=jpeg, image/webp not supported here without network)
    Map<String, String> q = parseQuery(uri.getRawQuery());
    String format = lower(q.get("format"));
    String ext = lower(q.get("ext"));
    if ((format != null && IMAGE_EXTS.contains(format))
        || (ext != null && IMAGE_EXTS.contains(ext))) {
      return true;
    }

    return false;
  }

  private static String lower(String s) {
    return s == null ? null : s.toLowerCase(Locale.ROOT);
  }

  // Tiny query parser (no decoding needed for our keys)
  private static Map<String, String> parseQuery(String rawQuery) {
    if (rawQuery == null || rawQuery.isBlank()) return Map.of();
    String[] parts = rawQuery.split("&");
    java.util.HashMap<String, String> map = new java.util.HashMap<>(parts.length * 2);
    for (String p : parts) {
      int eq = p.indexOf('=');
      if (eq > 0 && eq < p.length() - 1) {
        String k = p.substring(0, eq);
        String v = p.substring(eq + 1);
        map.put(k, v);
      } else if (eq < 0 && !p.isBlank()) {
        map.put(p, "");
      }
    }
    return map;
  }
}
