import 'package:fclient/core/constants/openid_client_constants.dart';
import 'package:fclient/modules/users/models/user.dart';
import 'package:shared_preferences/shared_preferences.dart';

class UserRepository {
  Future<String?> getOpenIDClientStorageString() async {
    SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
    return sharedPreferences.getString(OPENID_CLIENT_STORAGE_KEY);
  }

  Future<User?> getAuthenticatedUser() async {
    SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
    String? jwtToken =
        sharedPreferences.getString(OPENID_CLIENT_ACCESS_TOKEN_KEY);
    if (jwtToken != null) {
      return User.fromJWTToken(jwtToken: jwtToken);
    }
  }
}
