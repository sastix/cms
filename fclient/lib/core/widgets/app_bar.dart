import 'package:fclient/config/routes/route_paths.dart';
import 'package:fclient/modules/users/bloc/authenticate_user_bloc.dart';
import 'package:fclient/modules/users/bloc/user_state.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class CustomAppBar extends StatefulWidget implements PreferredSizeWidget {
  final double height = 60;

  @override
  Size get preferredSize => Size.fromHeight(height);

  @override
  _CustomAppBarState createState() => _CustomAppBarState();
}

class _CustomAppBarState extends State<CustomAppBar> {
  @override
  Widget build(BuildContext context) {
    return BlocBuilder<AuthenticateUserBloc, UserState>(
      builder: (context, state) {
        return AppBar(
          title: Text(dotenv.env['SITE_NAME'] as String),
          actions: _getActionsList(state),
        );
      },
    );
  }

  List<Widget> _getActionsList(UserState state) {
    List<Widget> actionsList = [
      PopupMenuButton<int>(
          onSelected: (item) => onSelected(context, item),
          itemBuilder: (context) => [
                PopupMenuItem<int>(
                  value: 0,
                  child: Text(state is AuthenticateUserStateSuccess
                      ? state.username
                      : AppLocalizations.of(context)!.login),
                  enabled: state is! AuthenticateUserStateSuccess,
                ),
                PopupMenuItem<int>(
                    value: 1,
                    child: Text(AppLocalizations.of(context)!.settings)),
                PopupMenuItem<int>(
                  value: 2,
                  child: Text(AppLocalizations.of(context)!.logout),
                  enabled: state is AuthenticateUserStateSuccess,
                ),
              ])
    ];
    return actionsList;
  }

  void onSelected(BuildContext context, int item) {
    switch (item) {
      case 0:
        Navigator.of(context).pushNamed(AUTHENTICATION_PAGE_PATH);
        break;
      case 1:
        Navigator.of(context).pushNamed(SETTINGS_PAGE_PATH);
        break;
      case 2:
        Navigator.of(context).pushNamed(LOGOUT_PAGE_PATH);
        break;
      default:
        Navigator.of(context).pushNamed(HOMEPAGE_PATH);
        break;
    }
  }
}
