import 'package:fclient/core/constants/cms_server_api_endpoints.dart';
import 'package:fclient/core/widgets/app_bar.dart';
import 'package:fclient/modules/cms_resources/models/cms_resource.dart';
import 'package:fclient/modules/cms_resources/widgets/custom_video_player.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ResourceDetailsScreen extends StatelessWidget {
  final CMSResource resourceDto;

  ResourceDetailsScreen({required this.resourceDto});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: CustomAppBar(),
        body: ListView(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                Text(
                  AppLocalizations.of(context)!.resource +
                      ": " +
                      resourceDto.resourceUID,
                  style: TextStyle(fontSize: 20.0, height: 3),
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Text(
                  resourceDto.name,
                  style: TextStyle(fontSize: 15.0, height: 1),
                ),
              ],
            ),
            _getResourceView(resourceDto,
                AppLocalizations.of(context)!.unexpectedErrorMessage),
          ],
        ));
  }

  Widget _getResourceView(CMSResource resource, String errorMessage) {
    if (resourceDto.mediaType.contains("image")) {
      return Image.network(getRawResourceEndpoint + resourceDto.resourceURI);
    } else if (resourceDto.mediaType.contains("video")) {
      return CustomVideoPlayer(
          title: resource.name,
          videoURI: getRawResourceEndpoint + resourceDto.resourceURI);
    } else {
      return Text(errorMessage);
    }
  }
}
