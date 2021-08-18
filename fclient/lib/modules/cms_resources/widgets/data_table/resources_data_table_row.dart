class ResourcesDataTableRow {
  final String resourceUID;
  final String resourceURI;
  final String author;
  final String mediaType;
  final String name;

  ResourcesDataTableRow(this.resourceUID, this.resourceURI, this.author, this.mediaType, this.name);

  bool selected = false;
}