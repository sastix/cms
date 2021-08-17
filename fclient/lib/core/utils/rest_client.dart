import 'package:dio/dio.dart';
import 'package:fclient/core/constants/openid_client_constants.dart';
import 'package:fclient/modules/users/services/web_authenticator_service.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:shared_preferences/shared_preferences.dart';

class RestClient {
  Dio dio = new Dio();

  RestClient() {
    dio.options.headers["Content-Type"] = "application/json";
    if (dotenv.env["KEYCLOAK_ENABLED"] == "true") {
      _addAuthenticationInterceptor();
    }
  }

  void _addAuthorizationHeader(RequestOptions options) async {
    SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
    String? jwtToken =
        sharedPreferences.getString(OPENID_CLIENT_ACCESS_TOKEN_KEY);
    if (jwtToken == null || JwtDecoder.isExpired(jwtToken)) {
      WebAuthenticatorService.authenticateBaseWeb();
    } else {
      options.headers["Authorization"] = "Bearer $jwtToken";
    }
  }

  void _addAuthenticationInterceptor() {
    dio.interceptors.add(InterceptorsWrapper(onRequest:
        (RequestOptions options, RequestInterceptorHandler handler) async {
      _addAuthorizationHeader(options);
      return handler.next(options);
    }, onError: (DioError error, ErrorInterceptorHandler handler) {
      if (error.response!.statusCode == 401) {
        WebAuthenticatorService.authenticateBaseWeb();
      }
    }));
  }
}
