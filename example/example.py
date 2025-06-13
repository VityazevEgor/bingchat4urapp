import requests
import time
import sys

# ANSI Color codes
class Colors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKCYAN = '\033[96m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

def print_header(text):
    print(f"\n{Colors.HEADER}{Colors.BOLD}{'='*50}")
    print(f"  {text}")
    print(f"{'='*50}{Colors.ENDC}")

def print_success(text):
    print(f"{Colors.OKGREEN}✓ {text}{Colors.ENDC}")

def print_error(text):
    print(f"{Colors.FAIL}✗ {text}{Colors.ENDC}")

def print_warning(text):
    print(f"{Colors.WARNING}⚠ {text}{Colors.ENDC}")

def print_info(text):
    print(f"{Colors.OKCYAN}ℹ {text}{Colors.ENDC}")

def get_data(task_id):
    """Poll the server for task completion"""
    print_info(f"Waiting for task {task_id} to complete...")
    while True:
        try:
            response = requests.get(f"http://127.0.0.1:8080/api/get/{task_id}")
            if response.status_code != 200:
                print_error(f"Failed to get task status. HTTP {response.status_code}")
                return None
            
            data = response.json()
            if 'isFinished' in data and data["isFinished"]:
                return data
            else:
                print(".", end="", flush=True)
                time.sleep(0.5)
        except requests.exceptions.RequestException as e:
            print_error(f"Network error: {e}")
            return None
        except Exception as e:
            print_error(f"Unexpected error: {e}")
            return None

def select_provider():
    """Let user select LLM provider"""
    print_header("LLM Provider Selection")
    
    providers = {
        "1": "OpenAI",
        "2": "DeepSeek", 
        "3": "DuckDuck"
    }
    
    print(f"{Colors.OKBLUE}Available providers:{Colors.ENDC}")
    for key, value in providers.items():
        print(f"  {Colors.BOLD}{key}.{Colors.ENDC} {value}")
    
    while True:
        choice = input(f"\n{Colors.OKCYAN}Select provider (1-3): {Colors.ENDC}").strip()
        if choice in providers:
            selected_provider = providers[choice]
            print_success(f"Selected provider: {selected_provider}")
            return selected_provider
        else:
            print_error("Invalid choice. Please select 1, 2, or 3.")

def set_preferred_provider(provider):
    """Set the preferred provider on the server"""
    print_info(f"Setting {provider} as preferred provider...")
    
    try:
        data = {"provider": provider}
        response = requests.post("http://127.0.0.1:8080/api/setPreferredProvider", json=data)
        
        if response.status_code == 200:
            print_success(f"Successfully set {provider} as preferred provider")
            return True
        else:
            print_error(f"Failed to set preferred provider. HTTP {response.status_code}")
            print_error(f"Response: {response.text}")
            return False
    except requests.exceptions.RequestException as e:
        print_error(f"Network error setting preferred provider: {e}")
        return False

def authenticate(provider):
    """Perform authentication for the selected provider"""
    print_header("Authentication")
    
    print_warning("IMPORTANT: Make sure you are logged into your Google account in the browser!")
    print_info("The server should have opened a browser window when it started.")
    
    try:
        data = {"provider": provider}
        response = requests.post("http://127.0.0.1:8080/api/auth", json=data)
        
        if response.status_code != 201:
            print_error(f"Failed to create auth task. HTTP {response.status_code}")
            return False
        
        task_id = response.text.strip()
        if not task_id.isdigit():
            print_error(f"Invalid task ID received: {task_id}")
            return False
            
        result = get_data(int(task_id))
        print()  # New line after dots
        
        if result is None:
            print_error("Failed to get authentication result")
            return False
            
        if result.get("gotError", True):
            print_error("Authentication failed!")
            print_error("Make sure you're logged into Google in the browser window.")
            return False
        else:
            print_success("Authentication completed successfully!")
            return True
            
    except requests.exceptions.RequestException as e:
        print_error(f"Network error during authentication: {e}")
        return False

def create_chat():
    """Create a new chat session"""
    print_info("Creating new chat session...")
    
    try:
        response = requests.post("http://127.0.0.1:8080/api/createchat", json={})
        
        if response.status_code != 201:
            print_error(f"Failed to create chat. HTTP {response.status_code}")
            return False
        
        task_id = response.text.strip()
        if not task_id.isdigit():
            print_error(f"Invalid task ID received: {task_id}")
            return False
            
        result = get_data(int(task_id))
        print()  # New line after dots
        
        if result is None or result.get("gotError", True):
            print_error("Failed to create chat session")
            return False
        else:
            print_success("Chat session created successfully!")
            return True
            
    except requests.exceptions.RequestException as e:
        print_error(f"Network error creating chat: {e}")
        return False

def send_prompt(prompt_text):
    """Send a prompt to the LLM"""
    try:
        data = {
            "prompt": prompt_text,
            "timeOutForAnswer": 90
        }
        response = requests.post("http://127.0.0.1:8080/api/sendprompt", json=data)
        
        if response.status_code != 201:
            print_error(f"Failed to send prompt. HTTP {response.status_code}")
            return None
        
        task_id = response.text.strip()
        if not task_id.isdigit():
            print_error(f"Invalid task ID received: {task_id}")
            return None
            
        result = get_data(int(task_id))
        print()  # New line after dots
        
        if result is None:
            return None
            
        if result.get("gotError", True):
            print_error("Error processing prompt:")
            print_error(result.get("result", "Unknown error"))
            return None
        else:
            return result.get("result", "No response received")
            
    except requests.exceptions.RequestException as e:
        print_error(f"Network error sending prompt: {e}")
        return None

def main():
    print_header("LLMapi4free Client")
    print_info("Welcome to the LLMapi4free client!")
    
    # Step 1: Select provider
    provider = select_provider()
    
    # Step 2: Set preferred provider
    if not set_preferred_provider(provider):
        print_error("Failed to set preferred provider. Exiting.")
        sys.exit(1)
    
    # Step 3: Ask about authentication
    print_header("Authentication Setup")
    auth_choice = input(f"{Colors.OKCYAN}Do you want to authenticate now? (y/N): {Colors.ENDC}").lower().strip()
    
    if auth_choice in ['y', 'yes']:
        if not authenticate(provider):
            print_warning("Authentication failed, but you can still try sending prompts.")
            print_info("The system will attempt authentication automatically if needed.")
    else:
        print_info("Skipping authentication. System will authenticate automatically if needed.")
    
    # Step 4: Create chat session
    if not create_chat():
        print_error("Failed to create chat session. Exiting.")
        sys.exit(1)
    
    # Step 5: Chat loop
    print_header("Chat Session")
    print_info(f"Chat started with {provider}. Type 'exit' to quit.")
    print_info("You can now send prompts to the LLM!")
    
    while True:
        try:
            prompt = input(f"\n{Colors.BOLD}You:{Colors.ENDC} ").strip()
            
            if prompt.lower() == 'exit':
                print_info("Exiting chat session...")
                try:
                    requests.get("http://127.0.0.1:8080/api/exit")
                    print_success("Server shutdown initiated. Goodbye!")
                except:
                    print_warning("Could not notify server of exit, but closing client.")
                break
            
            if not prompt:
                print_warning("Please enter a prompt or 'exit' to quit.")
                continue
            
            print_info("Processing your prompt...")
            response = send_prompt(prompt)
            
            if response:
                print(f"\n{Colors.OKGREEN}{Colors.BOLD}AI:{Colors.ENDC} {response}")
            else:
                print_error("Failed to get response. Please try again.")
                
        except KeyboardInterrupt:
            print_info("\nGoodbye!")
            break
        except Exception as e:
            print_error(f"Unexpected error: {e}")

if __name__ == "__main__":
    main()