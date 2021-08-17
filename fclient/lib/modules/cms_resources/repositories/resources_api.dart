import 'dart:async';
import 'package:dio/dio.dart';
import 'package:fclient/core/constants/cms_server_api_endpoints.dart';
import 'package:fclient/core/utils/rest_client.dart';
import 'package:fclient/modules/cms_resources/dtos/create_cms_resource_request_dto.dart';
import 'package:fclient/modules/cms_resources/dtos/delete_cms_resource_request_dto.dart';
import 'package:fclient/modules/cms_resources/models/cms_resource.dart';

class ResourcesAPI {
  Future<List<CMSResource>> getResourcesFromAPI() async {
    RestClient restClient = new RestClient();
    Map<String, dynamic> data = {};
    data['page'] = 0;
    data['size'] = 2147483647;
    Response response =
        await restClient.dio.post(getResourcesEndpoint, data: data);
    return (response.data as List)
        .map((e) => CMSResource.fromCMSJson(e))
        .toList();
  }

  Future<CMSResource> createResource(
      CreateCMSResourceRequestDTO createCMSResourceRequestDTO) async {
    RestClient restClient = new RestClient();
    Map<String, dynamic> data = {};
    data['resourceAuthor'] = createCMSResourceRequestDTO.resourceAuthor;
    data['resourceExternalURI'] =
        createCMSResourceRequestDTO.resourceExternalURI;
    data['resourceMediaType'] = createCMSResourceRequestDTO.resourceMediaType;
    data['resourceName'] = createCMSResourceRequestDTO.resourceName;
    data['resourceTenantId'] = createCMSResourceRequestDTO.resourceTenantId;
    Response response =
        await restClient.dio.post(createResourceEndpoint, data: data);
    return CMSResource.fromCMSJson(response.data);
  }

  Future<CMSResource> deleteResource(
      DeleteCMSResourceRequestDTO deleteCMSResourceRequestDTO) async {
    RestClient restClient = new RestClient();
    Map<String, dynamic> data = {};
    data["resourceUID"] = deleteCMSResourceRequestDTO.resourceUID;
    Response response =
        await restClient.dio.post(deleteResourceEndpoint, data: data);
    return CMSResource.fromCMSJson(response.data);
  }
}
