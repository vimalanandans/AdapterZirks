# AdapterZirks

This repository includes adapter Zirks used to enable control of hardware components from the Bezirk ecosystem. These adapters take on the tasks of detecting specific hardware components, reading their states, and actuating them. These tasks are brought into the Bezirk ecosystem through the use of Bezirk [events](http://developer.bezirk.com/documentation/key_terms.php). Instead of having to learn yet another poorly designed and documented SDK, you can simply send a Bezirk event to control your hardware.

Typically, adapters use generic [hardware events](https://github.com/Bezirk-Bosch/HardwareEvents) to accept commands and return results. Generic events are used to provide a uniform way of detecting and working with the basic features of specific types of hardware (e.g. lights, beacons, etc.). This means if you switch light manufacturers you can switch from using, say, a Philips Hue adapter to using an OSRAM Lightify adapter without having to alter the events your Zirk sends and receives. In fact, you may not even have to alter anything except the adapter your Zirk imports and initializes. You can also choose to use both at the same time, enabling your Zirk to easily actuate lights from both manufacturers without having to worry about the details of how each works under the hood.

Assuming you need to do more than the generic events allow, some adapters use custom events to access the unique features of the hardware they work with. For example, you can use generic beacon events with the Estimote adapter if you just need to detect beacons in general. However, if you want to use the full range of features provided by Estimote stickers (A.K.A. nearables) you can instead use the more specific nearable messages provided by the Estimote adapter.

The following table summarizes our currently usable adapters:

Name | Version | Gradle Import | Supported Features | Example Code
--- | --- | --- | --- | ---
Estimote | 1.1.0 | `compile('com.bezirk:estimoteadapter:1.1.0@aar') { transitive = true }` | Detect iBeacons, detect Estimote nearables, get Estimote nearable attributes if detected as a generic beacon | [Test Zirk](https://github.com/Bezirk-Bosch/AdapterZirks/blob/master/EstimoteAdapter/app/src/main/java/com/bezirk/adapter/estimote/MainActivity.java), [Smart Desk](https://github.com/Bezirk-Bosch/SmartDeskZirk/blob/master/AndroidBeaconDetector/src/main/java/com/bezirk/smartdesk/beacondetector/MainActivity.java)
Lightify | 0.0.2 | `compile(group: 'com.bezirk', name: 'lightify', version: '0.0.2')` | Discover gateways, detect lights, turn lights on and off, set light brightness | [Test Zirk](https://github.com/Bezirk-Bosch/AdapterZirks/blob/master/lightify/src/test/java/com/bezirk/adapter/lightify/LightifyZirkTest.java)
PhilipsHue | 0.0.5 | `compile(group: 'com.bezirk', name: 'PhilipsHue', version: '0.0.5')` | Discover bridges, detect lights, turn lights on and off, set light brightness, set light color, get light state | [Test Zirk](https://github.com/Bezirk-Bosch/AdapterZirks/blob/master/PhilipsHue/src/test/java/com/bezirk/adapter/philips/hue/PhilipsHueZirkTest.java), [Smart Desk](https://github.com/Bezirk-Bosch/SmartDeskZirk/blob/master/SmartDesk/src/main/java/com/bezirk/smartdesk/Main.java)
WeMo | 1.2.0 | `compile(group: 'com.bezirk', name: 'wemoadapter', version: '1.2.0')` | Discover switches, turn switches on and off | [Test Zirk](https://github.com/Bezirk-Bosch/AdapterZirks/blob/master/wemoadapter/src/test/java/com/bezirk/adapter/belkin/wemo/WeMoZirkTest.java), [Smart Desk](https://github.com/Bezirk-Bosch/SmartDeskZirk/blob/master/SmartDesk/src/main/java/com/bezirk/smartdesk/Main.java)
