import 'package:flutter_dotenv/flutter_dotenv.dart';

final String apiBase = dotenv.env["CMS_SERVER_URI"] as String;
final String apiBasePath = dotenv.env["CMS_SERVER_BASEPATH"] as String;
final String apiBaseURL = apiBase + apiBasePath;
final String getResourcesEndpoint = apiBaseURL + "queryResourceByFields/";
final String createResourceEndpoint = apiBaseURL + "createResource/";
final String deleteResourceEndpoint = apiBaseURL + "deleteResourceNoLock/";
final String getRawResourceEndpoint = apiBaseURL + "getData/";
