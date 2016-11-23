import static org.junit.Assert.*;

import org.junit.Test;


public class TestNeighbors {

	@Test
	public void testBlankCorner1() {
		Board board = new Board(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 0}});
		Board north = new Board(new int[][]{{1, 2, 3}, {4, 5, 0}, {7, 8, 6}});
		Board east = new Board(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 0, 8}});
		Board[] boards = new Board[]{north, east};
		int[] hammings = new int[]{1, 1};
		int[] manhattans = new int[]{1, 1};
		int index = 0;
		for (Board neighbor : board.neighbors()) {
			assertTrue(neighbor.manhattan() == manhattans[index]);
			assertTrue(neighbor.hamming() == hammings[index]);
			assertTrue(neighbor.equals(boards[index++]));
		}
		assertTrue(index == 2);
	}

	@Test
	public void testBlankCorner2() {
		Board board = new Board(new int[][]{{1, 2, 3}, {4, 5, 6}, {0, 8, 7}});
		Board north = new Board(new int[][]{{1, 2, 3}, {0, 5, 6}, {4, 8, 7}});
		Board west = new Board(new int[][]{{1, 2, 3}, {4, 5, 6}, {8, 0, 7}});
		Board[] boards = new Board[]{north, west};
		int index = 0;
		for (Board neighbor : board.neighbors()) {
			assertTrue(neighbor.equals(boards[index++]));
		}
		assertTrue(index == 2);
	}

	@Test
	public void testBlankCorner3() {
		Board board = new Board(new int[][]{{1, 2, 0}, {4, 5, 6}, {7, 8, 3}});
		Board south = new Board(new int[][]{{1, 2, 6}, {4, 5, 0}, {7, 8, 3}});
		Board west = new Board(new int[][]{{1, 0, 2}, {4, 5, 6}, {7, 8, 3}});
		Board[] boards = new Board[]{south, west};
		int index = 0;
		for (Board neighbor : board.neighbors()) {
			assertTrue(neighbor.equals(boards[index++]));
		}
		assertTrue(index == 2);
	}

	@Test
	public void testBlankCorner4() {
		Board board = new Board(new int[][]{{0, 2, 3}, {4, 5, 6}, {7, 8, 1}});
		Board east = new Board(new int[][]{{2, 0, 3}, {4, 5, 6}, {7, 8, 1}});
		Board south = new Board(new int[][]{{4, 2, 3}, {0, 5, 6}, {7, 8, 1}});
		Board[] boards = new Board[]{east, south};
		int index = 0;
		for (Board neighbor : board.neighbors()) {
			assertTrue(neighbor.equals(boards[index++]));
		}
		assertTrue(index == 2);
	}

	@Test
	public void testBlankEdge1() {
		Board board = new Board(new int[][]{{1, 0, 3}, {4, 5, 6}, {7, 8, 2}});
		Board east = new Board(new int[][]{{1, 3, 0}, {4, 5, 6}, {7, 8, 2}});
		Board west = new Board(new int[][]{{0, 1, 3}, {4, 5, 6}, {7, 8, 2}});
		Board south = new Board(new int[][]{{1, 5, 3}, {4, 0, 6}, {7, 8, 2}});
		Board[] boards = new Board[]{east, south, west};
		int index = 0;
		for (Board neighbor : board.neighbors()) {
			assertTrue(neighbor.equals(boards[index++]));
		}
		assertTrue(index == 3);
	}

	@Test
	public void testBlankEdge2() {
		Board board = new Board(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 0, 8}});
		Board north = new Board(new int[][]{{1, 2, 3}, {4, 0, 6}, {7, 5, 8}});
		Board east = new Board(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 0}});
		Board west = new Board(new int[][]{{1, 2, 3}, {4, 5, 6}, {0, 7, 8}});
		Board[] boards = new Board[]{north, east,  west};
		int index = 0;
		for (Board neighbor : board.neighbors()) {
			assertTrue(neighbor.equals(boards[index++]));
		}
		assertTrue(index == 3);
	}

	@Test
	public void testBlankEdge3() {
		Board board = new Board(new int[][]{{1, 2, 3}, {0, 5, 6}, {7, 4, 8}});
		Board north = new Board(new int[][]{{0, 2, 3}, {1, 5, 6}, {7, 4, 8}});
		Board east = new Board(new int[][]{{1, 2, 3}, {5, 0, 6}, {7, 4, 8}});
		Board south = new Board(new int[][]{{1, 2, 3}, {7, 5, 6}, {0, 4, 8}});
		Board[] boards = new Board[]{north, east,  south};
		int index = 0;
		for (Board neighbor : board.neighbors()) {
			assertTrue(neighbor.equals(boards[index++]));
		}
		assertTrue(index == 3);
	}

	@Test
	public void testBlankEdge4() {
		Board board = new Board(new int[][]{{1, 2, 3}, {6, 5, 0}, {7, 4, 8}});
		Board north = new Board(new int[][]{{1, 2, 0}, {6, 5, 3}, {7, 4, 8}});
		Board west = new Board(new int[][]{{1, 2, 3}, {6, 0, 5}, {7, 4, 8}});
		Board south = new Board(new int[][]{{1, 2, 3}, {6, 5, 8}, {7, 4, 0}});
		Board[] boards = new Board[]{north, south, west};
		int index = 0;
		for (Board neighbor : board.neighbors()) {
			assertTrue(neighbor.equals(boards[index++]));
		}
		assertTrue(index == 3);
	}

	@Test
	public void testBlankCenter() {
		Board board = new Board(new int[][]{{1, 2, 3}, {4, 0, 6}, {7, 8, 5}});
		Board north = new Board(new int[][]{{1, 0, 3}, {4, 2, 6}, {7, 8, 5}});
		Board east = new Board(new int[][]{{1, 2, 3}, {4, 6, 0}, {7, 8, 5}});
		Board south = new Board(new int[][]{{1, 2, 3}, {4, 8, 6}, {7, 0, 5}});
		Board west = new Board(new int[][]{{1, 2, 3}, {0, 4, 6}, {7, 8, 5}});
		Board[] boards = new Board[]{north, east, south, west};
		int index = 0;
		for (Board neighbor : board.neighbors()) {
			assertTrue(neighbor.equals(boards[index++]));
		}
		assertTrue(index == 4);
	}
}
