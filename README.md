# Recognition

HAR Application
-------

The project requiered 2 applications, one for data collection and another for the real-time classification of human activities. For this application, the real-time classifier, there are 2 main screens. The first screen show a live reading of the accelerometer on all 3 axies, X,Y, and Z. The main objective on this screen is to calibrate the readings of the sensor for when the microcontroller is perpenticular to the floor, wall, and height.

The second screen is were the magic happens, a live reading of the heart rate sensor is soon with a visual indication that shows if the sensors are connected. The values of the accelerometer are sent to a cloud hosted server on Linode were the information is preprocessed and classified with a python script. The target label is returned on to the application and shown to the user.
