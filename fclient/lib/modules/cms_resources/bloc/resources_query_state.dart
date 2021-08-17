part of 'resources_query_bloc.dart';

abstract class ResourcesQueryState extends Equatable {
  const ResourcesQueryState();

  @override
  List<Object> get props => [];
}

class ResourcesQueryStateInitial extends ResourcesQueryState {}

class ResourcesQueryStateInProgress extends ResourcesQueryState {}

class ResourcesQueryStateSuccess extends ResourcesQueryState {
  final List<CMSResource> resources;

  ResourcesQueryStateSuccess({required this.resources});
}

class ResourcesQueryStateFailed extends ResourcesQueryState {
  final String error;

  ResourcesQueryStateFailed({required this.error});
}

class ResourcesDeleteQueryStateInProgress extends ResourcesQueryState {}

class ResourcesDeleteQueryStateSuccess extends ResourcesQueryState {
  final CMSResource resource;

  ResourcesDeleteQueryStateSuccess({required this.resource});
}

class ResourcesDeleteQueryStateFailed extends ResourcesQueryState {
  final String error;

  ResourcesDeleteQueryStateFailed({required this.error});
}

class ResourcesCreateQueryStateInProgress extends ResourcesQueryState {}

class ResourcesCreateQueryStateSuccess extends ResourcesQueryState {
  final CMSResource resource;

  ResourcesCreateQueryStateSuccess({required this.resource});
}

class ResourcesCreateQueryStateFailed extends ResourcesQueryState {
  final String error;

  ResourcesCreateQueryStateFailed({required this.error});
}
