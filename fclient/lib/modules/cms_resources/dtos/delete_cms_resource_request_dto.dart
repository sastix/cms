class DeleteCMSResourceRequestDTO {
  String resourceUID;

  DeleteCMSResourceRequestDTO({required this.resourceUID});

  @override
  String toString() {
    return """CMSResource(resourceAuthor: $resourceUID""";
  }

  bool validate() {
    if (resourceUID.isEmpty) {
      return false;
    }
    return true;
  }
}
