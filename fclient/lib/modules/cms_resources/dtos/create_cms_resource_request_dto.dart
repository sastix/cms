class CreateCMSResourceRequestDTO {
  late String resourceAuthor;
  late String resourceExternalURI;
  late String resourceMediaType;
  late String resourceName;
  late String resourceTenantId;

  @override
  String toString() {
    return """CMSResource(resourceAuthor: $resourceAuthor,
      resourceExternalURI: $resourceExternalURI,
      resourceMediaType: $resourceMediaType,
      resourceTenantId: $resourceTenantId,
      resourceName: $resourceName)""";
  }

  bool validate() {
    if (resourceAuthor.isEmpty ||
        resourceExternalURI.isEmpty ||
        resourceMediaType.isEmpty ||
        resourceName.isEmpty ||
        resourceTenantId.isEmpty) {
      return false;
    }
    return true;
  }
}
