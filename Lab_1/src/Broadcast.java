import java.io.PrintWriter;
import java.util.*;


public class Broadcast {
	
	private int numclients;
	private int activeClients;
	ArrayList<String> queue;
	public HashMap<Integer, PrintWriter> clients = new HashMap<Integer, PrintWriter>();
	
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
		return getNumClients();
	}
	
	public synchronized int removeClient(int id) {
		clients.remove(id);
		decreaseActiveClients();
		return getActiveClients();
	}
	
	
	public synchronized void printBroadcast(int id) {
		for (int i = 0; i < clients.size(); i++) {
			if (i==id-1) {
				continue;
			}
			
			clients.get(i).println(queue.get(0));
			
		}
		queue.remove(0);
	}
}
