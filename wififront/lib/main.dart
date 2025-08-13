import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Wifi Auto Logger',
      theme: ThemeData.dark(),
      debugShowCheckedModeBanner: false,
      home: HomePage(),
    );
  }
}

DateTime now = DateTime.now();

class HomePage extends StatefulWidget {
  @override
  _HomePage createState() => _HomePage();
}

class _HomePage extends State<HomePage> {
  List<TextEditingController> user = [];
  List<TextEditingController> password = [];
  List<String> activityLog = [];
  bool isChecked1 = false;
  bool isChecked2 = false;
  bool isChecked3 = false;
  TextEditingController Interval = TextEditingController();
  TextEditingController SpecTime = TextEditingController();
  void StartSched() {
    setState(() {
      activityLog.add(
        '[${now.hour}:${now.minute}:${now.second}]:\nSchedular Started',
      );
    });
  }

  void addRow() {
    setState(() {
      user.add(TextEditingController());
      password.add(TextEditingController());
      activityLog.add(
        '[${now.hour}:${now.minute}:${now.second}]:\nNew Row added',
      );
    });
  }

  void clearLog() {
    setState(() {
      activityLog.clear();
    });
  }

  void logIN() {
    setState(() {
      activityLog.add('Login Successful');
    });
  }

  void logOUT() {
    setState(() {
      activityLog.add('Logout Successful');
    });
  }

  void removeRow(int index) {
    setState(() {
      user.removeAt(index);
      password.removeAt(index);
      activityLog.add(
        '[${now.hour}:${now.minute}:${now.second}]:\nRow Deleted',
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color.fromARGB(255, 0, 0, 0),
        flexibleSpace: Stack(
          children: [
            Positioned(
              left: 120,
              top: 35,
              child: Text('Wifi Auto Logger', style: TextStyle(fontSize: 25)),
            ),
          ],
        ),
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
                      Start: StartSched,
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
  final VoidCallback Start;

  SchedulerSettingsSheet({
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
                    onPressed: (){widget.Start();
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
