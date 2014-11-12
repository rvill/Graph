

import java.util.*;
import java.sql.*;

public class Graph {
	private int from[]; // array for storing the origin nodes of arcs
	private int to[];   // array for storing the destination nodes of arcs
	private int dist[]; // array for storing the length of the arcs
	private int H[]; // array for the forward representation of the network
	private int T[]; // array for the backward representation if the network
	private int index[]; 
	private int nodes; // number of nodes
	private int arcs; // number of arcs
	
	// Graph Constructor
	public Graph() {
		Arc[] graph= null;
		// graph is discarded at end of constructor
		String url= "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=ArlingtonNetworkTiger;"+
			"user=gkocur3;password=car5940;";
		Connection con;
		
		// make a connection to the database
		// as the 1st step, we need to find the number of nodes in the network
		// we find the max value of the node number from the startnode and the endnode columns
		// the highestnode = max(max(startnode, endnode)) gives the highest node number
		//  since node numbers start from 0, number of nodes = highestnode + 1 
		
		String query= "select max(StartNode) as node from ArlingtonNetwork";
		Statement stmt;

		try {
			Class.forName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException");
			e.printStackTrace();
		}
		
		try {
			con = DriverManager.getConnection(url);
			System.out.println("Connected");		// Debug statement
			stmt= con.createStatement();
	        ResultSet rs = stmt.executeQuery(query);
	        if(rs.next())
	        	nodes = rs.getInt(1);
	        stmt.close();
	        
	        stmt = con.createStatement();	         
	        query = "Select max(EndNode) as node from ArlingtonNetwork";
	        
	        rs = stmt.executeQuery(query);
	        if(rs.next())
	        {
	        	int tmp = rs.getInt("node");
	        	if(tmp > nodes)
	        		nodes = tmp;
	        }	
	        nodes++;
	        System.out.println("Number of nodes: " + nodes);
	        
	        //after finding the number of nodes, we find the number of arcs in the network
	        // the count(id) gives the number of rows in the arlingtonnetwork table,
	        // which gives the number of arcs in the network;
	        
	        stmt = con.createStatement();
	        query = "Select count(id) as numarcs from ArlingtonNetwork";
	        rs = stmt.executeQuery(query); 
	        if (rs.next())
	             arcs = rs.getInt("numarcs");
	        System.out.println("Number of arcs: " + arcs);
	        
	        // now we know the number of arcs and nodes in the network, we create
	        // the different arrays needed for the forward and the reverse star representation
	        
	        graph= new Arc[arcs];
			from = new int[arcs];
			to = new int[arcs];
			dist = new int[arcs];

			// to create the forward star, we need to sort the arcs based on the
			// start node. we can do this with the aid of sql's order by clause
			// we also populate the graph array as it will be useful in the later
			// stages for sorting
			
			stmt = con.createStatement();
			query = "Select StartNode, EndNode, Distance from ArlingtonNetwork order by StartNode";
	        rs = stmt.executeQuery(query);
	        int temp= 0;
	        while(rs.next()) {
	           from[temp] = rs.getInt("StartNode");
	           to[temp] = rs.getInt("EndNode");
	           dist[temp] = rs.getInt("Distance");
	           graph[temp] = new Arc(from[temp], to[temp], dist[temp]);
	           temp++;
	        }	
	        
	        // we got everything we wanted from the database, close the connections
	        stmt.close();
	        con.close();
		}
		catch( SQLException ex ) {
			ex.printStackTrace();
		}      
		// Create array H from the array of Arcs. Length= nodes+1 (sentinel)
		
		H= new int[nodes+1];
		int prevOrigin= -1;
		for (int i=0; i < arcs; i++) {
			int o= graph[i].origin;
			if (o != prevOrigin) {
				for (int j= prevOrigin+1; j < o; j++)
					H[j]= i;	// Nodes with no arcs out
				H[o]= i;
				prevOrigin= o;
			}
		}
		for (int i= nodes; i > prevOrigin; i--) 	// Sentinel, and nodes before it with no arcs out.
			H[i]= arcs;

		// create a copy of the array graph, called graphcopy
		// we will use this array to build the T array and the index array
		// sort on the basis of destination node numbers 
		// the arc.java file defines rules to sort the array based on the destination node
		
		Arc[] graphcopy = new Arc[arcs];
		System.arraycopy(graph, 0, graphcopy, 0, arcs);
		Arrays.sort(graphcopy);
		
		// Create the T array. Logic identical to creating H array
		T= new int[nodes+1];
		int prevDest= -1;
		for (int i=0; i < arcs; i++) {
			int d= graphcopy[i].dest;
			if (d != prevDest) {
				for (int j= prevDest+1; j < d; j++)
					T[j]= i;	// Nodes with no arcs in
				T[d]= i;
				prevDest= d;
			}
		}
		for (int i= nodes; i > prevDest; i--) 	// Sentinel, and nodes before it with no arcs in.
			T[i]= arcs;
		
		// the index array maps the arc numbers from the T representation to the H 
		// representation, index[arc-number-in-T-representation] = arc-number-in-H-representation
		// we first select the arc number 0 in the T-representation and search for the 
		 // corresponding arc in the H-representation. 
		// we get the origin of arc 0 from the graphcopy array
		 // we then search for the potential destinations using the H[] and the to[] 
		// once the destination is found, we fill index[0] with the corresponding arc number
		// we then do the same for the remaining arcs.
		
		index = new int[arcs];
		for (int i = 0; i < arcs; i++) {
			for (int m = H[graphcopy[i].origin]; m < H[graphcopy[i].origin + 1]; m++) {
				if(to[m] == graphcopy[i].dest) {
					index[i] =m;
					break;
				} 
			}
		}
	}
	
	// traversing the graph based on the origin node
	public void traverse() {
		for (int node= 0; node < nodes; node++) 
			for (int arc= H[node]; arc < H[node+1]; arc++) 
				System.out.println("("+ node +
						", "+ to[arc]+", "+ dist[arc]+")");
	}
	
	// traversing the graph based on the destination node
	public void reverseTraverse() {
		for (int node= 0; node < nodes; node++)
			for (int arc= T[node]; arc < T[node+1]; arc++)
				System.out.println("("+ node +
						", "+ from[index[arc]]+", "+ dist[index[arc]]+")");
	}
    
    public static void main(String[] args) {
    	Graph g= new Graph();
    	System.out.println("\nTraverse (from, to, dist)");
    	g.traverse();
    	System.out.println("\nReverse traverse (to, from, dist)");
    	g.reverseTraverse();
    }
}
