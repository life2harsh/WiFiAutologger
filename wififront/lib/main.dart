import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import 'wifi_auth_service.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'WifiAL',
      theme: ThemeData.dark(),
      debugShowCheckedModeBanner: false,
      home: HomePage(),
    );
  }
}

DateTime now = DateTime.now();

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  _HomePage createState() => _HomePage();
}

class _HomePage extends State<HomePage> {
  List<TextEditingController> user = [];
  List<TextEditingController> password = [];
  List<String> activityLog = [];
  List<Map<String, dynamic>> credentials = [];
  bool isChecked1 = false;
  bool isChecked2 = false;
  bool isChecked3 = false;
  bool isSchedulerRunning = false;
  String schedulerStatus = 'Stopped';
  String lastUsedUsername = ''; // Track last used username for logout
  TextEditingController Interval = TextEditingController();
  TextEditingController SpecTime = TextEditingController();
  
  @override
  void initState() {
    super.initState();
    loadData();
  }

  void loadData() async {
    await loadCredentialsFromLocal();
    await loadActivityLogs();
    await loadSchedulerStatus();
  }

  Future<void> loadCredentialsFromLocal() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final savedCredentials = prefs.getStringList('saved_credentials') ?? [];
      
      setState(() {
        credentials.clear();
        user.clear();
        password.clear();
        
        // Load saved credentials
        for (int i = 0; i < savedCredentials.length; i++) {
          final credData = jsonDecode(savedCredentials[i]);
          credentials.add({
            'id': i + 1,
            'username': credData['username'],
            'encryptedPassword': credData['password'],
          });
          
          // Add to UI controllers
          user.add(TextEditingController(text: credData['username']));
          password.add(TextEditingController(text: '••••••••'));
        }
        
        // Always add one empty row for new input
        user.add(TextEditingController());
        password.add(TextEditingController());
      });
      
      print('Loaded ${savedCredentials.length} saved credentials');
    } catch (e) {
      print('Error loading credentials: $e');
      // Fallback - just add empty row
      setState(() {
        credentials = [];
        user.clear();
        password.clear();
        user.add(TextEditingController());
        password.add(TextEditingController());
      });
    }
  }

  Future<void> saveCredentialsToLocal() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      List<String> credentialsToSave = [];
      
      for (var cred in credentials) {
        credentialsToSave.add(jsonEncode({
          'username': cred['username'],
          'password': cred['encryptedPassword'],
        }));
      }
      
      await prefs.setStringList('saved_credentials', credentialsToSave);
      print('Saved ${credentialsToSave.length} credentials to storage');
    } catch (e) {
      print('Error saving credentials: $e');
    }
  }

  Future<void> loadActivityLogs() async {
    // For standalone app, maintain logs locally
    // You could save these to shared_preferences
    // For now, just keep them in memory
  }

  Future<void> loadSchedulerStatus() async {
    // For standalone app, maintain scheduler status locally
    setState(() {
      isSchedulerRunning = false;
      schedulerStatus = 'Stopped';
    });
  }

  DateTime now = DateTime.now();

  Future<void> StartSched() async {
    // For standalone app, implement local scheduler
    // This would use Timer.periodic() instead of backend API
    print('Starting local scheduler...');
    setState(() {
      isSchedulerRunning = true;
      schedulerStatus = 'Running locally';
    });
    await loadActivityLogs();
  }

  void addRow() async {
    if (user.isNotEmpty && password.isNotEmpty) {
      String username = user.last.text;
      String pass = password.last.text;
      
      if (username.isNotEmpty && pass.isNotEmpty && pass != '••••••••') {
        // Check if credential already exists
        bool alreadyExists = credentials.any((cred) => cred['username'] == username);
        
        if (!alreadyExists) {
          // Add to local credentials list
          setState(() {
            credentials.add({
              'id': credentials.length + 1,
              'username': username,
              'encryptedPassword': pass,
            });
          });
          
          // Save to persistent storage
          await saveCredentialsToLocal();
          
          print('Credential saved: $username');
          
          // Update the text field to show saved indicator
          password.last.text = '••••••••';
        } else {
          print('Credential already exists for: $username');
        }
      }
    }
    
    // Add empty row for new input
    setState(() {
      user.add(TextEditingController());
      password.add(TextEditingController());
    });
  }

  void clearLog() async {
    // Clear local activity log
    setState(() {
      activityLog.clear();
    });
  }

  void logIN() async {
    try {
      bool loginSuccessful = false;
      
      // First try with any fresh credentials in input fields
      for (int i = 0; i < user.length; i++) {
        if (user[i].text.isNotEmpty && 
            i < password.length && 
            password[i].text.isNotEmpty && 
            password[i].text != '••••••••') {
          
          lastUsedUsername = user[i].text; // Track for logout
          print('Attempting login with fresh credentials: ${user[i].text}');
          
          setState(() {
            activityLog.add('${DateTime.now().toString().substring(11, 19)}: Attempting login for ${user[i].text}');
          });
          
          final result = await WiFiAuthService.attemptLogin(
            user[i].text, 
            password[i].text
          );
          
          if (result['success'] == true) {
            print('Login successful: ${result['message']}');
            setState(() {
              activityLog.add('${DateTime.now().toString().substring(11, 19)}: ${result['message']}');
            });
            loginSuccessful = true;
            break;
          } else {
            print('Login failed: ${result['message']}');
            setState(() {
              activityLog.add('${DateTime.now().toString().substring(11, 19)}: ${result['message']}');
            });
          }
        }
      }
      
      // If no fresh credentials worked, try saved credentials
      if (!loginSuccessful && credentials.isNotEmpty) {
        for (var cred in credentials) {
          lastUsedUsername = cred['username']; // Track for logout
          print('Trying saved credential: ${cred['username']}');
          
          setState(() {
            activityLog.add('${DateTime.now().toString().substring(11, 19)}: Trying saved credential ${cred['username']}');
          });
          
          final result = await WiFiAuthService.attemptLogin(
            cred['username'], 
            cred['encryptedPassword']
          );
          
          if (result['success'] == true) {
            print('Login successful with saved credential: ${result['message']}');
            setState(() {
              activityLog.add('${DateTime.now().toString().substring(11, 19)}: ${result['message']}');
            });
            loginSuccessful = true;
            break;
          } else {
            print('Login failed with saved credential: ${result['message']}');
            setState(() {
              activityLog.add('${DateTime.now().toString().substring(11, 19)}: ${result['message']}');
            });
          }
        }
      }
      
      if (!loginSuccessful) {
        setState(() {
          activityLog.add('${DateTime.now().toString().substring(11, 19)}: All login attempts failed. Please check credentials and network.');
        });
      }
      
    } catch (e) {
      print('Error during login: $e');
      setState(() {
        activityLog.add('${DateTime.now().toString().substring(11, 19)}: Login error - $e');
      });
    }
  }

  void logOUT() async {
    try {
      setState(() {
        activityLog.add('${DateTime.now().toString().substring(11, 19)}: Attempting logout...');
      });
      
      // Use the last successfully logged in username for logout
      final result = await WiFiAuthService.attemptLogout(lastUsedUsername.isNotEmpty ? lastUsedUsername : null);
      
      if (result['success'] == true) {
        print('Logout successful: ${result['message']}');
        setState(() {
          activityLog.add('${DateTime.now().toString().substring(11, 19)}: ${result['message']}');
          lastUsedUsername = ''; // Clear after successful logout
        });
      } else {
        print('Logout failed: ${result['message']}');
        setState(() {
          activityLog.add('${DateTime.now().toString().substring(11, 19)}: ${result['message']}');
        });
      }
      
    } catch (e) {
      print('Error during logout: $e');
      setState(() {
        activityLog.add('${DateTime.now().toString().substring(11, 19)}: Logout error - $e');
      });
    }
  }

  void removeRow(int index) async {
    setState(() {
      // Remove from input lists
      if (index < user.length) user.removeAt(index);
      if (index < password.length) password.removeAt(index);
      
      // Remove from saved credentials if it's a saved credential
      if (index < credentials.length) {
        credentials.removeAt(index);
        print('Credential removed from storage');
      }
    });
    
    // Update saved credentials in storage
    await saveCredentialsToLocal();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color.fromARGB(255, 0, 0, 0),
        title: Text('WifiAL', style: TextStyle(fontSize: 25)),
        centerTitle: true,
      ),

      body: Container(
        decoration: BoxDecoration(color: Colors.black),
        child: Stack(
          children: [
            Column(
              children: [
                Container(
                  padding: EdgeInsets.all(20),

                  decoration: BoxDecoration(
                    color: Colors.black,
                    border: Border.all(
                      color: Color.fromARGB(255, 77, 82, 109),
                      width: 2,
                    ),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Row(
                        children: [
                          Expanded(
                            child: Text(
                              'User',
                              style: TextStyle(fontWeight: FontWeight.bold),
                            ),
                          ),
                          Expanded(
                            child: Text(
                              'Password',
                              style: TextStyle(fontWeight: FontWeight.bold),
                            ),
                          ),
                          SizedBox(width: 40),
                        ],
                      ),
                      Divider(),
                      ConstrainedBox(
                        constraints: BoxConstraints(maxHeight: 200),
                        child: SingleChildScrollView(
                          child: Column(
                            children: List.generate(user.length, (index) {
                              return Row(
                                children: [
                                  Expanded(
                                    child: TextField(
                                      controller: user[index],
                                      decoration: InputDecoration(
                                        hintText: 'User',
                                      ),
                                    ),
                                  ),
                                  Expanded(
                                    child: TextField(
                                      controller: password[index],
                                      decoration: InputDecoration(
                                        hintText: 'Password',
                                      ),
                                      obscureText: true,
                                    ),
                                  ),
                                  IconButton(
                                    onPressed: () => removeRow(index),
                                    icon: Icon(Icons.delete),
                                  ),
                                ],
                              );
                            }),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                SizedBox(height: 10),
                Align(
                  alignment: Alignment.centerRight,
                  child: Container(
                    decoration: BoxDecoration(
                      border: Border.all(
                        color: Color.fromARGB(255, 77, 82, 109),
                      ),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: FloatingActionButton(
                      backgroundColor: Colors.black,

                      onPressed: addRow,
                      child: Icon(Icons.add),
                    ),
                  ),
                ),
              ],
            ),
            Positioned(
              top: 400,
              left: (MediaQuery.of(context).size.width - (120 * 2 + 80)) / 2,
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  OutlinedButton(
                    onPressed: logIN,
                    style: OutlinedButton.styleFrom(minimumSize: Size(120, 70)),
                    child: Text('Login', style: TextStyle(color: Colors.green)),
                  ),
                  SizedBox(width: 80),
                  OutlinedButton(
                    onPressed: logOUT,
                    style: OutlinedButton.styleFrom(minimumSize: Size(120, 70)),
                    child: Text('Logout', style: TextStyle(color: Colors.red)),
                  ),
                ],
              ),
            ),

            Positioned(
              top: 550,
              left: (MediaQuery.of(context).size.width - (120 * 2 + 80)) / 2,
              child: OutlinedButton(
                onPressed: () {
                  showModalBottomSheet(
                    context: context,
                    builder: (_) => SchedulerSettingsSheet(
                      isChecked1: isChecked1,
                      isChecked2: isChecked2,
                      isChecked3: isChecked3,
                      intervalController: Interval,
                      onChangedInt: (val) => setState(() => isChecked1 = val),
                      onChangedST: (val) => setState(() => isChecked2 = val),
                      onChangedAL: (val) => setState(() => isChecked3 = val),
                      SpecTimeController: SpecTime,
                      Start: () async => await StartSched(),
                    ),
                  );
                },
                style: OutlinedButton.styleFrom(minimumSize: Size(325, 50)),
                child: Text('Schedular Settings'),
              ),
            ),
          ],
        ),
      ),
      drawer: Drawer(
        child: Container(
          decoration: BoxDecoration(color: Colors.black),
          child: SingleChildScrollView(
            child: Column(
              children: [
                SizedBox(
                  height: 150,
                  child: DrawerHeader(
                    child: Stack(
                      children: [
                        Positioned(
                          left: 75,
                          top: 20,
                          child: Text(
                            'Activity Log',
                            style: TextStyle(fontSize: 20),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                if (activityLog.isNotEmpty)
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: Column(
                      children: [
                        Container(
                          padding: EdgeInsets.all(20),
                          margin: EdgeInsets.all(20),
                          decoration: BoxDecoration(
                            color: const Color.fromARGB(93, 158, 158, 158),
                            borderRadius: BorderRadius.circular(16),
                          ),
                          child: Align(
                            alignment: Alignment.centerLeft,
                            child: Text(
                              activityLog.join('\n'),
                              style: TextStyle(
                                fontFamily: 'courier',
                                color: const Color.fromARGB(255, 255, 255, 255),
                              ),
                            ),
                          ),
                        ),
                        Align(
                          alignment: Alignment.centerLeft,
                          child: TextButton.icon(
                            onPressed: clearLog,
                            icon: Icon(Icons.delete_forever),
                            label: Text(
                              'Clear',
                              style: TextStyle(fontFamily: 'courier'),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),

                if (activityLog.isEmpty)
                  Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Text(
                      'No activity yet',
                      style: TextStyle(
                        color: Colors.grey,
                        fontStyle: FontStyle.italic,
                      ),
                    ),
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class SchedulerSettingsSheet extends StatefulWidget {
  final bool isChecked1;
  final bool isChecked2;
  final bool isChecked3;
  final TextEditingController intervalController;
  final TextEditingController SpecTimeController;
  final ValueChanged<bool> onChangedInt;
  final ValueChanged<bool> onChangedST;
  final ValueChanged<bool> onChangedAL;
  final Future<void> Function() Start;

  const SchedulerSettingsSheet({super.key, 
    required this.isChecked1,
    required this.isChecked2,
    required this.isChecked3,
    required this.intervalController,
    required this.onChangedInt,
    required this.onChangedST,
    required this.onChangedAL,
    required this.SpecTimeController,
    required this.Start,
  });

  @override
  _SchedulerSettingsSheetState createState() => _SchedulerSettingsSheetState();
}

class _SchedulerSettingsSheetState extends State<SchedulerSettingsSheet> {
  late bool localChecked1;
  late bool localChecked2;
  late bool localChecked3;
  late bool onPressedButt;
  String selectedOption = 'minutes';
  @override
  void initState() {
    super.initState();
    localChecked1 = widget.isChecked1;
    localChecked2 = widget.isChecked2;
    localChecked3 = widget.isChecked3;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(color: Colors.black),
      child: SizedBox(
        height: 400,
        child: Column(
          children: [
            SizedBox(height: 5),
            SizedBox(
              child: Center(
                child: Text(
                  'Schedular Settings',
                  style: TextStyle(fontSize: 20),
                ),
              ),
            ),
            Divider(),
            Column(
              children: [
                Container(
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Row(
                      children: [
                        Checkbox(
                          value: localChecked1,
                          onChanged: (bool? value) {
                            setState(() => localChecked1 = value ?? false);
                            widget.onChangedInt(localChecked1); // update parent
                          },
                        ),
                        Text('Interval:', style: TextStyle(fontSize: 15)),
                        SizedBox(width: 20),
                        SizedBox(
                          width: 100,
                          child: TextField(
                            controller: widget.intervalController,
                            enabled: localChecked1,
                            decoration: InputDecoration(
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(10),
                              ),
                            ),
                          ),
                        ),
                        SizedBox(width: 10),
                        Expanded(
                          child: DropdownButtonFormField<String>(
                            decoration: InputDecoration(
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(5),
                              ),
                            ),
                            value: selectedOption,
                            isExpanded: true,
                            items: ['hours', 'minutes', 'seconds'].map((
                              String value,
                            ) {
                              return DropdownMenuItem<String>(
                                value: value,
                                child: Center(child: Text(value)),
                              );
                            }).toList(),
                            onChanged: (String? newValue) {
                              setState(() {
                                selectedOption = newValue!;
                              });
                            },
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                Container(
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Row(
                      children: [
                        Checkbox(
                          value: localChecked2,
                          onChanged: (bool? value) {
                            setState(() => localChecked2 = value ?? false);
                            widget.onChangedST(localChecked2); // update parent
                          },
                        ),
                        Text('Specific Time:', style: TextStyle(fontSize: 15)),
                        SizedBox(width: 20),
                        SizedBox(
                          width: 100,
                          child: TextField(
                            controller: widget.SpecTimeController,
                            enabled: localChecked2,
                            decoration: InputDecoration(
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(10),
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Row(
                    children: [
                      Checkbox(
                        value: localChecked3,
                        onChanged: (bool? value) {
                          setState(() => localChecked3 = value ?? false);
                          widget.onChangedAL(localChecked3); // update parent
                        },
                      ),
                      Text(
                        'Auto Login on start',
                        style: TextStyle(fontSize: 15),
                      ),
                    ],
                  ),
                ),
                Align(
                  alignment: Alignment.bottomCenter,
                  child: OutlinedButton(
                    onPressed: () async {
                      await widget.Start();
                      Navigator.pop(context);
                    },
                    child: Text('Start Schedular'),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
