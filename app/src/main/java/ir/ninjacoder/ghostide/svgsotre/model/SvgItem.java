package ir.ninjacoder.ghostide.svgsotre.model;

public class SvgItem {
  private String name;
  private String url;
  private String svgContent;
  private String fileName;

  public SvgItem(String name, String url, String svgContent, String fileName) {
    this.name = name;
    this.url = url;
    this.svgContent = svgContent;
    this.fileName = fileName;
  }

  // Getters and Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getSvgContent() {
    return svgContent;
  }

  public void setSvgContent(String svgContent) {
    this.svgContent = svgContent;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
}
