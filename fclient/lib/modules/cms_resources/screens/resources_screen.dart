import 'package:fclient/core/widgets/app_bar.dart';
import 'package:fclient/modules/cms_resources/bloc/resources_query_bloc.dart';
import 'package:fclient/modules/cms_resources/models/cms_resource.dart';
import 'package:fclient/modules/cms_resources/repositories/cms_resource_repository.dart';
import 'package:fclient/modules/cms_resources/widgets/add_resource_popup.dart';
import 'package:fclient/modules/cms_resources/widgets/data_table/resources_data_table.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ResourcesScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return BlocProvider<ResourcesQueryBloc>(
        create: (_) =>
            ResourcesQueryBloc(cmsResourceRepository: CMSResourceRepository()),
        child: BlocBuilder<ResourcesQueryBloc, ResourcesQueryState>(
            builder: (context, state) {
          return _getResourcesScreenBody(context, state);
        }));
  }

  Widget _getResourcesScreenBody(
      BuildContext context, ResourcesQueryState state) {
    if (state is ResourcesQueryStateInitial) {
      BlocProvider.of<ResourcesQueryBloc>(context).add(ResourcesQueryStarted());
      return Scaffold(
          appBar: CustomAppBar(),
          body: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                CircularProgressIndicator(),
                Text(AppLocalizations.of(context)!.fetchingResources),
              ],
            ),
          ));
    } else if (state is ResourcesQueryStateInProgress) {
      return Scaffold(
          appBar: CustomAppBar(),
          body: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                CircularProgressIndicator(),
                Text(AppLocalizations.of(context)!.loadingResources),
              ],
            ),
          ));
    } else if (state is ResourcesQueryStateSuccess) {
      List<CMSResource> resources = state.resources;
      if (resources.isEmpty){
        return Scaffold(
          appBar: CustomAppBar(),
          body: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Text(AppLocalizations.of(context)!.noResourcesFoundWithYourAccount),
                Text(AppLocalizations.of(context)!.doYouWantToCreateAResource),
                AddResourcePopup(),
              ],
            ),
          ));
      }else{
        return Scaffold(
          appBar: CustomAppBar(),
          body: ResourcesDataTable(resources: state.resources),
        );
      }
    } else if (state is ResourcesQueryStateFailed) {
      return Scaffold(
          appBar: CustomAppBar(),
          body: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Text(AppLocalizations.of(context)!.resourceFetchingFailed),
              ],
            ),
          ));
    } else if (state is ResourcesDeleteQueryStateSuccess) {
      return Scaffold(
        appBar: CustomAppBar(),
        body: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                CircularProgressIndicator(),
                Text(AppLocalizations.of(context)!.resourceSuccessfullyDeleted),
              ],
            ),
          ),
      );
    } else if (state is ResourcesCreateQueryStateInProgress) {
      return Scaffold(
          appBar: CustomAppBar(),
          body: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                CircularProgressIndicator(),
                Text(AppLocalizations.of(context)!.creatingResource),
              ],
            ),
          ));
    } else if (state is ResourcesCreateQueryStateSuccess) {
      return Scaffold(
        appBar: CustomAppBar(),
        body:
            new Text(AppLocalizations.of(context)!.resourceSuccessfullyCreated),
      );
    } else if (state is ResourcesCreateQueryStateFailed) {
      return Scaffold(
        appBar: CustomAppBar(),
        body: SnackBar(
          content: Text(AppLocalizations.of(context)!.resourceCreationError),
        ),
      );
    } else {
      return Scaffold(
          appBar: CustomAppBar(),
          body: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Text(AppLocalizations.of(context)!.unexpectedErrorMessage),
              ],
            ),
          ));
    }
  }
}
