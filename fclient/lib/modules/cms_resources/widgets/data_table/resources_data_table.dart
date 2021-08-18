import 'package:fclient/modules/cms_resources/models/cms_resource.dart';
import 'package:fclient/modules/cms_resources/widgets/add_resource_popup.dart';
import 'package:fclient/modules/cms_resources/widgets/data_table/resources_data_table_data_source.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class ResourcesDataTable extends StatelessWidget {
  final List<CMSResource> resources;

  ResourcesDataTable({required this.resources});

  @override
  Widget build(BuildContext context) {
    ResourcesDataTableDataSource source = ResourcesDataTableDataSource(context, resources);
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
                DataColumn(
                label: Text(AppLocalizations.of(context)!.name)),
            DataColumn(label: Text(AppLocalizations.of(context)!.actions))
          ],
          source: source,
        ),
      ],
    );
  }
}
