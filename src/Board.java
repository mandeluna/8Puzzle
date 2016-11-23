import java.util.Iterator;

import edu.princeton.cs.algs4.StdOut;


public class Board {
	int n; // board is n x n tiles

	// sentinels for caching calculations
	int manhattan = -1;
	int hamming = -1;

	// 16-bit values use 1/2 as much memory as ints
	char[] tiles;

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
    	this.tiles = new char[n*n];
    	int bits = (int) Math.ceil(Math.log(tiles.length) / Math.log(2));
//    	StdOut.printf("%d tiles require %d bits per tile\n", tiles.length, bits);

    	for (int i = 0; i < n; i++) {
    		if (blocks[i].length != n) {
    			throw new IllegalArgumentException("Only square boards can be used");
    		}
    		for (int j = 0; j < n; j++) {
    			setTile(i, j, blocks[i][j]);
     		}
    	}
    }

    // private constructor for creating new boards without the overhead of copying the tiles
    // it is the caller's responsibility to duplicate the tiles during move generation
    private Board(char[] tiles) {
    	this.n = (int) Math.sqrt(tiles.length);
    	assert (n * n == tiles.length);
    	this.tiles = tiles;
    }

    private int index(int i, int j) {
    	return i * n + j;
    }

    private int getTile(int i, int j) {
    	return tiles[index(i, j)];
    }

	private void setTile(int i, int j, int k) {
		assert (k >= 0) && (k < n*n);
		tiles[index(i, j)] = (char) k;
    }

    private boolean isBlank(int i, int j) {
    	return getTile(i, j) == 0;
    }

	// board dimension n
    public int dimension() {
    	return n;
    }

	// number of blocks out of place
	public int hamming() {
		if (hamming >= 0) {
			return hamming;
		}
		hamming = 0;
    	for (int i = 0; i < n; i++) {
    		for (int j = 0; j < n; j++) {
    			if (!isBlank(i, j) && (getTile(i, j) != (i * n) + (j + 1))) {
    				hamming++;
    			}
     		}
    	}
    	return hamming;
	}

	// sum of Manhattan distances between blocks and goal
	public int manhattan() {
		if (manhattan >= 0) {
			return manhattan;
		}
		manhattan = 0;
    	for (int i = 0; i < n; i++) {
    		for (int j = 0; j < n; j++) {
    			if (!isBlank(i, j)) {
        			int t = getTile(i, j);
        			manhattan += (Math.abs(i - goal_i(t)) + Math.abs(j - goal_j(t)));
    			}
     		}
    	}
    	return manhattan;
	}

	// is this board the goal board?
	public boolean isGoal() {
    	return hamming() == 0;
}

	// a board that is obtained by exchanging any pair of blocks
	public Board twin() {
		char[] copy = this.tiles.clone();
		int first = 0;
		int second = 1;
		// make sure we don't swap the blank tile
		if ((copy[first] == 0) || (copy[second] == 0)) {
			first = 2;
			second = 3;
		}
		swap(copy, first, second);
		Board twin = new Board(copy);
		return twin;
	}

	@Override
	// TODO remove this method - not allowed for this exercise
	public int hashCode() {
		int sum = 0;
		for (int i = 0; i < n*n; i++) {
			sum += tiles[i] << i;
		}
		return sum;
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
    			 if (other.getTile(i, j) != this.getTile(i, j)) {
    				 return false;
    			 }
     		}
    	}
    	return true;
	}

	public String toString() {
		// string representation of this board (in the output format specified below)
	    StringBuilder s = new StringBuilder();
	    s.append(n + "\n");
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++) {
	            s.append(String.format("%2d ", getTile(i, j)));
	        }
	        s.append("\n");
	    }
	    return s.toString();
	}

	// all neighboring boards
	public Iterable<Board> neighbors() {
		return new Iterable<Board>() {

			@Override
            public Iterator<Board> iterator() {
	            return new Iterator<Board>() {

	    			int b = tileIndex(0);	// index of blank tile
	    			int index = 0;			// external index of valid neighbors
	    			int currIndex = 0;		// internal index of all neighbors (including invalid)
	    			int[][] nij = new int[4][2];
	    			int num_neighbors = 0;

	    			{
	    				// clockwise order: N -> E -> S -> W
	    				final int[][] dij = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

	    				for (int i = 0; i < dij.length; i++) {
	    					nij[i][0] = row(b) + dij[i][0];
	    					nij[i][1] = col(b) + dij[i][1];
	    					if ((nij[i][0] >= 0) && (nij[i][0] < n) && (nij[i][1] >= 0) && (nij[i][1] < n)) {
	    						num_neighbors++;
	    					}
	    					else {
	    						// sentinel to indicate invalid index
	    						nij[i][0] = nij[i][1] = -1;
	    					}
	    				}
	    			}

					@Override
                    public boolean hasNext() {
	                    return index < num_neighbors;
                    }

					@Override
                    public Board next() {
						// create new board to play with
						char[] copy = tiles.clone();
						// swap blank tile with neighboring tile
						int row = nij[currIndex][0];
						int col = nij[currIndex][1];
						currIndex++;
						// skip sentinel values in indexes
						while ((row < 0) || (col < 0)) {
							row = nij[currIndex][0];
							col = nij[currIndex][1];
							currIndex++;
	                    };
						int lookup = index(row, col);
	                    swap(copy, lookup, b);
	                    // increment index once neighbor is found
                    	index++;
	                    return new Board(copy);
                    }
	            };
            }
			
		};
	}

	private void swap(char[] copy, int a, int b) {
		char t = copy[a];
		copy[a] = copy[b];
		copy[b] = t;
    }

	// return the major index of the goal tile for tile t (1 <= t < n^2)
	private int goal_i(int t) {
		if (t == 0) {
			throw new IllegalArgumentException("Attempt to find goal of blank tile");
		}
		return row(t - 1);
	}

	// return the minor index of the goal tile for tile t (1 <= t < n^2)
	private int goal_j(int t) {
		if (t == 0) {
			throw new IllegalArgumentException("Attempt to find goal of blank tile");
		}
		return col(t - 1);
	}

	// return the major index of the goal tile for tile t (1 <= t < n^2)
	private int row(int index) {
		if ((index < 0) || (index > n*n)) {
			throw new IndexOutOfBoundsException("Index not on board: " + index);
		}
		return index / n;
	}

	// return the minor index of the goal tile for tile t (1 <= t < n^2)
	private int col(int index) {
		if ((index < 0) || (index > n*n)) {
			throw new IndexOutOfBoundsException("Index not on board: " + index);
		}
		return index % n;
	}

	// return the major/minor index of tile t (0 <= t < n^2)
	private int tileIndex(int t) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (getTile(i, j) == t) {
					return index(i, j);
				}
			}
		}
		throw new IllegalArgumentException("Tile is not on board: " + t);
	}

	// unit tests (not graded)
	public static void main(String[] args) {
	}
}
