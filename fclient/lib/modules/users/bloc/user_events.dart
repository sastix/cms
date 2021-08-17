import 'package:equatable/equatable.dart';

abstract class UserEvent extends Equatable {
  const UserEvent();

  @override
  List<Object> get props => [];
}

class UserAuthenticationChecked extends UserEvent {}

class UserCouldNotAuthenticate extends UserEvent {
  final String error;

  UserCouldNotAuthenticate({required this.error});
}

class UserAuthenticated extends UserEvent {
  final String username;
  final String email;

  UserAuthenticated({required this.username, required this.email});
}

class UserLoggedOut extends UserEvent {}
