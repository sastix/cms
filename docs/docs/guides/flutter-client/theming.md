---
sidebar_position: 1
---

# Theming

The administrator of the application can use the theming hooks to
configure the look and feel of the application depending on the
branding needs. The user can make selections from the predefined
administrator themes on demand.

## Changing the predefined themes

The Sastix CMS comes with two predefined themes, light and dark. The
administrator can change those themes by modifying files in the
`lib/config/themes` folder. For example the light theme configuration
looks like this:

```
import 'package:flutter/material.dart';

var lightThemeData = new ThemeData(
    primaryColor: Colors.blue,
    textTheme: new TextTheme(button: TextStyle(color: Colors.white70)),
    brightness: Brightness.light,
    accentColor: Colors.blue);
```

The administrator could set any property on the ThemeData object as
described in Flutter documentation
([ThemeData](https://api.flutter.dev/flutter/material/ThemeData-class.html)).

## User preferences

The user can change between the predefined themes provided by the
administrator. To change between the two themes:

- Head to the settings page.Open the dropdown on the upper right
corner of the screen and select "Settings".
![User Dropdown](/img/user-dropdown.png)
- Toggle the button to change between light and dark theme.<br/>
Light theme:
![Light Theme Mode](/img/light-theme-toggle.png)
Dark theme:
![Dark Theme Mode](/img/dark-theme-toggle.png)