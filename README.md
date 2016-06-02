Controlling OSC 360 Camera from your PC 
===========
 
This is a part of a software set which goal is to control and possibly stream OSC camera from your PC.
Originally I integrated a HTTP Proxy then a port forwarder, but I realized that we'll just simply use a port forwarder application for the purpose.

Steps
------------

1.) Start FriendsCameraAccess on your phone and connect to your camera. Now your phone's wifi is used for the `OSC` connection.
2.) Download and start https://play.google.com/store/apps/details?id=com.elixsr.portforwarder
3.) Configure Fwd: select `lo` as the network and usually your camera determines the `TCP` forward rule (remote IP and port):


    192.168.43.1:6624


Pick also a phone local port, let's pick `3137`.

    3137

4.) Connect your phoen to the PC through `USB`.
5.) Set up a port forward from your PC to your phone with `ADB`:

    ~/android-sdk-linux/platform-tools/adb forward tcp:3137 tcp:3137

6.) Start a `Rich REST Client` or `Postman` on your PC. Using that:
7.) Start a session

    http://localhost:3137/osc/commands/execute
    {
       "name": "camera.startSession",
       "parameters": {
         "timeout": 50
       }
    }

Response:

    {
      "name": "camera.startSession"
      "state": "done"
      "results": {
        "sessionId": "16b857"
        "timeout": 50
      }
    }


8.) Take a picture

    http://localhost:3137/osc/commands/execute
    {
      "name": "camera.takePicture",
      "parameters": {
        "sessionId": "16b857"
      }
    }

9.) Close the session

    http://localhost:3137/osc/commands/execute
    {
      "name": "camera.closeSession",
      "parameters": {
        "sessionId": "16b857"
      }
    }


10.) Stop `ADB` port forwarding:

    ~/android-sdk-linux/platform-tools/adb forward --remove tcp:3137

11.) Stop the `Fwd` and `FriendsCameraAccess`.

More to come using this setup.
