import 'dart:html' as html;

class WebFileDownloader{
  static void downloadFile(String url, String name){
    html.window.open(url, name);
  }
}