import 'package:fclient/modules/cms_resources/dtos/create_cms_resource_request_dto.dart';
import 'package:fclient/modules/cms_resources/dtos/delete_cms_resource_request_dto.dart';
import 'package:fclient/modules/cms_resources/models/cms_resource.dart';
import 'package:fclient/modules/cms_resources/repositories/resources_api.dart';

class CMSResourceRepository {
  ResourcesAPI resourcesAPI = ResourcesAPI();

  Future<CMSResource> saveResource(
      CreateCMSResourceRequestDTO createCMSResourceRequestDTO) {
    return resourcesAPI.createResource(createCMSResourceRequestDTO);
  }

  Future<List<CMSResource>> getResources() {
    return resourcesAPI.getResourcesFromAPI();
  }

  Future<CMSResource> deleteResource(
      DeleteCMSResourceRequestDTO deleteCMSResourceRequestDTO) {
    return resourcesAPI.deleteResource(deleteCMSResourceRequestDTO);
  }
}
