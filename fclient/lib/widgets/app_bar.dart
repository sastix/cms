import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class CustomAppBar extends StatefulWidget implements PreferredSizeWidget {
  final double height = 60;

  @override
  Size get preferredSize => Size.fromHeight(height);

  @override
  _CustomAppBarState createState() => _CustomAppBarState();
}

class _CustomAppBarState extends State<CustomAppBar> {
  @override
  Widget build(BuildContext context) {
    return AppBar(
      title: Text('Sastix-CMS'),
      actions: [
        PopupMenuButton<int>(
            onSelected: (item) => onSelected(context, item),
            itemBuilder: (context) => [
                  PopupMenuItem<int>(value: 0, child: Text('Login')),
                  PopupMenuItem<int>(value: 1, child: Text('Settings'))
                ])
      ],
    );
  }

  void onSelected(BuildContext context, int item) {
    switch (item) {
      case 0:
        Navigator.of(context).pushNamed('/authenticate/');
        break;
      case 1:
        Navigator.of(context).pushNamed('/settings/');
        break;
    }
  }
}
