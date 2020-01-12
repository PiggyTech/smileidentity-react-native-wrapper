/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  View,
  Button,
  Text,
  StatusBar,
} from 'react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';
import {NativeEventEmitter} from 'react-native';
import SmileIdentity from 'react-native-smile-identity';

export default class App extends Component<{}> {
  /**
   * Event Listeners
   */
  componentDidMount() {
    const eventEmitter = new NativeEventEmitter(SmileIdentity);
    eventEmitter.addListener('FaceChanged', faceChangedEventValue => {
      console.log(`Face changed ${faceChangedEventValue}`);
    });
    eventEmitter.addListener('Error', errorMessage => {
      console.log(errorMessage);
    });
  }
  render() {
    const onCompleteCallback = bitmapFilePath => {
      console.log(`${bitmapFilePath} `);
    };

    const options = {
      photoQuality: 0.9,
      fileName: 'TempFile',
      smileRequired: true,
      captureSmileManually: false,
      cameraFacingFront: true,
    };

    const captureSelfie = () => {
      SmileIdentity.captureSelfie(options, onCompleteCallback);
    };

    return (
      <>
        <StatusBar barStyle="dark-content" />
        <SafeAreaView>
          <View style={styles.container}>
            <Text style={styles.welcome}>☆Xcode Library example☆</Text>
            <Text style={styles.instructions}>STATUS: {this.state.status}</Text>
            <Text style={styles.welcome}>☆NATIVE CALLBACK MESSAGE☆</Text>
            <Text style={styles.instructions}>{this.state.message}</Text>
          </View>

          <View style={styles.engine}>
            <Text style={styles.footer}>Engine: Hermes</Text>
            <Button title="Take Selfie Button" onPress={captureSelfie} />
          </View>
        </SafeAreaView>
      </>
    );
  }
}

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  container: {
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  engine: {
    left: 0,
    top: 40,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});