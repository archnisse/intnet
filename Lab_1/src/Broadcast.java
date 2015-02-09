import java.io.PrintWriter;
import java.util.*;


public class Broadcast {
	
	private int numclients;
	private int activeClients;
	ArrayList<String> queue;
	public HashMap<Integer, PrintWriter> clients = new HashMap<Integer, PrintWriter>();
	ArrayList<Integer> currentClients = new ArrayList<Integer>();
	
	public Broadcast() {
		queue = new ArrayList<String>(10);
		numclients = 0;
		activeClients = 0;
	}
	
	public synchronized void addMessage(String message) {
		queue.add(message);
	}

	
	private synchronized void incrementNumClients() {
		numclients++;
	}
	
	private synchronized int getNumClients() {
		return numclients;
	}
	
	private synchronized void incrementActiveClients() {
		activeClients++;
	}
	
	private synchronized void decreaseActiveClients() {
		activeClients--;
	}
	
	public synchronized int getActiveClients() {
		return activeClients;
	}
	
	public synchronized int addClient(PrintWriter writer) {
		incrementNumClients();
		incrementActiveClients();
		clients.put(numclients, writer);
		currentClients.add(numclients);
		return getNumClients();
	}
	
	public synchronized int removeClient(int id) {
		clients.remove(id);
		decreaseActiveClients();
		int tmp = -1;
		for (int i = 0; i < currentClients.size(); i++) {
			if (currentClients.get(i) == id) {
				tmp = i;
				break;
			}
		}
		if (tmp >= 0) {
			currentClients.remove(tmp);
		}
		return getActiveClients();
	}
	
	
	public synchronized void printBroadcast(int id) {
		for (int i = 0; i < currentClients.size(); i++) {
			if (currentClients.get(i) == id) {
				continue;
			}
			clients.get(currentClients.get(i)).println(queue.get(0));
			
		}
		queue.remove(0);
	}
}
