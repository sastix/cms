import 'package:equatable/equatable.dart';

abstract class UserState extends Equatable {
  @override
  List<Object> get props => [];
}

class UserStateEmpty extends UserState {
  @override
  String toString() => 'Authentication state is empty';
}

class AuthenticateUserStateInProgress extends UserState {
  @override
  String toString() => 'AuthenticateUserStateLoading';
}

class AuthenticateUserStateSuccess extends UserState {
  final String username;
  final String email;

  AuthenticateUserStateSuccess({
    required this.username,
    required this.email,
  });

  @override
  List<Object> get props => [username, email];

  @override
  String toString() =>
      "AuthenticateUserStateSuccess { username: ${this.username}, "
      "jwtToken: ${this.email}}";
}

class AuthenticateUserStateError extends UserState {
  final String error;

  AuthenticateUserStateError({
    required this.error,
  });

  @override
  List<Object> get props => [error];

  @override
  String toString() => 'AuthenticateUserStateError { error: ${this.error}}';
}

class UserLoggedOutInProgress extends UserState {}
