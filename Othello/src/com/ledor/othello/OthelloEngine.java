package com.ledor.othello;

import android.graphics.Point;
import android.util.Log;

public class OthelloEngine {
	
	public static final int BLACK_DISC = -1;
	public static final int EMPTY =  0;
	public static final int WHITE_DISC = 1;
    public final static int DISC_DIAMETER = 40;
    
    /*
     * The squares of the board (8x8)
     */
    private int mSquares[][];
    private boolean mSafeDiscs[][];
    
	/*
	 *  Internal Counters
	 */
	private int mBlackCount;
	private int mWhiteCount;
	private int mEmptyCount;
	private int mBlackBoarderCount;
	private int mWhiteBoarderCount;
	private int mBlackSafeCount;
	private int mWhiteSafeCount;

    /*
	 * Create new, empty Othello Board
	 */
	public OthelloEngine() {
		mSquares = new int[8][8];
		mSafeDiscs = new boolean[8][8];

		int a, b;
		for (a = 0; a < 8; a++)
			for (b = 0; b < 8; b++)
			{
				mSquares[a][b] = EMPTY;
				mSafeDiscs[a][b] = false;
			}

		UpdateCount();
    }
	
	/*
	 * Create Othello Board copied from existing board for AI to analyze
	 */
	public OthelloEngine(OthelloEngine board)
	{
		mSquares = new int[8][8];
		mSafeDiscs = new boolean[8][8];

		int a, b;
		for (a = 0; a < 8; a++)
		{
			for (b = 0; b < 8; b++)
			{
				mSquares[a][b] = board.mSquares[a][b];
				mSafeDiscs[a][b] = board.mSafeDiscs[a][b];
			}
		}

		mBlackCount = board.mBlackCount;
		mWhiteCount = board.mWhiteCount;
		mEmptyCount = board.mEmptyCount;
		mBlackBoarderCount = board.mBlackBoarderCount;
		mWhiteBoarderCount = board.mWhiteBoarderCount;
		mBlackSafeCount = board.mBlackSafeCount;
		mWhiteSafeCount = board.mWhiteSafeCount;

	}
	
	// Get counters
	public int getBlackCount()
	{
		return mBlackCount;
	}
	
	public int getWhiteCount()
	{
		return mWhiteCount;
	}
	public int getEmptyCount()
	{
		return mEmptyCount;
	}
	public int getBlackBoarderCount()
	{
		return mBlackBoarderCount;
	}
	public int getWhiteBoarderCount()
	{
		return mWhiteBoarderCount;
	}
	public int getBlackSafeCount()
	{
		return mBlackSafeCount;
	}
	public int getWhiteSafeCount()
	{
		return mWhiteSafeCount;
	}

	public int getSquareContents(int row, int col)
	{
		return mSquares[row][col];
	}

	/*
	 * Update the disc and internal counters
	 */
	private void UpdateCount( )
	{
		mBlackCount = 0;
		mWhiteCount = 0;
		mEmptyCount = 0;
		mBlackBoarderCount = 0;
		mWhiteBoarderCount = 0;
		mBlackSafeCount = 0;
		mWhiteSafeCount = 0;

		int a, b;

		boolean statusChanged = true;
		while (statusChanged)
		{
			statusChanged = false;
			for (a = 0; a < 8; a++)
			{
				for (b = 0; b < 8; b++)
				{
					if (mSquares[a][b] != EMPTY && !mSafeDiscs[a][b] && !IsFlippable(a, b))
					{
						mSafeDiscs[a][b] = true;
						statusChanged = true;
					}
				}
			}
		}

		int dr, dc;
		for (a = 0; a < 8; a++)
		{
			for (b = 0; b < 8; b++)
			{
				boolean isLastPiece = false;
				if (mSquares[a][b] != EMPTY)
				{
					for (dr = -1; dr <= 1; dr++)
					{
						for (dc = -1; dc <= 1; dc++)
						{
							if (!(dr == 0 && dc == 0) && a + dr >= 0 && a + dr < 8 && b + dc >= 0 && b + dc < 8 && mSquares[a + dr][b + dc] == EMPTY)
							{
								isLastPiece = true;
							}
						}
					}
				}

				// Update the counts.
				if (mSquares[a][b] == BLACK_DISC)
				{
					mBlackCount++;
					if (isLastPiece)
					{
						mBlackBoarderCount++;
					}
					if (mSafeDiscs[a][a])
					{
						mBlackSafeCount++;
					}
				}
				else if (mSquares[a][b] == WHITE_DISC)
				{
					mWhiteCount++;
					if (isLastPiece)
					{
						mWhiteBoarderCount++;
					}
					if (mSafeDiscs[a][a])
					{
						mWhiteSafeCount++;
					}
				}
				else
				{
					mEmptyCount++;
				}
			}
		}
		
	}

	/*
	 * Check if the one square is flippable
	 */
	private boolean IsFlippable(int row, int col)
	{
		int color = mSquares[row][col];

		int a, b;
		boolean hasSpaceSide1, hasSpaceSide2;
		boolean hasUnsafeSide1, hasUnsafeSide2;

		/*
		 *  Check the horizontal line through the disc.
		 */
		hasSpaceSide1  = false; /* empty square on left side of the disc */
		hasUnsafeSide1 = false; /* different disc color on left side of the disc */
		hasSpaceSide2  = false; /* empty square on right side of the disc */
		hasUnsafeSide2 = false; /* different disc color on right side of the disc */
		
		/*
		 *  Check Left side.
		 */
		for (b = 0; b < col && !hasSpaceSide1; b++)
		{
			if (mSquares[row][b] == EMPTY)
			{
				hasSpaceSide1 = true;
			}
			else if  (mSquares[row][b] != color || !mSafeDiscs[row][b])
			{
				hasUnsafeSide1 = true;
			}
		}
		
		/*
		 *  Check Right side.
		 */
		for (b = col + 1; b < 8 && !hasSpaceSide2; b++)
		{
			if (mSquares[row][b] == EMPTY)
			{
				hasSpaceSide2 = true;
			}
			else if (mSquares[row][b] != color || !mSafeDiscs[row][b])
			{
				hasUnsafeSide2 = true;
			}
		}
		
		if ((hasSpaceSide1  && hasSpaceSide2 ) ||
				(hasSpaceSide1  && hasUnsafeSide2) ||
				(hasUnsafeSide1 && hasSpaceSide2 ))
		{
				return true;
		}
		
		/*
		 * Check the vertical line through the disc.
		 */
		hasSpaceSide1  = false; /* empty square above of the disc */
		hasUnsafeSide1 = false; /* different disc color above of the disc */
		hasSpaceSide2  = false; /* empty square bottom of the disc */
		hasUnsafeSide2 = false; /* different disc color bottom of the disc */
		
		/*
		 * Top of the disc
		 */
		for (a = 0; a < row && !hasSpaceSide1; a++)
		{
			if (mSquares[a][col] == EMPTY)
			{
				hasSpaceSide1 = true;
			}
			else if (mSquares[a][col] != color || !mSafeDiscs[a][col])
			{
				hasUnsafeSide1 = true;
			}
		}
		
		/*
		 * Bottom of the disc
		 */
		for (a = row + 1; a < 8 && !hasSpaceSide2; a++)
		{
			if (mSquares[a][col] == EMPTY)
			{
				hasSpaceSide2 = true;
			}
			else if (mSquares[a][col] != color || !mSafeDiscs[a][col])
			{
				hasUnsafeSide2 = true;
			}
		}
		
		if ((hasSpaceSide1  && hasSpaceSide2 ) ||
				(hasSpaceSide1  && hasUnsafeSide2) ||
				(hasUnsafeSide1 && hasSpaceSide2 ))
		{
				return true;
		}

		/*
		 * Check TopLeft and BottomRight of the disc
		 */
		hasSpaceSide1  = false; /* empty square TopLeft of the disc */
		hasUnsafeSide1 = false; /* different disc color TopLeft of the disc */
		hasSpaceSide2  = false; /* empty square BottomRight of the disc */
		hasUnsafeSide2 = false; /* different disc color BottomRight of the disc */

		/*
		 * TopLeft of the disc
		 */
		a = row - 1;
		b = col - 1;
		while (a >= 0 && b >= 0 && !hasSpaceSide1)
		{
			if (mSquares[a][b] == EMPTY)
			{
				hasSpaceSide1 = true;
			}
			else if (mSquares[a][b] != color || !mSafeDiscs[a][b])
			{
				hasUnsafeSide1 = true;
			}
			a--;
			b--;
		}
		
		/*
		 * BottomRight of the disc
		 */
		a = row + 1;
		b = col + 1;
		while (a < 8 && b < 8 && !hasSpaceSide2)
		{
			if (mSquares[a][b] == EMPTY)
			{
				hasSpaceSide2 = true;
			}
			else if (mSquares[a][b] != color || !mSafeDiscs[a][b])
			{
				hasUnsafeSide2 = true;
			}
			a++;
			b++;
		}
		
		if ((hasSpaceSide1  && hasSpaceSide2 ) ||
			(hasSpaceSide1  && hasUnsafeSide2) ||
			(hasUnsafeSide1 && hasSpaceSide2 ))
		{
			return true;
		}

		/*
		 * Check the TopRight and BottomLeft of the disc
		 */
		hasSpaceSide1  = false; /* empty square TopRight of the disc */
		hasUnsafeSide1 = false; /* different disc color TopRight of the disc */
		hasSpaceSide2  = false; /* empty square BottomLeft of the disc */
		hasUnsafeSide2 = false; /* different disc color BottomLeft of the disc */

		/*
		 * TopRight of the disc
		 */
		a = row - 1;
		b = col + 1;
		while (a >= 0 && b < 8 && !hasSpaceSide1)
		{
			if (mSquares[a][b] == EMPTY)
			{
				hasSpaceSide1 = true;
			}
			else if (mSquares[a][b] != color || !mSafeDiscs[a][b])
			{
				hasUnsafeSide1 = true;
			}
			a--;
			b++;
		}
		
		/*
		 * BottomLeft of the disc
		 */
		a = row + 1;
		b = col - 1;
		while (a < 8 && b >= 0 && !hasSpaceSide2)
		{
			if (mSquares[a][b] == EMPTY)
			{
				hasSpaceSide2 = true;
			}
			else if (mSquares[a][b] != color || !mSafeDiscs[a][b])
			{
				hasUnsafeSide2 = true;
			}
			a++;
			b--;
		}
		if ((hasSpaceSide1  && hasSpaceSide2 ) ||
			(hasSpaceSide1  && hasUnsafeSide2) ||
			(hasUnsafeSide1 && hasSpaceSide2 ))
		{
			return true;
		}

		return false;
	}

	/*
	 * New Game
	 */
	public void newGame()
	{
		int a, b;
		for (a = 0; a < 8; a++)
			for (b = 0; b < 8; b++)
			{
				mSquares[a][b] = EMPTY;
				mSafeDiscs[a][b] = false;
			}

		/*
		 *  Set two black and two white discs in the center.
		 */
		mSquares[3][3] = WHITE_DISC;
		mSquares[3][4] = BLACK_DISC;
		mSquares[4][3] = BLACK_DISC;
		mSquares[4][4] = WHITE_DISC;

		UpdateCount();
	}

	/*
	 * Place the disc on the board and flip the other discs
	 */
	public void placeDisc(int color, int row, int col)
	{
		if (row >= 8 || row < 0 || col >= 8 || col < 0) {
			Log.i("debug", "Invalid move. " + "row: " + row + "; col: " + col);
		}
		mSquares[row][col] = color;

		int dr, dc;
		int r, c;
		for (dr = -1; dr <= 1; dr++)
			for (dc = -1; dc <= 1; dc++)
				if (!(dr == 0 && dc == 0) && IsFlippableLine(color, row, col, dr, dc))
				{
					r = row + dr;
					c = col + dc;
					while(mSquares[r][c] == -color)
					{
						mSquares[r][c] = color;
						r += dr;
						c += dc;
					}
				}

		UpdateCount();
	}

	/*
	 * Check if the game is already ended
	 */
    public boolean IsEndGame() {
    	
        if (GetValidMoveCount(WHITE_DISC) <= 0 && GetValidMoveCount(BLACK_DISC) <= 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
	/*
	 * Check if we can still get valid moves
	 */
	public int GetValidMoveCount(int color)
	{
		int n = 0;

		int i, j;
		for (i = 0; i < 8; i++)
		{
			for (j = 0; j < 8; j++)
			{
				if (this.IsValidMove(color, i, j))
				{
					n++;
				}
			}
		}
		return n;
	}
    
	/*
	 * Check if it is a valid move
	 */
	public boolean IsValidMove(int color, int row, int col)
	{
		if (mSquares[row][col] != EMPTY)
		{
			return false;
		}

		int dr, dc;
		for (dr = -1; dr <= 1; dr++)
		{
			for (dc = -1; dc <= 1; dc++)
			{
				if (!(dr == 0 && dc == 0) && IsFlippableLine(color, row, col, dr, dc))
				{
					return true;
				}
			}
		}

		return false;
	}
    
	/*
	 * Check if the straight line can be flipped
	 */
	private boolean IsFlippableLine(int color, int row, int col, int dr, int dc)
	{
		int r = row + dr;
		int c = col + dc;
		while (r >= 0 && r < 8 && c >= 0 && c < 8 && mSquares[r][c] == -color)
		{
			r += dr;
			c += dc;
		}

		if (r < 0 || r > 7 || c < 0 || c > 7 || (r - dr == row && c - dc == col) || mSquares[r][c] != color)
		{
			return false;
		}

		return true;
	}
    
	/*
	 * Check if there are still valid moves
	 */
	public boolean hasAnyValidMove(int color)
	{
		// Check all board positions for a valid move.
		int r, c;
		for (r = 0; r < 8; r++)
			for (c = 0; c < 8; c++)
				if (this.IsValidMove(color, r, c))
					return true;

		// None found.
		return false;
	}
	
	/*
	 * Get the winner of the game
	 */
    public int getWinner() {
        if (mWhiteCount > mBlackCount)
        {
            return WHITE_DISC;
        }
        else if (mBlackCount > mWhiteCount)
        {
            return BLACK_DISC;
        }
        else
        {
            return EMPTY;
        }
    }

    /*
	 * BoardSquare Class
	 * Represent one square in the Othello Board
	 */
	public class BoardSquare {
	    private int rowNum;
	    private int colNum;
	    private int rank;
	    
	    public BoardSquare(int rownum,int colnum){
	        this.rowNum = rownum;
	        this.colNum = colnum;
	        this.rank = 0;
	    }
	    
	    public int getrownum() {
	        return this.rowNum;
	    }
	    
	    public int getcolnum() {
	        return this.colNum;
	    }
	    
	    public int getrank() {
	        return this.rank;
	    }

	    public void setrownum(int rownum) {
	        this.rowNum = rownum;
	    }
	    
	    public void setcolnum(int colnum){
	        this.colNum = colnum;
	    }

	    public void setrank(int rank){
	        this.rank = rank;
	    }
	}
	
	/*
	 * BoardCoordinate Class
	 * Coordinate of each square in Othello Board
	 */
	public class BoardCoordinate {
	    private Point point[];
	    
	    public BoardCoordinate(Point topLeft,Point topRight,Point bottomRight,Point bottomLeft) {
	        point = new Point[4];
	        point[0] = topLeft;
	        point[1] = topRight;
	        point[2] = bottomRight;
	        point[3] = bottomLeft;
	    }
	    
	    public Point getBoardCoordinate(int index) {
	        return point[index];
	    }

	}
}
