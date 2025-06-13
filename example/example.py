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

print("Available providers:")
print("1. OpenAI")
print("2. DeepSeek")
print("3. DuckDuck")

auth_choice = input("Do you need to authenticate? (y/n, default: n): ").lower()
if auth_choice == 'y':
    print("\nIMPORTANT: Make sure you are already logged into your Google account in the browser!")
    print("The server should have opened a browser window when it started.")
    
    provider_choice = input("\nSelect provider (1-3): ")
    provider_map = {"1": "OpenAI", "2": "DeepSeek", "3": "DuckDuck"}
    
    if provider_choice not in provider_map:
        print("Invalid provider choice. Using OpenAI as default.")
        provider = "OpenAI"
    else:
        provider = provider_map[provider_choice]
    
    print(f"Starting authentication for {provider}...")
    
    data = {"provider": provider}
    response = requests.post("http://127.0.0.1:8080/api/auth", json=data)
    print("Response from server = ", response.text)

    result = get_data(response.text)
    if (result["gotError"] == True):
        print("Authentication failed. Make sure you're logged into Google in the browser.")
        exit(0)
    print("Authentication completed!")
else:
    print("Skipping authentication. Make sure you're already authenticated.")

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
    response = requests.post("http://127.0.0.1:8080/api/sendprompt", json={"prompt":promt, "timeOutForAnswer":90})
    result = get_data(response.text)
    if (result["gotError"] !=True):
        print(result["result"])


