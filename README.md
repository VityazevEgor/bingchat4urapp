# bingchat4urapp
An application that allows you to use BingChat in your Windows or Linux applications using the API of a local server. This application does not rely on reverse engineering APIs. Instead, it utilizes the Java Chromium Embedded Framework (JCEF) to emulate a browser environment and Selenium for browser control. This approach ensures a seamless and robust interaction with BingChat, providing a reliable tool for your application needs.
[Video demonstration](https://youtu.be/lHJaL333qAE)

## How to run
1. Download OpenJDK version 17 or higher from the official Oracle website.
2. Download the .jar file from the Releases section.
3. Depending on your operating system, perform the following steps:
   - **Linux**: Execute the command `java -jar bingchat4urapp_server-0.0.1-SNAPSHOT.jar`. The application will be launched on port 8080.
   - **Windows**: Execute the command `java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED -jar bingchat4urapp_server-0.0.1-SNAPSHOT.jar`. The application will be launched on port 8080.

## Proxy Support
If you want to use a proxy, you need to pass the proxy address as a parameter to the .jar file. Please note that only SOCKS5 proxies are supported. For example, you can pass the proxy to the application through the console like this:

```bash
java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED -jar bingchat4urapp_server-0.0.1-SNAPSHOT.jar 127.0.0.1:8521
```

## How to use

This server accepts several types of requests and returns specific results based on the provided SpringBoot controller code. Here are the request types and their expected responses (а full example of the program can be found in the “example” folder):

### POST /auth

This endpoint is used to create an authentication task. It expects a request body with `login` and `password` fields.

Example using Python:

```python
import requests

url = "http://localhost:8080/auth"
data = {"login": "your_login", "password": "your_password"}
response = requests.post(url, data=data)

print(response.text)
```
The server will return the ID of the created task.

### POST /createchat
This endpoint is used to create a chat. It expects a request body with a type field, which should be a number.
* 1 - More creative
* 2 - More Balanced
* 3 - More Precise

Example using Python:
```python
import requests

url = "http://localhost:8080/createchat"
data = {"type": "2"}
response = requests.post(url, data=data)

print(response.text)
```
The server will return the ID of the created task.

### POST /sendpromt
This endpoint is used to create a prompt task. It expects a request body with promt and timeOutForAnswer fields. The timeOutForAnswer field should be a number.

Example using Python:
```python
import requests

url = "http://localhost:8080/sendpromt"
data = {"promt": "your_prompt", "timeOutForAnswer": "90"}
response = requests.post(url, data=data)

print(response.text)
```
The server will return the ID of the created task.

### GET /get
This endpoint is used to get a task. It expects an id parameter in the request.

Example using Python:
```python
import requests

url = "http://localhost:8080/get?id=1"
response = requests.get(url)

print(response.text)
```
The server will return a TaskModel object with the following fields:

* id: The ID of the task.
* type: The type of the task (1 for auth Bing, 2 for send prompt to Bing).
* data: The data of the task.
* isFinished: A boolean indicating whether the task is finished.
* gotError: A boolean indicating whether there was an error.
* result: The result of the task (answer on your ptomt)