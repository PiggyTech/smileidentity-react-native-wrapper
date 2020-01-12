## iOS Setup:

#### -  Navigate to the ios subdirectory
#### -  Run pod install.
```
Add the SmileIdentity framework as shown below.
```
![alt text](https://user-images.githubusercontent.com/13585693/71706727-0bfc4980-2de7-11ea-81d0-38d0164f6574.png "Adding Framework")

#### -  Go to Project > Targets > Frameworks/ Libraries/Embedded content
```
Ensure the Smile Identity Framework has "Embed and Sign" option selected.
```
![alt text](https://user-images.githubusercontent.com/13585693/71706723-0b63b300-2de7-11ea-9cf3-85618a695db8.png "Embedding Framework")

#### -  Navigate to the "react-native-smile-identity" Pod
```
Add `${PROJECT_DIR}/../` as a Framework Header Search path.
```
![alt text](https://user-images.githubusercontent.com/13585693/71706726-0bfc4980-2de7-11ea-9030-6361353b3fc0.png "Embedding Framework")

###### Setup Complete!  🤝
