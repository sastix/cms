import 'dart:async';

import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';
import 'package:fclient/modules/cms_resources/dtos/create_cms_resource_request_dto.dart';
import 'package:fclient/modules/cms_resources/dtos/delete_cms_resource_request_dto.dart';
import 'package:fclient/modules/cms_resources/models/cms_resource.dart';
import 'package:fclient/modules/cms_resources/repositories/cms_resource_repository.dart';

part 'resources_query_event.dart';
part 'resources_query_state.dart';

class ResourcesQueryBloc
    extends Bloc<ResourcesQueryEvent, ResourcesQueryState> {
  final CMSResourceRepository cmsResourceRepository;

  ResourcesQueryBloc({required this.cmsResourceRepository})
      : super(ResourcesQueryStateInitial());

  @override
  Stream<ResourcesQueryState> mapEventToState(
    ResourcesQueryEvent event,
  ) async* {
    if (event is ResourcesQueryStarted) {
      yield ResourcesQueryStateInProgress();
      List<CMSResource> resources = await cmsResourceRepository.getResources();
      yield ResourcesQueryStateSuccess(resources: resources);
    } else if (event is ResourcesQueryFinished) {
      yield ResourcesQueryStateSuccess(resources: event.resources);
    } else if (event is ResourcesQueryFailed) {
      yield ResourcesQueryStateFailed(error: event.error);
    } else if (event is ResourcesDeleteQueryStarted) {
      yield ResourcesQueryStateInProgress();
      CMSResource resource = await cmsResourceRepository.deleteResource(
          DeleteCMSResourceRequestDTO(resourceUID: event.resourceUID));
      yield ResourcesDeleteQueryStateSuccess(resource: resource);
      List<CMSResource> resources = await cmsResourceRepository.getResources();
      yield ResourcesQueryStateSuccess(resources: resources);
    } else if (event is ResourcesCreateQueryStarted) {
      yield ResourcesCreateQueryStateInProgress();
      CMSResource resource = await cmsResourceRepository
          .saveResource(event.createCMSResourceRequestDTO);
      yield ResourcesCreateQueryStateSuccess(resource: resource);
      List<CMSResource> resources = await cmsResourceRepository.getResources();
      yield ResourcesQueryStateSuccess(resources: resources);
    } else if (event is ResourcesCreateQueryFailed) {
      yield ResourcesCreateQueryStateFailed(error: event.error);
    }
  }
}
