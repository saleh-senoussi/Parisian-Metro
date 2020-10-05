package net.datastructures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import net.datastructures.AdjacencyMapGraph;
//import net.datastructures.Dijkstra;
import net.datastructures.Edge;
import net.datastructures.Graph;
import net.datastructures.GraphAlgorithms;
import net.datastructures.Map;
import net.datastructures.Vertex;

import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
/*
 * Metro class does three applications based on user input with function call
 * 
 * @author Saleh Bichara Senoussi
 * @author Mignon Koma
 */
public class Metro {
	
	private Graph <String, Integer>sGraph;
	Object METRO = new Object();
	private Hashtable <String, Boolean>visited;
	private ArrayStack<Vertex<String>> stack;
	private ArrayQueue<Vertex<String>> queue;
	private String [] list;
	private int numVertex;
	private int numEdges;
	
	/**
	 * Create a Metro graph from file
	 */
	public Metro(String fileName) throws Exception, IOException {
		sGraph = new AdjacencyMapGraph<String, Integer>(false);
		readMetro(fileName);
	}
	
	/**
	 * Read a list of edges from file
	 */
	protected void readMetro(String fileName) throws Exception, IOException {
		BufferedReader graphFile = new BufferedReader(new FileReader(fileName));
		
		// Create a hash map to store all the vertices read
		Hashtable<String, Vertex> vertices = new Hashtable<String, Vertex>();
		
		// Read the edges and insert
		String line = graphFile.readLine();
		StringTokenizer st = new StringTokenizer(line);
		if (st.countTokens() != 2)
			throw new IOException("Incorrect input file at line " + line);
		else{
			numEdges = 933;
			numVertex = 376;
			String [] list1 = new String [numVertex];
			list = list1;
			int j = numVertex;
			while ((line = graphFile.readLine()) != "$" && j >0){// Stores the stations name in an array of String
				st = new StringTokenizer(line);
				Integer key = new Integer(st.nextToken());
				StringBuffer v = new StringBuffer(st.nextToken());
				int k = st.countTokens();
				while (k>0){
					v.append(st.nextToken() + " ");
					k--;
				}
				list[key] = v.toString();
				j--;
			}
		}
		
		st = new StringTokenizer(line);
		
		while ((line = graphFile.readLine()) != null) {//Crée le graphe
			
			st = new StringTokenizer(line);
			
			if (st.countTokens() != 3)
				throw new IOException("Incorrect input file at line " + line);
			
			String source = st.nextToken();
			String dest = st.nextToken();
			Integer weight = new Integer(st.nextToken());
			
			Vertex<String> sv = vertices.get(source);
			if (sv == null) {
				// Source vertex not in graph -- insert
				sv = sGraph.insertVertex(source);
				vertices.put(source, sv);
			}
			Vertex<String> dv = vertices.get(dest);
			if (dv == null) {
				// Destination vertex not in graph -- insert
				dv = sGraph.insertVertex(dest);
				vertices.put(dest, dv);
			}
			
			if (weight ==-1){
				weight = 90;
			}
			// check if edge is already in graph
			if (sGraph.getEdge(sv, dv) == null) {
				// edge not in graph -- add
				//e's element is now the distance between the vertices
				Edge<Integer> e = sGraph.insertEdge(sv, dv, weight);
			}
		}
	}
	
	
	/**
	 * Helper routine to get a Vertex (Position) from a string naming the vertex
	 * @exception throws Exception
	 */
	protected Vertex<String> getVertex(String vert) throws Exception {
		// Go through vertex list to find vertex
		for (Vertex<String> vs : sGraph.vertices()) {
			if (vs.getElement().equals(vert)) {
				return vs;
			}
		}
		throw new Exception("Vertex not in graph: " + vert);
	}
	
	/*
	 * Call a recursive function that prints all the station that are in the same line as an specified one
	 */
	public void printStationsSameLine( String vert )  throws Exception{
		Vertex <String>    vt = getVertex ( vert );
		visited = new Hashtable <>();
		stationsSameLine ( this.sGraph, vt, 0);
		return;
	} /* printDFS */
	
	/*
	 * Prints all the station that are in the same line as an specified one
	 * These recursive function is used by 2 methods: printStationsSameLine and printPathWithLineOutOfOrder
	 */
	private void stationsSameLine( Graph <String, Integer>graph, Vertex <String>v, int k){
		
		if( visited.get ( v.getElement() ) != null ) return; //Si l'élément a déjà été visité, on fait rien
		visited.put ( v.getElement(), Boolean.TRUE );	//Autrement, on marque que c'est visité
		if (k==0){// printStationsSameLine's task is executed
			visit(v);
		}
		if (k==1){//printPathWithLineOutOfOrder's task is executed
			queue.enqueue(v);
		}
		for( Edge <Integer>e : graph.outgoingEdges ( v ) ) {
			if (e.getElement()!=90){ 	//Si le temps de transfert est de 90, ça veut dire qu'il y a un transfert 
										//entre les lignes, et cette condition permet d'éviter célà
				Vertex <String>    s = graph.opposite ( v, e );
				stationsSameLine (graph, s, k);
			}
		}
		return;
	} /* DFS */

	/*
	 * Imprime le numero de la station et son nom
	 */
	private void visit( Vertex <String>v ) {
		System.out.println ( v.getElement() + ":"+ list[Integer.parseInt(v.getElement())] );
	} /* visit */
	
	
	/**
	 * Calls a recursive function that prints the shortest distances between two stations
	 */
	
	protected void printShortestPath(String source, String goal) throws Exception{
		Vertex <String>    vSource = getVertex ( source );
		Vertex <String>    vGoal = getVertex ( goal );
		if(vSource == null ||vGoal == null)
			return;
		GraphAlgorithms    dj = new GraphAlgorithms();
		Map <Vertex <String>, Integer>    result = dj.shortestPathLengths ( sGraph, vSource );
		shortestPath(result, vGoal, vSource);
		visit(vGoal);
	}
	
	
	/**
	 * Print the shortest distances between two stations
	 * This function is used by printShortestPath and printPathWithLineOutOfOrder
	 * @exception throws Exception
	 */
	private void shortestPath(Map <Vertex <String>, Integer> result,Vertex<String> v, Vertex<String>vSource){
		
		// Print shortest path to named station
		int min = result.get ( v );
		
		if (queue!=null){//printPathWithLineOutOfOrder
			stack.push(v);
		}
		if (min !=0 && v!= vSource){
			for( Edge <Integer>e : sGraph.incomingEdges(v) ) {
				try{
					Vertex<String> temp = sGraph.opposite(v,e) ;
					if (queue ==null){////printShortestPath
						if (result.get(temp)<min){
							min = result.get(temp);
							v = temp;
						}
					}
					else{//printPathWithLineOutOfOrder
						if (result.get(temp)<min && !findElement( queue,temp)){
							min = result.get(temp);
							v = temp;
						}
					}
				} catch(IllegalArgumentException except){
				}
			}
			shortestPath(result,v, vSource);
		}
		else
			return;
		if (visited==null){//printShortestPath
			visit(v);
		}
		return;
	} /* printShortestPath */
	
	
	/*
	 * Print a the shortest path with a line out of order
	 */
	protected void printPathWithLineOutOfOrder(String source, String dest, String out) throws Exception{
		Vertex <String>    vSource = getVertex ( source );
		Vertex <String>    vGoal = getVertex ( dest );
		Vertex <String>    vOut = getVertex ( out );
		visited = new Hashtable <>();
		queue = new ArrayQueue<>();
		stationsSameLine(sGraph, vOut, 1);
		int temp = 0;
		if(findElement(queue, vSource)){ // vérifie si la station du départ est sur la ligne qui est hors fonction
			for (Edge <Integer>e:sGraph.outgoingEdges(vSource)){//vérifie s'il y a moyen de passer par une autre ligne dans ce cas
				if (e.getElement()==90)
					temp = 90;
			}
			if (temp==0)
				System.out.println("The line is out of order");
			return;
		}
		temp = 0;
		if (findElement(queue, vGoal)){//vérifie si la station d'arrivée est sur la ligne qui est hors fonction
			for (Edge <Integer>e:sGraph.outgoingEdges(vSource)){//vérifie s'il y a moyen de passer par une autre ligne dans ce cas
				if (e.getElement()==90)
					temp = 90;
			}
			if (temp==0)
				System.out.println("The line is out of order");
			return;
		}
		
		GraphAlgorithms    dj = new GraphAlgorithms();
		Map <Vertex <String>, Integer>    result = dj.shortestPathLengths ( sGraph, vSource );
		stack = new ArrayStack<>();
		shortestPath(result,vGoal, vSource);
		while (!stack.isEmpty()){
			visit(stack.pop());
		}
	}
	
	/*
	 * Checks if a element is in an ArrayQueue
	 */
	private boolean findElement(ArrayQueue<Vertex<String>> Q, Vertex<String> v){
		int size = queue.size();
		Vertex<String> temp;
		boolean found = false;
		while (size> 0 && found == false){
			temp = Q.dequeue();
			if (temp.equals(v)){
				found = true;
			}
			queue.enqueue(temp);
			size--;
		}
		return found;
	}
	
	/** Helper method:
	 * Read a String representing a vertex from the console
	 */
	public static String readVertex() throws IOException {
		System.out.println ("If you wish to know all the stations on the same line, "
				+ "please give one station in that very line.");
		System.out.println ("If you want to know the shortest path between two stations, "
				+ "please provide the source and the destination.");
		System.out.println ("If you want to know the shortest path between two stations and you also know "
				+ "that one line is out of service, "
				+ "please provide the source, the destination and one of the stations of that line.");
		BufferedReader    reader =
			new BufferedReader ( new InputStreamReader ( System.in ) );
		return reader.readLine();
	} /* readVertex */
	
	
	public static void main( String [] argv ) {
		//if( argv.length < 1 ) {
			//System.err.println ( "Usage: java metro fileName" );
			//System.exit ( -1 );
		//}
		try {
			Metro    sGraph = new Metro ( "/Users/salehbicharasenoussi/Documents/Devoir4/src/metro.txt" );
			StringTokenizer    st = new StringTokenizer ( readVertex() );
			while (st.countTokens()==0 || st.countTokens()>3){
				System.out.println ( "Your request is invalid, please enter 1 to 3 number of stations");
				st = new StringTokenizer ( readVertex() );
			}
			if (st.countTokens()==1){
				String val = st.nextToken();
				sGraph.printStationsSameLine(val);
			}
			else if(st.countTokens()==2){
				String source = st.nextToken();
				String goal = st.nextToken();
				sGraph.printShortestPath(source,goal);
			}
			else if(st.countTokens()==3){
				String source = st.nextToken();
				String goal = st.nextToken();
				String blocked = st.nextToken();
				sGraph.printPathWithLineOutOfOrder(source, goal, blocked);
			}
		}
		catch( Exception except ) {
			System.err.println ( except );
			except.printStackTrace();
		}
	} /* main */
}
