# Module 2 Final Assignment

###### Nedap University 4.0 2018–2020

###### Author

 - @KesterMeurink

## Installation
The client program can be installed, by navigating to the location of the src files.

The server program can be installed by navigating to the location of the src files and using the gradlew wrapper.
Before doing so make sure a wifi connection to the pi is present.
Then running gradlew build
And gradlew deploy should place the server program on the pi, it is possible that a reboot of the pi is required before it is working properly.
To prevent this a ssh connection to the pi can also be made and the command `sudo systemctl stop num2.service` can be executed to ensure nothing is running during deployment.



## Startup
The Server will run upon boot of the pi and connection of the client to the server is done automatically once the client program is started and a wifi connection with the pi is available.
Both the client and server program require a Files folder to be present at the location of the program, as this will be the folder in which files will be uploaded and downloaded.