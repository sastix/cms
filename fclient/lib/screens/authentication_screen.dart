import 'dart:convert';
import 'dart:html';

import 'package:fclient/widgets/app_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:openid_client/openid_client_browser.dart';

class AuthenticationScreen extends StatefulWidget {
  final Map<String, String> authenticationParameters;

  const AuthenticationScreen({Key? key, required this.authenticationParameters})
      : super(key: key);
  @override
  _AuthenticationScreenState createState() => _AuthenticationScreenState();
}

class _AuthenticationScreenState extends State<AuthenticationScreen> {
  final String _clientId = dotenv.env['FLUTTER_CLIENT_ID'] as String;
  final String _uri = dotenv.env['KEYCLOAK_URI'] as String;
  final List<String> _scopes = <String>[
    'openid',
    'profile',
    'email',
    'offline_access'
  ];
  String logoutUrl = dotenv.env['LOGOUT_URL'] as String;

  @override
  Widget build(BuildContext context) {
    authenticate(
        Uri.parse(_uri), _clientId, _scopes, widget.authenticationParameters);
    return Scaffold(
      appBar: CustomAppBar(),
      body: Center(
        child: Text('Authenticating...'),
      ),
    );
  }

  authenticate(Uri uri, String clientId, List<String> scopes,
      Map<String, String> authenticationParameters) async {
    var issuer = await Issuer.discover(uri);
    var client = new Client(issuer, clientId);

    var authenticator = new Authenticator(client, scopes: scopes);

    if (authenticationParameters.isNotEmpty) {
      await saveCredential(authenticator, authenticationParameters);
    } else {
      var c = await authenticator.credential;

      if (c == null) {
        authenticator.authorize();
      } else {
        window.location.href = '/';
      }
    }
  }

  Future<Credential?> saveCredential(Authenticator authenticator,
      Map<String, String> authenticationParameters) async {
    if (authenticationParameters.containsKey('access_token') ||
        authenticationParameters.containsKey('code') ||
        authenticationParameters.containsKey('id_token')) {
      window.localStorage['openid_client:auth'] =
          json.encode(authenticationParameters);
      window.location.href = '/';
    }
  }
}
