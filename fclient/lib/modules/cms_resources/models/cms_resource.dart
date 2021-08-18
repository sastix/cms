import 'dart:convert';

class CMSResource {
  final String resourceUID;
  final String author;
  final String resourceURI;
  final String mediaType;
  final String name;
  List<String> resourcesList = [];

  CMSResource(
      {required this.resourceUID,
      required this.author,
      required this.resourceURI,
      required this.mediaType,
      required this.name});

  static CMSResource fromCMSJson(json) {
    CMSResource resource = CMSResource(
        resourceUID: json['resourceUID'],
        author: json['author'],
        resourceURI: json['resourceURI'],
        mediaType: json['mediaType'],
        name: json['name']);
    if (json['resourcesList'] != null) {
      resource.resourcesList =
          (jsonDecode(json['resourcesList']) as List<dynamic>).cast<String>();
    }
    return resource;
  }

  @override
  String toString() {
    return """CMSResource(resourceUID: $resourceUID,
      author: $author,
      resourceURI: $resourceURI,
      resourcesList: $resourcesList)
      mediaType: $mediaType""";
  }
}
