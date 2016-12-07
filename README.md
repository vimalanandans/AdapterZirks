# AdapterZirks

This repository includes adapter Zirks which allow commercial IoT hardware and services to be easily controlled with Bezirk. These adapters can detect the specific hardware, read their states, and actuate them. With these adapters, you can simply use Bezirk [events](http://developer.bezirk.com/documentation/key_terms.php) to read and control the hardware, saving you from having to learn various, sometimes confusing, proprietary SDKs.

Typically, adapters use generic [hardware events](https://github.com/Bezirk-Bosch/HardwareEvents) to accept commands and return results. Generic events provide a uniform way of working with the basic features of specific hardware categories (e.g. lights, beacons, etc.). This means if you change light manufacturers (say, from Philips to OSRAM), you can just change the corresponding adapter without having to alter the events your Zirk sends and receives. You can also choose to use both adapters (Philips Hue and Osram Lightify) at the same time, enabling your Zirk to easily actuate lights from both manufacturers without having to worry about under-the-hood details.

Assuming you need to do more than the generic events allow, some adapters use custom events to access the unique features of the hardware they work with. For example, with the Estimote adapter, if you need more than generic beacon detection and want to use the full range of features provided by Estimote stickers (A.K.A. Nearables) you can use the more specific Nearable messages provided by the Estimote adapter.

###To Test An Adapter

1. Open the AdapterZirk project in Android Studio
2. Click Gradle Sync button on the right side in the Gradle tab.
3. In that Gradle tab, find the adapter of interest in the list under AdapterZirks and expand it
4. Then go into Tasks -> Build and double click testClasses
5. Then right click the corresponding test class in the project view on the left side and select Run ... main()
   For example: if testing PhilipsHue adapter, right click the PhilipsHueZirkTest.java under PhilipsHue/src/test/java/com.bezirk.adapter.philips.hue and select "Run PhilipsHueZirk...main()"

###Currently available adapters

Name | Version | Gradle Import | Supported Features | Example Code
--- | --- | --- | --- | ---
Estimote | 1.1.0 | `compile('com.bezirk:estimoteadapter:1.1.0@aar') { transitive = true }` | Detect iBeacons, detect Estimote nearables, get Estimote nearable attributes if detected as a generic beacon | [Test Zirk](https://github.com/Bezirk-Bosch/AdapterZirks/blob/master/EstimoteAdapter/app/src/main/java/com/bezirk/adapter/estimote/MainActivity.java), [Smart Desk](https://github.com/Bezirk-Bosch/SmartDeskZirk/blob/master/AndroidBeaconDetector/src/main/java/com/bezirk/smartdesk/beacondetector/MainActivity.java)
Lightify | 0.0.2 | `compile(group: 'com.bezirk', name: 'lightify', version: '0.0.2')` | Discover gateways, detect lights, turn lights on and off, set light brightness | [Test Zirk](https://github.com/Bezirk-Bosch/AdapterZirks/blob/master/lightify/src/test/java/com/bezirk/adapter/lightify/LightifyZirkTest.java)
PhilipsHue | 0.0.5 | `compile(group: 'com.bezirk', name: 'PhilipsHue', version: '0.0.5')` | Discover bridges, detect lights, turn lights on and off, set light brightness, set light color, get light state | [Test Zirk](https://github.com/Bezirk-Bosch/AdapterZirks/blob/master/PhilipsHue/src/test/java/com/bezirk/adapter/philips/hue/PhilipsHueZirkTest.java), [Smart Desk](https://github.com/Bezirk-Bosch/SmartDeskZirk/blob/master/SmartDesk/src/main/java/com/bezirk/smartdesk/Main.java)
WeMo | 1.2.0 | `compile(group: 'com.bezirk', name: 'wemoadapter', version: '1.2.0')` | Discover switches, turn switches on and off | [Test Zirk](https://github.com/Bezirk-Bosch/AdapterZirks/blob/master/wemoadapter/src/test/java/com/bezirk/adapter/belkin/wemo/WeMoZirkTest.java), [Smart Desk](https://github.com/Bezirk-Bosch/SmartDeskZirk/blob/master/SmartDesk/src/main/java/com/bezirk/smartdesk/Main.java)
Wunderground | 0.0.1 | `compile(group: 'com.bezirk', name: 'wundergroundadapter', version: '0.0.1')` | Get current temperature, humidity, and pressure for state (or country) and city | [Test Zirk](https://github.com/Bezirk-Bosch/AdapterZirks/blob/master/wundergroundadapter/src/test/java/com/bezirk/adapter/wunderground/WundergroundZirkTest.java)
