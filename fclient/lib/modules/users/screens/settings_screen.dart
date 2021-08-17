import 'package:fclient/core/widgets/app_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:easy_dynamic_theme/easy_dynamic_theme.dart';
import 'package:settings_ui/settings_ui.dart';

class SettingsScreen extends StatefulWidget {
  @override
  _State createState() => _State();
}

class _State extends State<SettingsScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(),
      body: buildSettingsList(),
    );
  }

  Widget buildSettingsList() {
    return SettingsList(
      sections: [
        SettingsSection(
          title: 'Theme',
          tiles: [
            SettingsTile.switchTile(
              title: 'Dark Mode',
              leading: Icon(Icons.wb_sunny),
              switchValue: Theme.of(context).brightness == Brightness.dark,
              onToggle: (bool value) {
                setState(() {
                  EasyDynamicTheme.of(context).changeTheme(dark: value);
                });
              },
            ),
          ],
        ),
      ],
    );
  }
}
