import java.util.Iterator;


public class Board {
	int n; // board is n x n tiles
	int tiles[][];

	// construct a board from an n-by-n array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
    	if (blocks == null) {
    		throw new NullPointerException();
    	}

    	if (blocks.length < 2) {
    		throw new IllegalArgumentException("Nothing to do");
    	}

    	this.n = blocks.length;

    	this.tiles = new int[n][];
    	for (int i = 0; i < n; i++) {
    		if (blocks[i].length != n) {
    			throw new IllegalArgumentException("Only square boards can be used");
    		}
    		this.tiles[i] = new int[n];
    		for (int j = 0; j < n; j++) {
    			this.tiles[i][j] = blocks[i][j];
     		}
    	}
    }

	// board dimension n
    public int dimension() {
    	return n;
    }

    private boolean isBlank(int i, int j) {
    	return (this.tiles[i][j] == 0);
    }

	// number of blocks out of place
	public int hamming() {
		int count = 0;
    	for (int i = 0; i < n; i++) {
    		for (int j = 0; j < n; j++) {
    			if (!isBlank(i, j) && (this.tiles[i][j] != (i * n) + (j + 1))) {
    				count++;
    			}
     		}
    	}
    	return count;
	}

	// return the major index of the goal tile for tile t (1 <= t < n^2)
	private int goal_i(int t) {
		if (t == 0) {
			throw new IllegalArgumentException("Attempt to find goal of blank tile");
		}
		return (t - 1) / n;
	}

	// return the minor index of the goal tile for tile t (1 <= t < n^2)
	private int goal_j(int t) {
		if (t == 0) {
			throw new IllegalArgumentException("Attempt to find goal of blank tile");
		}
		return (t - 1) % n;
	}

	// return the major/minor index of tile t (0 <= t < n^2)
	private int[] index(int t) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (this.tiles[i][j] == t) {
					return new int[]{i, j};
				}
			}
		}
		throw new IllegalArgumentException("Tile is not on board: " + t);
	}

	// sum of Manhattan distances between blocks and goal
	public int manhattan() {
		int count = 0;
    	for (int i = 0; i < n; i++) {
    		for (int j = 0; j < n; j++) {
    			if (!isBlank(i, j)) {
        			int t = this.tiles[i][j];
        			count += (Math.abs(i - goal_i(t)) + Math.abs(j - goal_j(t)));
    			}
     		}
    	}
    	return count;
	}

	// is this board the goal board?
	public boolean isGoal() {
    	for (int i = 0; i < n; i++) {
    		for (int j = 0; j < n; j++) {
    			 if (!isBlank(i, j) && (this.tiles[i][j] != (i * n) + (j + 1))) {
    				 return false;
    			 }
     		}
    	}
    	return true;
}

	// a board that is obtained by exchanging any pair of blocks
	public Board twin() {
		// TODO implement this method
		return new Board(this.tiles);
	}

	// does this board equal y?
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object.getClass() != this.getClass()) {
			return false;
		}
		Board other = (Board) object;
    	for (int i = 0; i < n; i++) {
    		for (int j = 0; j < n; j++) {
    			 if (other.tiles[i][j] != this.tiles[i][j]) {
    				 return false;
    			 }
     		}
    	}
    	return true;
	}

	// all neighboring boards
	public Iterable<Board> neighbors() {
		return new Iterable<Board>() {

			int[] b = index(0);	// index of blank tile
			int index = 0;
			int[][] nij = new int[4][2];
			int num_neighbors = 0;

			{
				final int[][] dij = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

				for (int i = 0; i < dij.length; i++) {
					nij[i][0] = b[0] + dij[i][0];
					nij[i][1] = b[1] + dij[i][1];
					if ((nij[i][0] > 0) && (nij[i][0] < n) && (nij[i][1] > 0) && (nij[i][1] < n)) {
						num_neighbors++;
					}
					else {
						// sentinel to indicate invalid index
						nij[i][0] = nij[i][1] = -1;
					}
				}
			}

			@Override
            public Iterator<Board> iterator() {
	            return new Iterator<Board>() {

					@Override
                    public boolean hasNext() {
	                    return index < num_neighbors;
                    }

					@Override
                    public Board next() {
						// swap blank tile with neighboring tile
	                    int[] candidate = nij[index];
                    }
	            	
	            };
            }
			
		};
	}

	public String toString() {
		// string representation of this board (in the output format specified below)
	    StringBuilder s = new StringBuilder();
	    s.append(n + "\n");
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++) {
	            s.append(String.format("%2d ", tiles[i][j]));
	        }
	        s.append("\n");
	    }
	    return s.toString();	}
	
	// unit tests (not graded)
	public static void main(String[] args) {
	}
}
