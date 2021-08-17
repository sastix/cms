import 'package:fclient/modules/users/bloc/user_events.dart';
import 'package:fclient/modules/users/bloc/user_state.dart';
import 'package:fclient/modules/users/repositories/user_repository.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class AuthenticateUserBloc extends Bloc<UserEvent, UserState> {
  final UserRepository userRepository;

  AuthenticateUserBloc({required this.userRepository})
      : super(UserStateEmpty());

  @override
  Stream<UserState> mapEventToState(UserEvent event) async* {
    if (event is UserAuthenticationChecked) {
      yield AuthenticateUserStateInProgress();
    } else if (event is UserAuthenticated) {
      yield AuthenticateUserStateSuccess(
          username: event.username, email: event.email);
    } else if (event is UserCouldNotAuthenticate) {
      yield AuthenticateUserStateError(error: event.error);
    } else if (event is UserLoggedOut) {
      yield UserLoggedOutInProgress();
    }
  }
}
