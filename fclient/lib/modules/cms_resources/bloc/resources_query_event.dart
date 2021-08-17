part of 'resources_query_bloc.dart';

abstract class ResourcesQueryEvent extends Equatable {
  const ResourcesQueryEvent();

  @override
  List<Object> get props => [];
}

class ResourcesQueryStarted extends ResourcesQueryEvent {}

class ResourcesQueryFinished extends ResourcesQueryEvent {
  final List<CMSResource> resources;

  ResourcesQueryFinished({required this.resources});
}

class ResourcesQueryFailed extends ResourcesQueryEvent {
  final String error;

  ResourcesQueryFailed({required this.error});
}

class ResourcesDeleteQueryStarted extends ResourcesQueryEvent {
  final String resourceUID;

  ResourcesDeleteQueryStarted({required this.resourceUID});
}

class ResourcesDeleteQueryFinished extends ResourcesQueryEvent {
  final String resourceUID;

  ResourcesDeleteQueryFinished({required this.resourceUID});
}

class ResourcesDeleteQueryFailed extends ResourcesQueryEvent {
  final String error;

  ResourcesDeleteQueryFailed({required this.error});
}

class ResourcesCreateQueryStarted extends ResourcesQueryEvent {
  final CreateCMSResourceRequestDTO createCMSResourceRequestDTO;

  ResourcesCreateQueryStarted({required this.createCMSResourceRequestDTO});
}

class ResourcesCreateQueryFinished extends ResourcesQueryEvent {
  final CMSResource cmsResource;

  ResourcesCreateQueryFinished({required this.cmsResource});
}

class ResourcesCreateQueryFailed extends ResourcesQueryEvent {
  final String error;

  ResourcesCreateQueryFailed({required this.error});
}
