import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;


public class Solver {

	boolean solved = false;
	int moves = -1;
	private MinPQ<SearchNode> queue = new MinPQ<>(new MoveComparator());
	private MinPQ<SearchNode> queue2 = new MinPQ<>(new MoveComparator());
    private Set<Board> visited = new HashSet<>();
    private Set<Board> visited2 = new HashSet<>();
//	private List<Board> visited = new ArrayList<>();
//	private List<Board> visited2 = new ArrayList<>();
//	Board lastVisited = null;
//    Board lastVisited2 = null;

	// find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
    	if (initial == null) {
    		throw new NullPointerException();
    	}

    	queue.insert(new SearchNode(initial, null, 0));
    	queue2.insert(new SearchNode(initial.twin(), null, 0));

		while (!queue.isEmpty()) {

//		    printQueue(queue);

	    	SearchNode node = queue.min();
	    	Board curr = node.board;
	    	SearchNode node2 = queue2.min();
	    	Board twin = node2.board;
	    	// number of moves will be the same for both trees
	    	moves = node.moves;

    		if (curr.isGoal()) {
    			solved = true;
    			break;
    		}
    		if (twin.isGoal()) {
    			solved = false;
    			break;
    		}
    		queue.delMin();
    		visited(curr);
    		visited2(twin);
    		for (Board neighbor : curr.neighbors()) {
    			if (!isVisited(neighbor)) {
    				queue.insert(new SearchNode(neighbor, node, moves + 1));
    			}
    		}
    		for (Board neighbor : twin.neighbors()) {
    			if (!isVisited2(neighbor)) {
    				queue2.insert(new SearchNode(neighbor, node2, moves + 1));
    			}
    		}
		}
    }

    private void printQueue(MinPQ<SearchNode> queue) {
        List<String> rows = new ArrayList<>();

        int col = 1;
        for (SearchNode node : queue) {
            int colwidth = node.board.dimension() * 4 + 5;
            String[] recs = node.toString().split("\n");
            for (int i = 0; i < recs.length; i++) {
                String row = (rows.size() <= i) ? recs[i] : rows.get(i) + recs[i];
                String element = row;
                for (int c = 0; c < col * colwidth - row.length(); c++) {
                    element += ' ';
                }
                if (rows.size() <= i) {
                    rows.add(element);
                }
                else {
                    rows.set(i, element);
                }
            }
            col++;
        }
        for (String row : rows) {
            StdOut.println(row);
        }
        StdOut.println();
    }

    private boolean isVisited2(Board board) {
    	return visited2.contains(board);
//    	return lastVisited2.equals(board);
    }

    private void visited2(Board board) {
		visited2.add(board);
//		lastVisited2 = board;
    }

    private boolean isVisited(Board board) {
    	return visited.contains(board);
//    	return lastVisited.equals(board);
    }

    private void visited(Board board) {
		visited.add(board);
//		lastVisited = board;
    }

    // is the initial board solvable?
    public boolean isSolvable() {
    	return solved;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
    	return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
    	if (queue.isEmpty()) {
    		return null;
    	}

    	Stack<Board> solution = new Stack<>();
		SearchNode node = queue.min();

		do {
			solution.push(node.board);
			node = node.previous;
		} while (node != null);

    	return solution;
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        long now = System.currentTimeMillis();
        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
        StdOut.printf("Execution time %d milliseconds\n", System.currentTimeMillis() - now);
    }

    private class SearchNode {
    	private Board board;
    	private SearchNode previous;
    	private int moves;

    	private SearchNode(Board board, SearchNode previous, int moves) {
    		this.board = board;
    		this.previous = previous;
    		this.moves = moves;
    	}

    	private int priority() {
            return board.manhattan() + moves;
    	}

    	public String toString() {
            //  priority  = 4
            //  moves     = 0
            //  manhattan = 4
            //  3            
            //   0  1  3     
            //   4  2  5     
            //   7  8  6  

    	    StringBuilder builder = new StringBuilder();
    	    builder.append(String.format("priority  = %d\n", priority()));
            builder.append(String.format("moves     = %d\n", moves));
            builder.append(String.format("manhattan = %d\n", board.manhattan()));
            builder.append(this.board);
            return builder.toString();
    	}
    }

    private class MoveComparator implements Comparator<SearchNode> {
		@Override
        public int compare(SearchNode o1, SearchNode o2) {
		    // are we almost at the goal
            if (o1.board.isGoal()) {
                return -1;
            }
            if (o2.board.isGoal()) {
                return 1;
            }
			if (Math.abs(o1.board.manhattan()) <= 1) {
				return -1;
			}
			if (Math.abs(o2.board.manhattan()) <= 1) {
				return 1;
			}
			int d = o1.board.dimension();
	        int score1 = o1.priority();
	        int score2 = o2.priority();
	        if (score1 == score2) {
	        	score1 = o1.board.hamming() * d + o1.moves;
	        	score2 = o2.board.hamming() * d + o2.moves;
	        }
	        return score1 - score2;
        }
    }
}
