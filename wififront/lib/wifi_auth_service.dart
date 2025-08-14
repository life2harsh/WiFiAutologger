import 'dart:convert';
import 'package:http/http.dart' as http;

class WiFiAuthService {
  static const String loginUrl = 'http://172.16.68.6:8090/login.xml';
  static const String logoutUrl = 'http://172.16.68.6:8090/logout.xml';
  static const String loginMode = '191';
  static const String logoutMode = '193';
  static const String productType = '0';

  static Map<String, String> get _headers => {
    'Content-Type': 'application/x-www-form-urlencoded',
    'User-Agent': 'WiFi-Authenticator/2.0',
    'Accept': 'text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5',
    'Accept-Language': 'en-US,en;q=0.5',
    'Accept-Encoding': 'gzip, deflate',
    'Connection': 'keep-alive',
  };

  static Future<Map<String, dynamic>> attemptLogin(String username, String password) async {
    try {
      final currentMillis = DateTime.now().millisecondsSinceEpoch;
      
      final encodedUsername = Uri.encodeComponent(username);
      final encodedPassword = Uri.encodeComponent(password);
      
      final urlParameters = 'mode=$loginMode&username=$encodedUsername&password=$encodedPassword&a=$currentMillis&producttype=$productType';

      print('Login URL: $loginUrl');
      print('Login Parameters: $urlParameters');

      final response = await http.post(
        Uri.parse(loginUrl),
        headers: _headers,
        body: urlParameters,
      ).timeout(const Duration(seconds: 15));

      print('Login Response Code: ${response.statusCode}');
      print('Login Response Body: ${response.body}');

      if (response.statusCode == 200) {
        final responseBody = response.body;
        
        if (responseBody.contains('<status><![CDATA[LIVE]]></status>')) {
          return {
            'success': true,
            'message': 'Login successful for $username',
            'username': username,
          };
        } else if (responseBody.contains('<status><![CDATA[LOGIN]]></status>')) {
          final messageMatch = RegExp(r'<message><!\[CDATA\[(.*?)\]\]></message>').firstMatch(responseBody);
          final errorMessage = messageMatch?.group(1) ?? 'Login failed';
          
          return {
            'success': false,
            'message': 'Login failed: $errorMessage',
          };
        } else {
          return {
            'success': false,
            'message': 'Login failed: Unexpected response format',
          };
        }
      } else {
        return {
          'success': false,
          'message': 'Login failed: HTTP ${response.statusCode}',
        };
      }
    } catch (e) {
      print('Login error: $e');
      
      if (e.toString().contains('Failed host lookup') || 
          e.toString().contains('No route to host') ||
          e.toString().contains('Network is unreachable')) {
        return {
          'success': false,
          'message': 'Network error: Are you connected to JIIT WiFi network?',
        };
      } else if (e.toString().contains('TimeoutException')) {
        return {
          'success': false,
          'message': 'Connection timeout: Check JIIT WiFi connection',
        };
      } else {
        return {
          'success': false,
          'message': 'Login error: $e',
        };
      }
    }
  }

  static Future<Map<String, dynamic>> attemptLogout([String? username]) async {
    try {
      final currentMillis = DateTime.now().millisecondsSinceEpoch;
      final logoutUsername = username ?? 'user';
      final encodedUsername = Uri.encodeComponent(logoutUsername);
      
      final urlParameters = 'mode=$logoutMode&username=$encodedUsername&a=$currentMillis&producttype=$productType';

      print('Logout URL: $logoutUrl');
      print('Logout Parameters: $urlParameters');

      final response = await http.post(
        Uri.parse(logoutUrl),
        headers: _headers,
        body: urlParameters,
      ).timeout(const Duration(seconds: 15));

      print('Logout Response Code: ${response.statusCode}');
      print('Logout Response Body: ${response.body}');

      if (response.statusCode == 200) {
        return {
          'success': true,
          'message': 'Logout successful',
        };
      } else {
        return {
          'success': false,
          'message': 'Logout failed: HTTP ${response.statusCode}',
        };
      }
    } on Exception catch (e) {
      if (e.toString().contains('TimeoutException')) {
        return {
          'success': true,
          'message': 'Logout completed (timeout - likely successful)',
        };
      } else if (e.toString().contains('Failed host lookup') || 
                 e.toString().contains('No route to host') ||
                 e.toString().contains('Network is unreachable')) {
        return {
          'success': false,
          'message': 'Network error: Are you connected to JIIT WiFi network?',
        };
      }
      
      print('Logout error: $e');
      return {
        'success': false,
        'message': 'Logout error: $e',
      };
    }
  }

  static Future<bool> _isWiFiPortalAvailable() async {
    try {
      final response = await http.head(
        Uri.parse('http://172.16.68.6:8090/')
      ).timeout(const Duration(seconds: 5));
      
      return response.statusCode > 0;
    } catch (e) {
      print('WiFi portal check failed: $e');
      return false;
    }
  }

  static Future<Map<String, dynamic>> getNetworkStatus() async {
    try {
      final portalAvailable = await _isWiFiPortalAvailable();
      
      if (portalAvailable) {
        return {
          'connected': true,
          'status': 'Connected to WiFi - Authentication Required',
          'portalAvailable': true,
        };
      } else {
        return {
          'connected': false,
          'status': 'No Network Connection or Portal Unavailable',
          'portalAvailable': false,
        };
      }
    } catch (e) {
      return {
        'connected': false,
        'status': 'Network check failed: $e',
        'portalAvailable': false,
      };
    }
  }

  static Future<Map<String, dynamic>> testConnection() async {
    try {
      final networkStatus = await getNetworkStatus();
      
      if (networkStatus['portalAvailable'] == true) {
        final response = await http.get(
          Uri.parse('http://172.16.68.6:8090/'),
          headers: {
            'User-Agent': 'WiFi-Authenticator/2.0',
          },
        ).timeout(const Duration(seconds: 10));
        
        if (response.statusCode == 200 && response.body.contains('JIIT')) {
          return {
            'success': true,
            'message': 'WiFi portal is accessible and working',
            'portalType': 'JIIT Campus WiFi',
          };
        } else {
          return {
            'success': false,
            'message': 'WiFi portal responded but content unexpected',
          };
        }
      } else {
        return {
          'success': false,
          'message': 'WiFi portal is not accessible',
        };
      }
    } catch (e) {
      return {
        'success': false,
        'message': 'Connection test failed: $e',
      };
    }
  }
}
