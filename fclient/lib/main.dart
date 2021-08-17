import 'package:easy_dynamic_theme/easy_dynamic_theme.dart';
import 'package:fclient/config/routes/route_generator.dart';
import 'package:fclient/config/routes/route_paths.dart';
import 'package:fclient/config/themes/dark_theme_data.dart';
import 'package:fclient/config/themes/light_theme_data.dart';
import 'package:fclient/modules/users/bloc/authenticate_user_bloc.dart';
import 'package:fclient/modules/users/repositories/user_repository.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

Future main() async {
  await dotenv.load(fileName: 'assets/config/.env');
  runApp(EasyDynamicThemeWidget(child: SastixCMS()));
}

class SastixCMS extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return BlocProvider<AuthenticateUserBloc>(
      create: (_) => AuthenticateUserBloc(userRepository: UserRepository()),
      child: MaterialApp(
        localizationsDelegates: [
          GlobalMaterialLocalizations.delegate,
          GlobalWidgetsLocalizations.delegate,
          GlobalCupertinoLocalizations.delegate,
          AppLocalizations.delegate,
        ],
        title: dotenv.env['SITE_NAME'] as String,
        debugShowCheckedModeBanner: false,
        theme: lightThemeData,
        darkTheme: darkThemeData,
        themeMode: EasyDynamicTheme.of(context).themeMode,
        initialRoute: HOMEPAGE_PATH,
        onGenerateRoute: RouteGenerator.generateRoute,
      ),
    );
  }
}
