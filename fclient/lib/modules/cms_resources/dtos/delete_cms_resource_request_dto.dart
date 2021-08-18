class DeleteCMSResourceRequestDTO {
  final String resourceUID;
  final String author;
  final String resourceURI;
  final String mediaType;
  final String name;

  DeleteCMSResourceRequestDTO(
      {required this.author,
      required this.resourceURI,
      required this.mediaType,
      required this.name,
      required this.resourceUID});

  @override
  String toString() {
    return """CMSResource(resourceUID: $resourceUID,
      author: $author,
      resourceURI: $resourceURI,
      mediaType: $mediaType""";
  }

  Map<String, dynamic> toMap() {
    return {
      'resourceUID': resourceUID,
      'author': author,
      'resourceURI': resourceURI,
      'mediaType': mediaType,
      'name': name,
    };
  }

  bool validate() {
    if (resourceUID.isEmpty) {
      return false;
    }
    return true;
  }
}
