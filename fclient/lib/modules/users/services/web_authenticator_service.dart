import 'dart:convert';
import 'dart:html';

import 'package:flutter/widgets.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:openid_client/openid_client_browser.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:fclient/config/routes/route_paths.dart';
import 'package:fclient/core/constants/openid_client_constants.dart';
import 'package:fclient/modules/users/bloc/authenticate_user_bloc.dart';
import 'package:fclient/modules/users/bloc/user_events.dart';
import 'package:fclient/modules/users/models/user.dart';

class WebAuthenticatorService {
  static final String _clientId = dotenv.env['FLUTTER_CLIENT_ID'] as String;
  static final String _uri = dotenv.env['KEYCLOAK_URI'] as String;
  static final List<String> _scopes = <String>[
    'openid',
    'profile',
    'email',
    'offline_access'
  ];
  static final String logoutUrl = dotenv.env['LOGOUT_URL'] as String;

  static authenticateBaseWeb() async {
    var issuer = await Issuer.discover(Uri.parse(_uri));
    var client = new Client(issuer, _clientId);

    var authenticator = new Authenticator(client, scopes: _scopes);

    authenticator.authorize();
  }

  static authenticateWeb(BuildContext context,
      Map<String, String> authenticationParameters) async {
    var issuer = await Issuer.discover(Uri.parse(_uri));
    var client = new Client(issuer, _clientId);

    var authenticator = new Authenticator(client, scopes: _scopes);

    if (authenticationParameters.isNotEmpty) {
      String? jwtToken = await saveCredentialWeb(authenticationParameters);
      User user = User.fromJWTToken(jwtToken: jwtToken.toString());
      BlocProvider.of<AuthenticateUserBloc>(context).add(UserAuthenticated(
          username: user.username, email: user.email.toString()));
      Navigator.of(context).pushReplacementNamed(HOMEPAGE_PATH);
    } else {
      authenticator.authorize();
    }
  }

  static Future<String?> saveCredentialWeb(
      Map<String, String> authenticationParameters) async {
    if (authenticationParameters.containsKey(OPENID_CLIENT_ACCESS_TOKEN_KEY) ||
        authenticationParameters.containsKey(OPENID_CLIENT_CODE_KEY) ||
        authenticationParameters.containsKey(OPENID_CLIENT_ID_TOKEN_KEY)) {
      SharedPreferences sharedPreferences =
          await SharedPreferences.getInstance();
      sharedPreferences.setString(
          OPENID_CLIENT_STORAGE_KEY, json.encode(authenticationParameters));
      sharedPreferences.setString(OPENID_CLIENT_ACCESS_TOKEN_KEY,
          authenticationParameters[OPENID_CLIENT_ACCESS_TOKEN_KEY] as String);
      return authenticationParameters[OPENID_CLIENT_ACCESS_TOKEN_KEY] as String;
    }
  }

  static logout(
      BuildContext context, String keycloakURL, String logoutURI) async {
    SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
    sharedPreferences.remove(OPENID_CLIENT_STORAGE_KEY);
    sharedPreferences.remove(OPENID_CLIENT_STATE_KEY);
    sharedPreferences.remove(OPENID_CLIENT_ACCESS_TOKEN_KEY);
    window.location.href = keycloakURL +
        logoutURI +
        "?redirect_uri=" +
        Uri.parse(window.location.href).origin.toString();
  }
}
