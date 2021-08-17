import 'package:fclient/core/widgets/app_bar.dart';
import 'package:fclient/core/widgets/drawer.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class HomePageScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(),
      body: Center(
        child: Text(AppLocalizations.of(context)!.homePagePrompt),
      ),
      drawer: CustomDrawer(),
    );
  }
}
