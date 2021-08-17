import 'package:jwt_decoder/jwt_decoder.dart';

class User {
  late String username;
  late String? email;

  User({
    required this.username,
    required this.email,
  });

  User.fromJWTToken({required String jwtToken}) {
    Map<String, dynamic> decodedToken = JwtDecoder.decode(jwtToken);
    this.username = decodedToken['preferred_username'] as String;
    this.email = decodedToken['email'];
  }
}
