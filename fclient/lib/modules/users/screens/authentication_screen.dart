import 'package:fclient/core/widgets/app_bar.dart';
import 'package:fclient/modules/users/services/web_authenticator_service.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class AuthenticationScreen extends StatelessWidget {
  final Map<String, String> authenticationParameters;

  AuthenticationScreen({required this.authenticationParameters});

  @override
  Widget build(BuildContext context) {
    if (kIsWeb) {
      WebAuthenticatorService.authenticateWeb(
          context, this.authenticationParameters);
    }
    return Scaffold(
      appBar: CustomAppBar(),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: const <Widget>[
            CircularProgressIndicator(),
            Text('Authenticating...'),
          ],
        ),
      ),
    );
  }
}
