import os
import getpass
import requests
import time

def get_data(id):
    while True:
        response = requests.get(f"http://127.0.0.1:8080/api/get/{id}")
        data = response.json()
        if 'isFinished' in data and data["isFinished"]:
            return data
        else:
            time.sleep(0.5)  # Пауза перед следующим запросом

login = input("Enter login for Miscrosoft acc or type skip (if u already auth): ")
if (login!="skip"):
    password = getpass.getpass("Enter password: ")

    data = {"login": login, "password": password}
    response = requests.post("http://127.0.0.1:8080/api/auth", json=data)
    print("Response from server = ", response.text)

    result = get_data(response.text)
    if (result["gotError"] == True):
        print("Can't auth. Recheck ur password. Or there is can be some problems with new Bing Design. Just try again")
        exit(0)

print("Finsihed auth!")

response = requests.post("http://127.0.0.1:8080/api/createchat", json={"type":3})
result = get_data(response.text)
if (result["gotError"] == True):
    print("Can't create chat")
    exit

promt = ""
while (True):
    promt = input("Enter promt: ")
    if (promt == "exit"):
        requests.get("http://127.0.0.1:8080/api/exit")
        break
    response = requests.post("http://127.0.0.1:8080/api/sendpromt", json={"promt":promt, "timeOutForAnswer":90})
    result = get_data(response.text)
    if (result["gotError"] !=True):
        print(result["result"])


