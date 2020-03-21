# smileidentity-react-native-wrapper
React Native wrapper for smile identity Mobile SDK
&nbsp;
#### -  Installation:
`npm i smileidentity-react-native-wrapper` OR `yarn add smileidentity-react-native-wrapper`
```
Run `react-native link` to link this library on React Native < 0.60.0
```
&nbsp;
#### -  Download the required Smile Identity Libraries for Android or iOS on the [Smile Identity Website](https://test-smileid.herokuapp.com/sdk)
&nbsp;
## Android Setup:
#### -  Follow the Instructions on the [Android Readme](https://github.com/PiggyTech/smileidentity-react-native-wrapper/tree/master/android)
&nbsp;
## iOS Setup:
#### -  Follow the Instructions on the [iOS Readme](https://github.com/PiggyTech/smileidentity-react-native-wrapper/tree/master/ios)
&nbsp;


## -  Usage:  

#### Import Smile Identity into your JavaScript Project

```javascript
import SmileIdentity from 'react-native-smile-identity';
```

SmileIdentity has 3 functions
- [startCapturingSelfie](#startCapturingSelfie)
- [pauseCapturing](#pauseCapturing)
- [stopCapturing](#stopCapturing)

### `startCapturingSelfie`
Capture a selfie with:
```javascript
SmileIdentity.captureSelfie(options, onCompleteCallback);
```

Options:
Key                       | Value 
--------------------------| -------------
photoQuality              | Double (Value: 0 to 1)
fileName                  | String
smileRequired             | Boolean
captureSmileManually      | Boolean
cameraFacingFront         | Boolean


onCompleteCallback returns a file path to the captured image
```javascript
const onCompleteCallback = bitmapFilePath => {
      console.log(`${bitmapFilePath} `);
      pauseCapturingSelfie();
};
```

### `pauseCapturing`
Manually pause capturing with
```javascript
SmileIdentity.pauseCapturing()
```

### `stopCapturing`
Manually stop capturing **and remove camera overlay** with
```javascript
SmileIdentity.stopCapturing()
```

## Listening to Events (Optional)
You can listen to face changed events and errors from the camera preview

Import NativeEventEmitter
```javascript
import {NativeEventEmitter} from 'react-native';
```

Listen to `FaceChanged` and `Error` events

```javascript
componentDidMount() {
    const eventEmitter = new NativeEventEmitter(SmileIdentity);
    eventEmitter.addListener('FaceChanged', faceChangedEventValue => {
      console.log(`Face changed ${faceChangedEventValue}`);
    });
    eventEmitter.addListener('Error', errorMessage => {
      console.log(errorMessage);
    });
  }
  ```
  
## Example Project
You can check out the [Sample App](https://github.com/PiggyTech/smileidentity-react-native-wrapper/tree/master/testApp)

## Authors

* **Oyeleke Okiki** - [Yeet!](http://oyelekeokiki.com)
* [OJ!](mailto:oj@piggyvest.com)

