# bingchat4urapp
An application that allows you to use BingChat in your Windows or Linux applications using the API of a local server. This application does not rely on reverse engineering APIs. Instead, it utilizes the Java Chromium Embedded Framework (JCEF) to emulate a browser environment and Selenium for browser control. This approach ensures a seamless and robust interaction with BingChat, providing a reliable tool for your application needs.
[Video demonstration](https://youtu.be/lHJaL333qAE)

## How to run
1. Download OpenJDK version 17 or higher from the official Oracle website.
2. Download the .jar file from the Releases section.
3. Run .jar file using following command: `java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED -jar bingchat4urapp_server-0.0.1-SNAPSHOT.jar` 
4. The application will be launched on port 8080.

## Proxy Support
If you want to use a proxy, you need to pass the proxy address as a parameter to the .jar file. Please note that only SOCKS5 proxies are supported. For example, you can pass the proxy to the application through the console like this:

```bash
java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED -jar bingchat4urapp_server-0.0.1-SNAPSHOT.jar 127.0.0.1:8521
```

## How to use

Here are the request types and their expected responses (а full example of the program can be found in the “example” folder):

### POST /api/auth

This endpoint is used to create an authentication task. It expects a request body with `login` and `password` fields.

Example using Python:

```python
url = "http://127.0.0.1:8080/api/auth"
data = {"login": login, "password": password}
response = requests.post(url, json=data)
    
print("Response from server =", response.text)
```
The server will return the ID of the created task.

### POST /api/createchat
This endpoint is used to create a chat. It expects a JSON body with a `type` field, which should be a number:
* `1` - More creative
* `2` - More Balanced
* `3` - More Precise

Example using Python:
```python
import requests

url = "http://127.0.0.1:8080/api/createchat"
data = {"type": 3}
response = requests.post(url, json=data)

print(response.text)
```
The server will return the ID of the created task.

### POST /api/sendpromt
This endpoint is used to create a prompt task. It expects a JSON body with `promt` and `timeOutForAnswer` fields. The `timeOutForAnswer` field should be a number representing the timeout for the answer in seconds.

Example using Python:
```python
import requests

url = "http://127.0.0.1:8080/api/sendpromt"
data = {"promt": "your_prompt", "timeOutForAnswer": 90}
response = requests.post(url, json=data)

print(response.text)
```
The server will return the ID of the created task.

### GET /api/get/{id}
This endpoint is used to retrieve a task by its ID. It expects the `id` to be passed as a part of the URL.

Example using Python:
```python
import requests

url = "http://localhost:8080/get/1"
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

### GET /api/exit
This endpoint is used to close server app correctly.

Example using Python:
```python
import requests

response = requests.get("http://127.0.0.1:8080/api/exit")
print("Session ended.")
```