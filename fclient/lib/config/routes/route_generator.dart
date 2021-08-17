import 'package:fclient/config/routes/route_paths.dart';
import 'package:fclient/core/screens/error_screen.dart';
import 'package:fclient/modules/dashboard/screens/home_screen.dart';
import 'package:fclient/modules/cms_resources/screens/resources_screen.dart';
import 'package:fclient/modules/users/screens/authentication_screen.dart';
import 'package:fclient/modules/users/screens/logout_screen.dart';
import 'package:fclient/modules/users/screens/settings_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class RouteGenerator {
  static Route<dynamic> generateRoute(RouteSettings settings) {
    switch (settings.name) {
      case HOMEPAGE_PATH:
        return MaterialPageRoute(
            builder: (_) => HomePageScreen(), settings: settings);
      case RESOURCES_PAGE_PATH:
        return MaterialPageRoute(
            builder: (_) => ResourcesScreen(), settings: settings);
      case SETTINGS_PAGE_PATH:
        return MaterialPageRoute(
            builder: (_) => SettingsScreen(), settings: settings);
      case AUTHENTICATION_PAGE_PATH:
        return MaterialPageRoute(
            builder: (_) => AuthenticationScreen(
                  authenticationParameters: {},
                ),
            settings: settings);
      case LOGOUT_PAGE_PATH:
        return MaterialPageRoute(
            builder: (_) => LogoutScreen(), settings: settings);
      default:
        if (settings.name != null && settings.name!.contains('access_token')) {
          return MaterialPageRoute(
              builder: (_) => AuthenticationScreen(
                    authenticationParameters: Uri.parse(
                            AUTHENTICATION_PAGE_PATH +
                                '?' +
                                settings.name.toString())
                        .queryParameters,
                  ),
              settings: new RouteSettings(
                  name: AUTHENTICATION_PAGE_PATH,
                  arguments: settings.arguments));
        }
        return MaterialPageRoute(
            builder: (_) => ErrorPageScreen(), settings: settings);
    }
  }
}
