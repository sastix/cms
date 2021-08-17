import 'package:fclient/core/constants/cms_server_api_endpoints.dart';
import 'package:fclient/modules/cms_resources/bloc/resources_query_bloc.dart';
import 'package:fclient/modules/cms_resources/models/cms_resource.dart';
import 'package:fclient/modules/cms_resources/widgets/add_resource_popup.dart';
import 'package:fclient/modules/cms_resources/widgets/custom_video_player.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:url_launcher/url_launcher.dart';

class ResourcesDataTable extends StatelessWidget {
  final List<CMSResource> resources;

  ResourcesDataTable({required this.resources});

  @override
  Widget build(BuildContext context) {
    _DataSource source = _DataSource(context, resources);
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        PaginatedDataTable(
          header: Row(children: [
            Text(AppLocalizations.of(context)!.resources),
            AddResourcePopup(),
          ]),
          showCheckboxColumn: false,
          rowsPerPage: source.rowCount > 10 ? 10 : source.rowCount,
          columns: [
            DataColumn(label: Text(AppLocalizations.of(context)!.resourceUID)),
            DataColumn(label: Text(AppLocalizations.of(context)!.resourceURI)),
            DataColumn(
                label: Text(AppLocalizations.of(context)!.resourceAuthor)),
            DataColumn(
                label: Text(AppLocalizations.of(context)!.mediaTypePrompt)),
            DataColumn(label: Text(AppLocalizations.of(context)!.actions))
          ],
          source: source,
        ),
      ],
    );
  }
}

class _Row {
  _Row(this.resourceUID, this.resourceURI, this.author, this.mediaType);

  final String resourceUID;
  final String resourceURI;
  final String author;
  final String mediaType;

  bool selected = false;
}

class _DataSource extends DataTableSource {
  _DataSource(this.context, List<CMSResource>? resources) {
    for (var res in resources!) {
      _rows.add(
          _Row(res.resourceUID, res.resourceURI, res.author, res.mediaType));
    }
  }

  final BuildContext context;
  List<_Row> _rows = [];

  int _selectedCount = 0;

  @override
  DataRow? getRow(int index) {
    assert(index >= 0);
    if (index >= _rows.length) return null;
    final row = _rows[index];
    return DataRow.byIndex(
      index: index,
      cells: [
        DataCell(Text(row.resourceUID)),
        DataCell(Text(row.resourceURI),
            onTap: () => launch(getRawResourceEndpoint + row.resourceURI)),
        DataCell(Text(row.author)),
        DataCell(Text(row.mediaType)),
        DataCell(Row(
          children: _getActionButtons(context, row),
        )),
      ],
    );
  }

  List<Widget> _getActionButtons(BuildContext context, _Row row) {
    List<Widget> actionButtons = [];
    if (row.mediaType == "video/mp4") {
      actionButtons.add(TextButton(
        style: TextButton.styleFrom(
          backgroundColor: Colors.blue,
          primary: Colors.white,
        ),
        onPressed: () {
          _playVideo(context, row);
        },
        child: Text(AppLocalizations.of(context)!.watchVideoPrompt),
      ));
    }
    actionButtons.add(TextButton(
      style: TextButton.styleFrom(
        backgroundColor: Colors.red,
        primary: Colors.white,
      ),
      onPressed: () {
        BlocProvider.of<ResourcesQueryBloc>(context)
            .add(ResourcesDeleteQueryStarted(resourceUID: row.resourceUID));
      },
      child: Text(AppLocalizations.of(context)!.deletePrompt),
    ));
    return actionButtons;
  }

  void _playVideo(BuildContext context, _Row row) {
    showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: new Text(row.resourceUID),
            content: CustomVideoPlayer(
                title: row.resourceUID,
                videoURI: getRawResourceEndpoint + row.resourceURI),
          );
        });
  }

  @override
  int get rowCount => _rows.length;

  @override
  bool get isRowCountApproximate => false;

  @override
  int get selectedRowCount => _selectedCount;
}
