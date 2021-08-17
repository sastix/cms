import 'package:fclient/core/widgets/app_bar.dart';
import 'package:fclient/modules/users/services/web_authenticator_service.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class LogoutScreen extends StatelessWidget {
  final String _uri = dotenv.env['KEYCLOAK_URI'] as String;
  final String _logoutUrl = dotenv.env['LOGOUT_URL'] as String;

  @override
  Widget build(BuildContext context) {
    if (kIsWeb) {
      WebAuthenticatorService.logout(context, _uri, _logoutUrl);
    }
    return Scaffold(
      appBar: CustomAppBar(),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: const <Widget>[
            CircularProgressIndicator(),
            Text('Logging out...'),
          ],
        ),
      ),
    );
  }
}
