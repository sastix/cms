import 'package:fclient/config/routes/route_paths.dart';
import 'package:fclient/core/constants/cms_server_api_endpoints.dart';
import 'package:fclient/modules/cms_resources/bloc/resources_query_bloc.dart';
import 'package:fclient/modules/cms_resources/dtos/delete_cms_resource_request_dto.dart';
import 'package:fclient/modules/cms_resources/models/cms_resource.dart';
import 'package:fclient/modules/cms_resources/services/web_file_downloader.dart';
import 'package:fclient/modules/cms_resources/widgets/data_table/resources_data_table_row.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ResourcesDataTableDataSource extends DataTableSource {
  final BuildContext context;
  List<ResourcesDataTableRow> _rows = [];
  int _selectedCount = 0;

  ResourcesDataTableDataSource(this.context, List<CMSResource>? resources) {
    for (var res in resources!) {
      _rows.add(ResourcesDataTableRow(res.resourceUID, res.resourceURI,
          res.author, res.mediaType, res.name));
    }
  }

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
        DataCell(Text(row.name)),
        DataCell(Row(
          children: _getActionButtons(context, row),
        )),
      ],
    );
  }

  List<Widget> _getActionButtons(
      BuildContext context, ResourcesDataTableRow row) {
    List<Widget> actionButtons = [];
    if (row.mediaType.contains("image") || row.mediaType.contains("video")) {
      actionButtons.add(Container(
        margin: EdgeInsets.all(5),
        child: TextButton(
          style: TextButton.styleFrom(
            backgroundColor: Colors.green,
            primary: Colors.white,
            padding: EdgeInsets.all(0),
          ),
          onPressed: () {
            Navigator.of(context).pushNamed(RESOURCES_PAGE_PATH,
                arguments: CMSResource(
                    resourceUID: row.resourceUID,
                    author: row.author,
                    resourceURI: row.resourceURI,
                    mediaType: row.mediaType,
                    name: row.name));
          },
          child: Text(AppLocalizations.of(context)!.watch),
        ),
      ));
    }
    actionButtons.add(Container(
        margin: EdgeInsets.all(5),
        child: TextButton(
          style: TextButton.styleFrom(
            backgroundColor: Colors.blue,
            primary: Colors.white,
            padding: EdgeInsets.all(0),
          ),
          onPressed: () {
            WebFileDownloader.downloadFile(
                getRawResourceEndpoint + row.resourceURI, row.name);
          },
          child: Text(AppLocalizations.of(context)!.download),
        )));
    actionButtons.add(Container(
      margin: EdgeInsets.all(5),
      child: TextButton(
        style: TextButton.styleFrom(
          backgroundColor: Colors.red,
          primary: Colors.white,
          padding: EdgeInsets.all(0),
        ),
        onPressed: () {
          BlocProvider.of<ResourcesQueryBloc>(context).add(
              ResourcesDeleteQueryStarted(
                  deleteCMSResourceRequestDTO: DeleteCMSResourceRequestDTO(
                      author: row.author,
                      resourceURI: row.resourceURI,
                      mediaType: row.mediaType,
                      name: row.name,
                      resourceUID: row.resourceUID)));
        },
        child: Text(AppLocalizations.of(context)!.deletePrompt),
      ),
    ));
    return actionButtons;
  }

  @override
  int get rowCount => _rows.length;

  @override
  bool get isRowCountApproximate => false;

  @override
  int get selectedRowCount => _selectedCount;
}
