import socket
import threading

# Configuration
HOST = 'localhost'
PORT = 5000
NUM_CLIENTS = 500  

def simulate_client(client_id):
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((HOST, PORT))
            fix_message = '35=A|49=SenderCompID|56=TargetCompID|11=123456|54=1\n'
            s.sendall(fix_message.encode('utf-8'))

            response = s.recv(1024).decode('utf-8')
            print(f"Client {client_id} received: {response}")
    except Exception as e:
        print(f"Client {client_id} error: {e}")

def main():
    threads = []
    for i in range(NUM_CLIENTS):
        t = threading.Thread(target=simulate_client, args=(i,))
        t.start()
        threads.append(t)

    for t in threads:
        t.join()

if __name__ == "__main__":
    main()
