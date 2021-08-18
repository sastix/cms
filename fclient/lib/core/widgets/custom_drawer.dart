import 'package:fclient/config/routes/route_paths.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class CustomDrawer extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Drawer(
        child: ListView(
      padding: EdgeInsets.zero,
      children: [
        DrawerHeader(
            decoration: BoxDecoration(color: Colors.blue[100]),
            child: ImageIcon(AssetImage("assets/images/sastix_logo.png"))),
        ListTile(
          title: Text(AppLocalizations.of(context)!.homepagePath),
          onTap: () {
            Navigator.of(context).pushNamed(HOMEPAGE_PATH);
          },
        ),
        ListTile(
          title: Text(AppLocalizations.of(context)!.resources),
          onTap: () {
            Navigator.of(context).pushNamed(RESOURCES_PAGE_PATH);
          },
        )
      ],
    ));
  }
}
