import java.util.Arrays;
import java.util.Iterator;

//import edu.princeton.cs.algs4.StdOut;


public class Board {
	Storage tiles;

	// construct a board from an n-by-n array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
    	if (blocks == null) {
    		throw new NullPointerException();
    	}

    	if (blocks.length < 2) {
    		throw new IllegalArgumentException("Nothing to do");
    	}

    	int n = blocks.length;
    	if (n <= 4) {
    		this.tiles = new LongStorage(n*n);
    	}
    	else if (n <= 16) {
    		this.tiles = new ByteStorage(n*n);
    	}
    	else if (n <= 128) {
    		this.tiles = new CharStorage(n*n);
    	}
    	else {
    		throw new IllegalArgumentException("Board is too large");
    	}

    	for (int i = 0; i < n; i++) {
    		if (blocks[i].length != n) {
    			throw new IllegalArgumentException("Only square boards can be used");
    		}
    		for (int j = 0; j < n; j++) {
    			setTile(i, j, blocks[i][j]);
     		}
    	}
    	computeHamming();
    	computeManhattan();
    }

    // private constructor for creating new boards without the overhead of copying the tiles
    // it is the caller's responsibility to duplicate the tiles during move generation
    private Board(Storage tiles) {
    	this.tiles = tiles;
    }

    private int index(int i, int j) {
    	return i * dimension() + j;
    }

    private int getTile(int i, int j) {
    	return tiles.getTile(index(i, j));
    }

	private void setTile(int i, int j, int k) {
		tiles.setTile(index(i, j), k);
    }

    private boolean isBlank(int i, int j) {
    	return getTile(i, j) == 0;
    }

	// board dimension n
    public int dimension() {
    	return tiles.dimension();
    }

	// number of blocks out of place
	public int hamming() {
    	return tiles.getHamming();
	}

	// number of blocks out of place
	private void computeHamming() {
		int hamming = 0;
		int n = dimension();
    	for (int i = 0; i < n; i++) {
    		for (int j = 0; j < n; j++) {
    			if (!isBlank(i, j) && (getTile(i, j) != (i * n) + (j + 1))) {
    				hamming++;
    			}
     		}
    	}
    	tiles.setHamming(hamming);
	}

	// sum of Manhattan distances between blocks and goal
	public int manhattan() {
    	return tiles.getManhattan();
	}

	// sum of Manhattan distances between blocks and goal
	private void computeManhattan() {
		int manhattan = 0;
		int n = dimension();
    	for (int i = 0; i < n; i++) {
    		for (int j = 0; j < n; j++) {
    			if (!isBlank(i, j)) {
        			int t = getTile(i, j);
        			manhattan += (Math.abs(i - goal_i(t)) + Math.abs(j - goal_j(t)));
    			}
     		}
    	}
    	tiles.setManhattan(manhattan);
	}

	// is this board the goal board?
	public boolean isGoal() {
    	return hamming() == 0;
}

	// a board that is obtained by exchanging any pair of blocks
	public Board twin() {
		Storage copy;
        try {
	        copy = tiles.clone();
        } catch (CloneNotSupportedException e) {
        	throw new RuntimeException(e);
        }
		int first = 0;
		int second = 1;
		// make sure we don't swap the blank tile
		if ((copy.getTile(first) == 0) || (copy.getTile(second) == 0)) {
			first = 2;
			second = 3;
		}
		copy.swap(first, second);
		Board twin = new Board(copy);

		twin.computeHamming();
		twin.computeManhattan();

		return twin;
	}

	public int hashCode() {
		return tiles.hashCode();
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
    	return this.tiles.equals(other.tiles);
	}

	public String toString() {
		// string representation of this board (in the output format specified below)
	    StringBuilder s = new StringBuilder();
	    int n = dimension();
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
	    			int n = dimension();

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
						Storage copy;
                        try {
	                        copy = tiles.clone();
                        } catch (CloneNotSupportedException e) {
                        	throw new RuntimeException(e);
                        }
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
	                    copy.swap(lookup, b);

	                    int t = tiles.getTile(lookup);
//	                    int manhattan = copy.getManhattan();
//	                    int delta = (Math.abs(row - goal_i(t)) + Math.abs(col - goal_j(t)));
//	                    copy.setManhattan(manhattan + delta);

//	                    int hamming = copy.getHamming();
//	                    delta = (t == (row * n) + (col + 1)) ? 1 : -1;
//	                    copy.setHamming(hamming + delta);

	                    // increment index once neighbor is found
                    	index++;
	                    Board board = new Board(copy);
	                    // TODO update these incrementally
	                    board.computeHamming();
	                    board.computeManhattan();
	                    return board;
                    }
	            };
            }
			
		};
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
		int n = dimension();
		return index / n;
	}

	// return the minor index of the goal tile for tile t (1 <= t < n^2)
	private int col(int index) {
		int n = dimension();
		return index % n;
	}

	// return the major/minor index of tile t (0 <= t < n^2)
	private int tileIndex(int t) {
		int n = dimension();
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
		Board board9 = new Board(new int[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}});
		assert (board9.getTile(0, 0) == 1);
		assert (board9.getTile(1, 0) == 4);
		board9.tiles.swap(0, 3);
		assert (board9.getTile(0, 0) == 4);
		assert (board9.getTile(1, 0) == 1);

		Board board16 = new Board(new int[][] {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 0}});
		assert (board16.getTile(0, 0) == 1);
		assert (board16.getTile(1, 0) == 5);
		board16.tiles.swap(0, 4);
		assert (board16.getTile(0, 0) == 5);
		assert (board16.getTile(1, 0) == 1);

		Board board2 = new Board(new int[][] {{1, 2}, {3, 0}});
		assert (board2.getTile(0, 0) == 1);
		assert (board2.getTile(0, 1) == 2);
		Board twin = board2.twin();
		assert (twin.getTile(0, 0) == 2);
		assert (twin.getTile(0, 1) == 1);
	}

	private interface Storage extends Cloneable {
		int getManhattan();
		int getHamming();
		void setManhattan(int value);
		void setHamming(int value);

		int dimension();
		Storage clone() throws CloneNotSupportedException;
		void swap(int a, int b);
		int getTile(int index);		// one-dimensional version of the above
		void setTile(int index, int value);
	}

	/*
	 * Use this class if the entire board can fit in a 64-bit long (i.e. N <= 4)
	 */
	private class LongStorage implements Storage {

		long bits = 0;
		byte n;
		byte hamming;
		byte manhattan;

		private LongStorage(int size) {
			if (size > 16) {
				throw new IllegalArgumentException("Tiles cannot fit in a 64-bit long");
			}
			this.n = (byte) Math.ceil(Math.sqrt(size));
		}

		@Override
		public LongStorage clone() throws CloneNotSupportedException {
			LongStorage copy = new LongStorage(n*n);
			copy.bits = this.bits;
			copy.n = this.n;
			copy.manhattan = this.manhattan;
			copy.hamming = this.hamming;
			return copy;
		}

		@Override
		public int hashCode() {
			return Long.hashCode(this.bits);
		}

		@Override
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
			LongStorage other = (LongStorage) object;
	    	return this.bits == other.bits;
		}

		@Override
        public void swap(int a, int b) {
//	        StdOut.printf("swap(%d, %d) before: a=%d, b=%d, bits=%s\n", a, b, getTile(a), getTile(b), Long.toBinaryString(bits));
			int t = getTile(a);
			setTile(a, getTile(b));
//	        StdOut.printf("swap(%d, %d) after setTile(a): a=%d, b=%d, bits=%s\n", a, b, getTile(a), getTile(b), Long.toBinaryString(bits));
			setTile(b, t);
//	        StdOut.printf("swap(%d, %d) after setTile(b): a=%d, b=%d, bits=%s\n", a, b, getTile(a), getTile(b), Long.toBinaryString(bits));
        }

		@Override
        public int getTile(int index) {
			// 4 bits per tile value
	        return (int) (bits >> (index * 4)) & 0x0F;
        }

        public void setTile(int index, int value) {
        	assert (value <= 16);
        	// clear the slot for the value
        	bits &= (~(0x0FL << (index * 4)));
        	// "or" in the bits for the value
	        bits |= ((long) value << (index * 4));
        }

		@Override
        public int dimension() {
	        return n;
        }

		@Override
        public int getManhattan() {
	        return manhattan;
        }

		@Override
        public int getHamming() {
	        return hamming;
        }

		@Override
        public void setManhattan(int value) {
			assert (value <= Byte.MAX_VALUE);
			hamming = (byte) value;
        }

		@Override
        public void setHamming(int value) {
			assert (value <= Byte.MAX_VALUE);
			hamming = (byte) value;
        }
	}

	private class ByteStorage implements Storage {

		byte[] bytes;
		byte hamming;
		byte manhattan;
		byte n;

		private ByteStorage(int size) {
			this.bytes = new byte[size];
			this.n = (byte) Math.ceil(Math.sqrt(size));			
		}

		@Override
		public ByteStorage clone() throws CloneNotSupportedException {
			ByteStorage copy = new ByteStorage(bytes.length);
			copy.bytes = this.bytes.clone();
			copy.manhattan = this.manhattan;
			copy.hamming = this.hamming;
			return copy;
		}

		@Override
		public int hashCode() {
			return this.bytes.hashCode();
		}

		@Override
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
			ByteStorage other = (ByteStorage) object;
			return Arrays.equals(bytes, other.bytes);
		}

		@Override
        public void swap(int a, int b) {
			byte t = bytes[a];
			bytes[a] = bytes[b];
			bytes[b] = t;
        }

		@Override
        public int getTile(int index) {
	        return bytes[index];
        }

		@Override
        public void setTile(int index, int k) {
			bytes[index] = (byte) k;
        }

		@Override
        public int dimension() {
	        return n;
        }

		@Override
        public int getManhattan() {
	        return manhattan;
        }

		@Override
        public int getHamming() {
	        return hamming;
        }

		@Override
        public void setManhattan(int value) {
			assert (value <= Byte.MAX_VALUE);
			manhattan = (byte) value;
        }

		@Override
        public void setHamming(int value) {
			assert (value <= Byte.MAX_VALUE);
			hamming = (byte) value;
        }
	}

	private class CharStorage implements Storage {

		char[] bytes;
		char hamming;
		char manhattan;
		char n;

		private CharStorage(int size) {
			this.bytes = new char[size];
			this.n = (char) Math.ceil(Math.sqrt(size));
		}

		@Override
		public CharStorage clone() throws CloneNotSupportedException {
			CharStorage copy = new CharStorage(bytes.length);
			copy.bytes = this.bytes.clone();
			copy.manhattan = this.manhattan;
			copy.hamming = this.hamming;
			return copy;
		}

		@Override
		public int hashCode() {
			return this.bytes.hashCode();
		}

		@Override
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
			CharStorage other = (CharStorage) object;
			return Arrays.equals(bytes, other.bytes);
		}

		@Override
        public void swap(int a, int b) {
			char t = bytes[a];
			bytes[a] = bytes[b];
			bytes[b] = t;
        }

		@Override
        public int getTile(int index) {
	        return bytes[index];
        }

		@Override
        public void setTile(int index, int k) {
			bytes[index] = (char) k;
        }

		@Override
        public int dimension() {
	        return n;
        }

		@Override
        public int getManhattan() {
	        return manhattan;
        }

		@Override
        public int getHamming() {
	        return hamming;
        }

		@Override
        public void setManhattan(int value) {
			assert (value <= Character.MAX_VALUE);
			manhattan = (char) value;
        }

		@Override
        public void setHamming(int value) {
			assert (value <= Character.MAX_VALUE);
			hamming = (char) value;
        }
	}
}
