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
//	Board lastVisited = null;
//	private List<Board> visited = new ArrayList<>();

	// find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
    	if (initial == null) {
    		throw new NullPointerException();
    	}

    	queue.insert(new SearchNode(initial, null, 0));
    	queue2.insert(new SearchNode(initial.twin(), null, 0));

		while (!queue.isEmpty()) {
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

    private boolean isVisited2(Board board) {
    	return visited2.contains(board);
//    	return lastVisited2.equals(board);
    }

    private void visited2(Board board) {
		visited2.add(board);
//		lastVisited2 = curr;
    }

    private boolean isVisited(Board board) {
    	return visited.contains(board);
//    	return lastVisited.equals(board);
    }

    private void visited(Board board) {
		visited.add(board);
//		lastVisited = curr;
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
    }

    private class MoveComparator implements Comparator<SearchNode> {
		@Override
        public int compare(SearchNode o1, SearchNode o2) {
			if (o1.board.isGoal()) {
				return -1;
			}
			if (o2.board.isGoal()) {
				return 1;
			}
			// increase weight to find non-minimal solutions (faster) e.g. (o1.moves + o2.moves + 2) / 2;
			int weight = 1;
	        int score1 = o1.board.manhattan() * weight + o1.moves;
	        int score2 = o2.board.manhattan() * weight + o2.moves;
	        if (score1 == score2) {
	        	score1 = o1.board.hamming() * weight + o1.moves;
	        	score2 = o2.board.hamming() * weight + o2.moves;
	        }
	        return score1 - score2;
        }
    }
}
