//package com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

//import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.*;
import com.sun.net.httpserver.*;

public class Server {
	
	final String login = "login";
	final String listComp = "listcomp";
	final String viewComp = "viewcomp";
	final String bookmark = "bookmark";
	final String newUser = "newuser";
	final String newComp = "newcomp";
	final String removeComp = "removecomp";
	
	final String cname = "cname";
	final String uname = "uname";
	final String pword = "pword";
	final String info = "info";
	final String type = "type";
	final String cid = "cid";
	final String uid = "uid";
	
	
	final String path = "database.db";
	
	public Server() throws Exception {
		// Set up database
		setUpDatabase("database.db");
		
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1",1235), 0);
        server.createContext("/", new MyHandler());
        server.createContext("/knas", new OtherHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server is running.");
	}
	

	public static void main(String[] args) throws Exception {
    	new Server();
    }

    private class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
        	System.out.println("=====");
        	
        	
        	// Check the cookie from the client.
        	cookieHandler(t);
        	
        	System.out.println("HttpRequestHeaders:");
        	
        	
            Headers resphead = t.getResponseHeaders();
            
            List<String> values = new ArrayList<String>();
            values.add("test=japp");
            resphead.put("Set-Cookie", values);
            
        	URI uri = t.getRequestURI();
        	System.out.println("urin: |" + uri.getPath() + "|");
        	
        	
            String reqPath = uri.getPath().substring(1);
            try {
	        	Path path = Paths.get(reqPath);
	        	byte[] data = Files.readAllBytes(path);
	            t.sendResponseHeaders(200, data.length);
	            
	            OutputStream os = t.getResponseBody();
	            os.write(data);
	            os.close();
	            
            } catch (Exception e) {
            	OutputStream os = t.getResponseBody();
            	String resp = "404 (Not Found)\n";
            	System.err.println("Error: " + e);
	            t.sendResponseHeaders(404, resp.length());
	            os.write(resp.getBytes());
	            os.close();
            }
            System.out.println("====");
        }
    }
    
    

    private void setUpDatabase(String path) {
    	SQLiteConnection db;
		java.util.logging.Logger.getLogger("com.almworks.sqlite4java").setLevel(java.util.logging.Level.OFF);
		
		File dbFile = new File(path);
		boolean newDB = (dbFile.exists()) ? false : true;
		db = new SQLiteConnection(dbFile);
		try {
		//	db.setBusyTimeout(800);
			db.open(true);
			if(newDB) {
				System.out.println("Creating new DB");
				db.exec("BEGIN TRANSACTION");
				db.exec("CREATE TABLE users (uid INTEGER PRIMARY KEY AUTOINCREMENT, "
				+this.uname+" varchar(64), "+this.pword+" VARCHAR(64))");
				db.exec("CREATE TABLE bookmarks (uid INTEGER FOREIGN KEY, cid INTEGER)");
				db.exec("CREATE TABLE company (cid INTEGER PRIMARY KEY AUTOINCREMENT, "
				+this.cname+" varchar(64), "+this.type+" VARCHAR(64), "+this.info+" TEXT)");
				db.exec("COMMIT");
				db.dispose();
				System.out.println("Database was created.");
			} else {
				System.out.println("Database was found.");
			}

			//db.exec("BEGIN");
		} catch(Exception e) {
			System.err.println("Could not connect to the database " + e);
		} finally {
			db.dispose();
		}
	}
    
    /**
     * Opens the database specified by path
     * @param path, path to the database file
     * @return SQLiteConnection on success, null on failure
     */
    private SQLiteConnection openDatabase(String path) {
    	SQLiteConnection db;
		File dbFile = new File(path);
		db = new SQLiteConnection(dbFile);
    	try {
    		db.open(true);
    		db.exec("BEGIN");
    		return db;
    	} catch (Exception e) {
    		System.err.println("Opening database failed: " + e);
    		return null;
    	}
    }
    
    private void closeDatabase(SQLiteConnection db) {
    	try {db.exec("COMMIT"); } catch (Exception e) {}
    	db.dispose();
    }
    
    private int cookieHandler(HttpExchange t) {
    	Headers head = t.getRequestHeaders();

    	List<String> cookie_field = head.get("Cookie");
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0;  i < cookie_field.size(); i++) {
    		sb.append(cookie_field.get(i));
    	}
    	String cookie = sb.toString();
    	String[] cookies = cookie.split(";");
    	for (String s : cookies) {
    		System.out.println("Cookie: " + s);
    		if (s.contains("server")) {
    			System.out.println("Cookie contained server");
    			String server = s.split("=")[1];
    			switch (server) {
        			case login :
        				System.out.println("Login set in cookie, run login code");
        				login(cookies, head);
        				break;
        			case listComp :
        				System.out.println("Listcomp set in cookie, run listcomp code");
        				listComp(cookies, head);
        				break;
        			case viewComp :
        				System.out.println("viewcomp set in cookie, run viewcomp code");
        				viewComp(cookies, head);
        				break;
        			case bookmark :
        				System.out.println("bookmark set in cookie, run bookmark code");
        				bookmark(cookies, head);
        				break;
        			case newUser :
        				newUser(cookies, head);
        				break;
        			case newComp :
        				newComp(cookies, head);
        				break;
        			case removeComp :
        				removeComp(cookies, head);
        				break;
    			}
    			// No reason to look at further cookies
    			break;
    		}
    	}
    	return -1;
    }
    
    /**
     * Performs the log in of the user
     * @param cookie, read the cookie fields
     * @return 0 on success, -1 on failure.
     */
    private int login(String[] cookie, Headers header) {
    	SQLiteConnection db;
    	String uname = "", pword = "";
    	db = openDatabase(path);
    	if (db != null) {
    		// Success opening a database
    		
    		// Get the values needed from the cookie
    		for (String cook : cookie) {
    			if (cook.contains(this.uname)) {
    				uname = cook.split("=")[1];
    			}
    			if (cook.contains(this.pword)) {
    				pword = cook.split("=")[1];
    			}
    		}
    		// Check if they were found
    		if (pword.isEmpty() || uname.isEmpty()) {
    			System.err.println("No username or password found in cookie.");
    			closeDatabase(db);
    			return -1;
    		}
    		
    		// Check the database
	    	try {
	    		SQLiteStatement st = null;
	    		st = db.prepare("SELECT uname FROM users WHERE uname = ? AND pword = ?");
	    		st.bind(1, uname);
	    		st.bind(2, pword);
	    		if (st.step()) {
	    			// A row was found, successful login.
	                List<String> values = new ArrayList<String>();
	                values.add("uname="+uname);
	                header.put("Set-Cookie", values);
	    		}
    			closeDatabase(db);
	    		return 0;
	    	} catch (Exception e) {
	    		System.err.println("Failed to read the database");
    			closeDatabase(db);
	    		return -1;
	    	}
    	} else {
    		System.err.println("Failed to open database");
    		return -1;
    	}
    }
    
    /**
     * Populates a cookie with all the companies in the database.
     * Format:
     * listcomp=cid:cname,cid:cname,cid:cname
     * @param cookie, the cookie
     * @param header, header for the response
     * @return 0 on success, -1 on error.
     */
    private int listComp(String[] cookie, Headers header) {
    	SQLiteConnection db;
    	String prefix = "";
        List<String> values = new ArrayList<String>();
    	StringBuilder sb = new StringBuilder();
    	db = openDatabase(path);
    	if (db != null) {
    		// Success
    		
    		// Gather all the companies from the database
	    	try {
	    		SQLiteStatement st = null;
	    		st = db.prepare("SELECT cid, cname FROM company ORDER BY cname");
	    		while (st.step()) {
	    			// First company will not have the comma, the rest will, but not the last. Perfect.
	    			sb.append(prefix);
	    			prefix = ",";
	    			sb.append(st.columnInt(0));
	    			sb.append(":");
	    			sb.append(st.columnString(1));
	    		}
                values.add("listcomp="+sb.toString());
                header.put("Set-Cookie", values);
    			closeDatabase(db);
	    		return 0;
	    	} catch (Exception e) {
	    		System.err.println("Failed to read the database");
    			closeDatabase(db);
	    		return -1;
	    	}
    		
    	} else {
    		System.err.println("Failed to open database");
    		return -1;
    	}
    }
    
    /**
     * Populates a cookie with a company's information
     * @param cookie
     * @param header
     * @return
     */
    private int viewComp(String[] cookie, Headers header) {
    	SQLiteConnection db;
    	int cid = -1;
        List<String> values = new ArrayList<String>();
		SQLiteStatement st = null;
    	db = openDatabase(path);
    	if (db != null) {
    		// Success
    		
    		for (String cook : cookie) {
    			if (cook.contains(this.cid)) {
    				cid = Integer.parseInt(cook.split("=")[1]);
    			}
    			if (cid >= 0)
    				break;
    		}
    		if (cid < 0) {
    			System.err.println("Found no company.");
    			closeDatabase(db);
    			return -1;
    		}
    		try {
	    		st = db.prepare("SELECT cname, type, info FROM company WHERE "+this.cid+" = ?");
	    		st.bind(1, cid);
	    		if (st.step()) {
	    			// A row was found, successful login.
	    			values.add(this.cname + "=" + st.columnString(0));
	                values.add(this.type + "=" + st.columnString(1));
	                values.add(this.info + "=" + st.columnString(2));
	                header.put("Set-Cookie", values);
	    		}

    			closeDatabase(db);
    			return 0;
    		} catch (Exception e) {
    			System.err.println("Failed reading database");
    			closeDatabase(db);
    			return -1;
    		}
    		
    	} else {
    		System.err.println("Failed to open database");
    		return -1;
    	}
    }
    
    private int bookmark(String[] cookie, Headers header) {
    	SQLiteConnection db;
    	int uid = -1, cid = -1;
    	String uname = "", prefix = "";
    	StringBuilder sb = new StringBuilder();
        List<String> values = new ArrayList<String>();
		SQLiteStatement st = null;
    	db = openDatabase(path);
    	if (db != null) {
    		// Success
    		
    		for (String cook : cookie) {
    			if (cook.contains(this.uname)) {
    				uname = cook.split("=")[1];
    			}
    			if (!uname.isEmpty())
    				break;
    		}
    		
    		try {
    			st = db.prepare("SELECT uid FROM users WHERE uname = ?");
    			st.bind(1, uname);
    			if (st.step()) {
    				uid = st.columnInt(0);
    				if (uid < 0) {
    					System.err.println("Could not find user id");
    					closeDatabase(db);
    					return -1;
    				}
    			} else {
    				System.err.println("Could not find user.");
    				closeDatabase(db);
    				return -1;
    			}
	    		st = db.prepare("SELECT cname FROM bookmarks WHERE cid = ? ORDER BY cname");
	    		st.bind(1, uid);
	    		while (st.step()) {
	    			// First company will not have the comma, the rest will, but not the last. Perfect.
	    			sb.append(prefix);
	    			prefix = ",";
	    			sb.append(st.columnString(0));
	    		}
                values.add("listcomp="+sb.toString());
                header.put("Set-Cookie", values);
    			
                closeDatabase(db);
    			return 0;
    		} catch (Exception e) {
    			System.err.println("Failed reading database");
    			closeDatabase(db);
    			return -1;
    		}
    		
    	} else {
    		System.err.println("Failed to open database");
    		return -1;
    	}
    }
    
    private int newUser(String[] cookie, Headers header) {
    	SQLiteConnection db;
    	String uname = "", pword = "";
    	db = openDatabase(path);
    	List<String> values = new ArrayList<String>();
		SQLiteStatement st = null;
    	if (db != null) {
    		// Success
    		
    		// Get the values needed from the cookie
    		for (String cook : cookie) {
    			if (cook.contains(this.uname)) {
    				uname = cook.split("=")[1];
    			}
    			if (cook.contains(this.pword)) {
    				pword = cook.split("=")[1];
    			}
    		}
    		
    		try {
    			// TODO: Check username, can duplets be?
    			st = db.prepare("INSERT INTO users (cname, pword) VALUES (?, ?)");
    			st.bind(1, uname);
    			st.bind(2, pword);
    			st.stepThrough();
    			values.add(this.uname+"="+uname);
    			header.put("Set-Cookie", values);
    			closeDatabase(db);
    			return 0;
    			
    		} catch (Exception e) {
    			System.err.println("Could not insert to database");
    			closeDatabase(db);
    			return -1;
    		}
    		
    	} else {
    		System.err.println("Failed to open database");
    		return -1;
    	}
    }
    
    private int newComp(String[] cookie, Headers header) {
    	SQLiteConnection db;
    	db = openDatabase(path);
		SQLiteStatement st = null;
		String cname = "", type = "", info = "";
    	if (db != null) {
    		// Success
    		
    		for (String cook : cookie) {
    			if (cook.contains(this.cname)) {
    				cname = cook.split("=")[1];
    			}
    			if (cook.contains(this.type)) {
    				type = cook.split("=")[1];
    			}
    			if (cook.contains(this.info)) {
    				info = cook.split("=")[1];
    			}
    		}
    		
    		try {
    			st = db.prepare("INSERT INTO company (cname, type, info) VALUES (?, ?, ?)");
    			st.bind(1, cname);
    			st.bind(2, type);
    			st.bind(3, info);
    			st.stepThrough();

    			closeDatabase(db);
    			return 0;
    		} catch (Exception e) {
    			System.err.println("Could not insert new company: " + e);
    			closeDatabase(db);
    			return -1;
    		}
    		
    	} else {
    		System.err.println("Failed to open database");
    		return -1;
    	}
    }
    
    private int removeComp(String[] cookie, Headers header) {
    	SQLiteConnection db;
    	db = openDatabase(path);
    	int cid = -1;
    	List<String> value = new ArrayList<String>();
		SQLiteStatement st = null;
    	if (db != null) {
    		// Success
    		
    		for (String cook : cookie) {
    			if (cook.contains(this.cid)) {
    				cid = Integer.parseInt(cook.split("=")[1]);
    			}
    		}
    		
    		try {
    			st = db.prepare("DELETE FROM company WHERE cid = ?");
    			st.bind(1, cid);
    			st.stepThrough();
    			value.add("cid=");
    			header.put("Set-Cookie", value);
    			closeDatabase(db);
    			return 0;
    		} catch (Exception e) {
    			System.err.println("Could not delete company: " + e);
    			closeDatabase(db);
    			return -1;
    		}
    		
    	} else {
    		System.err.println("Failed to open database");
    		return -1;
    	}
    }
    
    // TODO: Remove companies from bookmarks
    
    // TODO: Filter companies by type

    private class OtherHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
        	t.getRequestBody();
        	System.out.println("t: " + t);
        	Path path = Paths.get("test.html");
        	byte[] data = Files.readAllBytes(path);
        	
            t.sendResponseHeaders(200, data.length);
            OutputStream os = t.getResponseBody();
            String resp = "<html><img src='7.jpg'></html>";
            os.write(resp.getBytes());
            os.close();
        }
    }
    
}