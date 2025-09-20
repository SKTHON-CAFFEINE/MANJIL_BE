package com.skthon.manjil.domain.reco.support;

public class JsonSanitizer {
  public static String extractTopJson(String raw) {
    if (raw == null) return "{}";
    String s = raw.trim();
    if (s.startsWith("```")) {
      s = s.replaceAll("^```[a-zA-Z]*", "").replaceAll("```$", "").trim();
    }
    int i = s.indexOf("{");
    int j = s.lastIndexOf("}");
    if (i >= 0 && j > i) s = s.substring(i, j + 1);
    return s;
  }
}
