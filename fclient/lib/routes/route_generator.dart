import 'package:fclient/screens/authentication_screen.dart';
import 'package:fclient/screens/error_screen.dart';
import 'package:fclient/screens/home_screen.dart';
import 'package:fclient/screens/resources_screen.dart';
import 'package:fclient/screens/settings_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class RouteGenerator {
  static Route<dynamic> generateRoute(RouteSettings settings) {
    switch (settings.name) {
      case '/':
        return MaterialPageRoute(
            builder: (_) => HomePageScreen(), settings: settings);
      case '/resources/':
        return MaterialPageRoute(
            builder: (_) => ResourcesScreen(), settings: settings);
      case '/settings/':
        return MaterialPageRoute(
            builder: (_) => SettingsScreen(), settings: settings);
      case '/authenticate/':
        return MaterialPageRoute(
            builder: (_) => AuthenticationScreen(
                  authenticationParameters: {},
                ),
            settings: settings);
      default:
        String? settingsName = settings.name;
        if (settingsName != null && settingsName.contains('access_token')) {
          Uri uri = Uri.parse('/authenticate/?' + settingsName);
          Map<String, String> authenticationParameters = uri.queryParameters;
          settings = new RouteSettings(
              name: '/authenticate/', arguments: settings.arguments);
          return MaterialPageRoute(
            builder: (_) => AuthenticationScreen(
                  authenticationParameters: authenticationParameters,
                ),
            settings: settings);
        }
        return MaterialPageRoute(
            builder: (_) => ErrorPageScreen(), settings: settings);
    }
  }
}
