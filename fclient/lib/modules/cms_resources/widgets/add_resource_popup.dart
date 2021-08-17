import 'package:dio/dio.dart';
import 'package:fclient/modules/cms_resources/bloc/resources_query_bloc.dart';
import 'package:fclient/modules/cms_resources/dtos/create_cms_resource_request_dto.dart';
import 'package:fclient/modules/cms_resources/repositories/resources_api.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class AddResourcePopup extends StatefulWidget {
  @override
  AddResourcePopupState createState() => AddResourcePopupState();
}

class AddResourcePopupState extends State<AddResourcePopup> {
  ResourcesAPI resourcesService = ResourcesAPI();
  void sendResponse(BuildContext context,
      CreateCMSResourceRequestDTO createCMSResourceRequestDTO) {
    try {
      BlocProvider.of<ResourcesQueryBloc>(context).add(
          ResourcesCreateQueryStarted(
              createCMSResourceRequestDTO: createCMSResourceRequestDTO));
    } on DioError catch (e) {
      BlocProvider.of<ResourcesQueryBloc>(context)
          .add(ResourcesCreateQueryFailed(error: e.message));
    }
  }

  @override
  Widget build(BuildContext context) {
    return IconButton(
        icon: const Icon(Icons.add_circle_outline),
        onPressed: () {
          final _formKey = GlobalKey<FormState>();
          CreateCMSResourceRequestDTO createCMSResourceRequestDTO =
              CreateCMSResourceRequestDTO();
          showDialog<String>(
            context: context,
            builder: (_) => AlertDialog(
              title: Text(AppLocalizations.of(context)!.createResourcePrompt),
              content: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    TextFormField(
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return AppLocalizations.of(context)!.enterAuthorError;
                        }
                        return null;
                      },
                      decoration: InputDecoration(
                        border: OutlineInputBorder(),
                        labelText: AppLocalizations.of(context)!.resourceAuthor,
                      ),
                      onSaved: (value) {
                        createCMSResourceRequestDTO.resourceAuthor = value!;
                      },
                    ),
                    const SizedBox(height: 15),
                    TextFormField(
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return AppLocalizations.of(context)!
                              .emptyExternalURIError;
                        }
                        if (!Uri.parse(value).isAbsolute) {
                          return AppLocalizations.of(context)!
                              .invalidExternalURIError;
                        }
                        return null;
                      },
                      decoration: InputDecoration(
                        border: OutlineInputBorder(),
                        labelText:
                            AppLocalizations.of(context)!.externalURIPrompt,
                      ),
                      onSaved: (value) {
                        createCMSResourceRequestDTO.resourceExternalURI =
                            value!;
                      },
                    ),
                    const SizedBox(height: 15),
                    TextFormField(
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return AppLocalizations.of(context)!
                              .emptyMediaTypeError;
                        }
                        return null;
                      },
                      decoration: InputDecoration(
                        border: OutlineInputBorder(),
                        labelText:
                            AppLocalizations.of(context)!.mediaTypePrompt,
                      ),
                      onSaved: (value) {
                        createCMSResourceRequestDTO.resourceMediaType = value!;
                      },
                    ),
                    const SizedBox(height: 15),
                    TextFormField(
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return AppLocalizations.of(context)!
                              .emptyResourceNameError;
                        }
                        return null;
                      },
                      decoration: InputDecoration(
                        border: OutlineInputBorder(),
                        labelText:
                            AppLocalizations.of(context)!.resourceNamePrompt,
                      ),
                      onSaved: (value) {
                        createCMSResourceRequestDTO.resourceName = value!;
                      },
                    ),
                    SizedBox(height: 15),
                    TextFormField(
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return AppLocalizations.of(context)!
                              .emptyTenantIdError;
                        }
                        return null;
                      },
                      decoration: InputDecoration(
                        border: OutlineInputBorder(),
                        labelText: AppLocalizations.of(context)!.tenantIdPrompt,
                      ),
                      onSaved: (value) {
                        createCMSResourceRequestDTO.resourceTenantId = value!;
                      },
                    ),
                  ],
                ),
              ),
              actions: <Widget>[
                TextButton(
                  onPressed: () => {
                    Navigator.pop(context, 'Cancel'),
                  },
                  child: Text(AppLocalizations.of(context)!.cancelPrompt),
                ),
                TextButton(
                  onPressed: () => {
                    if (_formKey.currentState!.validate())
                      {
                        _formKey.currentState!.save(),
                        Navigator.pop(context, 'Create'),
                        sendResponse(context, createCMSResourceRequestDTO)
                      },
                  },
                  child: Text(AppLocalizations.of(context)!.createPrompt),
                ),
              ],
            ),
          );
        });
  }
}
