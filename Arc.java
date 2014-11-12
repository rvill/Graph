
@SuppressWarnings("unchecked")
public class Arc implements Comparable {
	int origin;		// Package access
	int dest;		// Package access
	int cost;		// Package access
	public Arc() {}
	public Arc(int o, int d, int c) {
		origin= o;
		dest= d;
		cost= c;
	}
/*    public int compareTo(Object other) {
    	Arc o= (Arc) other;
        if (origin < o.origin  || (origin == o.origin && dest < o.dest))
            return -1;
        else if (origin > o.origin || (origin == o.origin && dest > o.dest))
            return 1;
        else
            return 0;
    } */
	 public int compareTo(Object other) {
	    	Arc o= (Arc) other;
	        if (dest < o.dest  || (dest == o.dest && origin < o.origin))
	            return -1;
	        else if (dest > o.dest || (dest == o.dest && origin > o.origin))
	            return 1;
	        else
	            return 0;
	    } 
    
        public String toString() {
    	return (origin+" "+dest+" "+cost);
    }
}
