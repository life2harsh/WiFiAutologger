import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  // Use your computer's IP address for physical devices
  // Use 10.0.2.2 for Android emulator
  static const String baseUrl = 'http://172.16.98.139:8081/api';
  
  static const Map<String, String> headers = {
    'Content-Type': 'application/json',
  };

  // Credential endpoints
  static Future<List<dynamic>> getCredentials() async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/credentials'),
        headers: headers,
      );
      
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception('Failed to load credentials');
      }
    } catch (e) {
      throw Exception('Error connecting to server: $e');
    }
  }

  static Future<Map<String, dynamic>> addCredential(String username, String password) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/credentials'),
        headers: headers,
        body: json.encode({
          'username': username,
          'password': password,
        }),
      );
      
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception('Failed to add credential');
      }
    } catch (e) {
      throw Exception('Error connecting to server: $e');
    }
  }

  static Future<void> deleteCredential(int id) async {
    try {
      final response = await http.delete(
        Uri.parse('$baseUrl/credentials/$id'),
        headers: headers,
      );
      
      if (response.statusCode != 200) {
        throw Exception('Failed to delete credential');
      }
    } catch (e) {
      throw Exception('Error connecting to server: $e');
    }
  }

  static Future<Map<String, dynamic>> login({String? username, String? password}) async {
    try {
      Map<String, dynamic> body = {};
      if (username != null && password != null) {
        body = {'username': username, 'password': password};
      }
      
      final response = await http.post(
        Uri.parse('$baseUrl/credentials/login'),
        headers: headers,
        body: json.encode(body),
      );
      
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception('Failed to login');
      }
    } catch (e) {
      throw Exception('Error connecting to server: $e');
    }
  }

  static Future<Map<String, dynamic>> logout() async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/credentials/logout'),
        headers: headers,
      );
      
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception('Failed to logout');
      }
    } catch (e) {
      throw Exception('Error connecting to server: $e');
    }
  }

  // Scheduler endpoints
  static Future<Map<String, dynamic>> startScheduler({
    required String type,
    int interval = 30,
    String timeUnit = 'minutes',
    String specificTime = '09:00',
    bool autoLogin = true,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/scheduler/start'),
        headers: headers,
        body: json.encode({
          'type': type,
          'interval': interval,
          'timeUnit': timeUnit,
          'specificTime': specificTime,
          'autoLogin': autoLogin,
        }),
      );
      
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception('Failed to start scheduler');
      }
    } catch (e) {
      throw Exception('Error connecting to server: $e');
    }
  }

  static Future<Map<String, dynamic>> stopScheduler() async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/scheduler/stop'),
        headers: headers,
      );
      
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception('Failed to stop scheduler');
      }
    } catch (e) {
      throw Exception('Error connecting to server: $e');
    }
  }

  static Future<Map<String, dynamic>> getSchedulerStatus() async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/scheduler/status'),
        headers: headers,
      );
      
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception('Failed to get scheduler status');
      }
    } catch (e) {
      throw Exception('Error connecting to server: $e');
    }
  }

  // Activity log endpoints
  static Future<List<String>> getActivityLogs() async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/activity-log'),
        headers: headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return List<String>.from(data['logs']);
      } else {
        throw Exception('Failed to load activity logs');
      }
    } catch (e) {
      throw Exception('Error connecting to server: $e');
    }
  }

  static Future<Map<String, dynamic>> clearActivityLogs() async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/activity-log/clear'),
        headers: headers,
      );
      
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception('Failed to clear activity logs');
      }
    } catch (e) {
      throw Exception('Error connecting to server: $e');
    }
  }
}
