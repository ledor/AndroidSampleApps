package com.ledor.othello;

import android.annotation.SuppressLint;
import java.util.Random;

@SuppressLint("DefaultLocale")
public class OthelloAI {
	public static final int maxRank = Integer.MAX_VALUE - 64;

	public enum Difficulty {
		BEGINNER,
		INTERMEDIATE,
		ADVANCED,
		EXPERT,
	 }

	/*
	 *  AI parameters.
	 */
	private int mLookAheadDepth;
	private int mForfeitWeight;
	private int mBoarderWeight;
	private int mMobilityWeight;
	private int mStabilityWeight;
	
	private boolean mIsAIEnd = false;

	public OthelloAI() {
		mForfeitWeight = 2;
		mBoarderWeight = 1;
		mMobilityWeight = 0;
		mStabilityWeight = 3;
		mLookAheadDepth = 3;
		SetAIParameters("Beginner");
	}
	
	public OthelloAI(String level) {
		SetAIParameters(level);
	}
	
	/*
	 *  Set the AI parameter weights.
	 */
	private void SetAIParameters(String level)
	{
		Difficulty lvl = Difficulty.valueOf(level.toUpperCase());
		switch (lvl)
		{
			case BEGINNER:
				this.mForfeitWeight      =  2;
				this.mBoarderWeight     =  1;
				this.mMobilityWeight     =  0;
				this.mStabilityWeight    =  3;
				break;
			case INTERMEDIATE:
				this.mForfeitWeight      =  3;
				this.mBoarderWeight     =  1;
				this.mMobilityWeight     =  0;
				this.mStabilityWeight    =  5;
				break;
			case ADVANCED:
				this.mForfeitWeight      =  7;
				this.mBoarderWeight     =  2;
				this.mMobilityWeight     =  1;
				this.mStabilityWeight    = 10;
				break;
			case EXPERT:
				this.mForfeitWeight      = 35;
				this.mBoarderWeight     = 10;
				this.mMobilityWeight     =  5;
				this.mStabilityWeight    = 50;
				break;
			default:
				this.mForfeitWeight      =  0;
				this.mBoarderWeight     =  0;
				this.mMobilityWeight     =  0;
				this.mStabilityWeight    =  0;
				break;
		}

		/*
		 *  Set the look-ahead depth.
		 */
		mLookAheadDepth = lvl.ordinal() + 3;

	}

	public OthelloEngine.BoardSquare GetBestMove(OthelloEngine aiboard, int turn, int depth, int alpha, int beta)
	{
		/*
		 *  Initialize the best move.
		 */
		OthelloEngine.BoardSquare bestMove = aiboard.new BoardSquare(-1, -1);
		bestMove.setrank(turn * maxRank);

		/*
		 *  Find out how many valid moves we have so we can initialize the
		 *  mobility score.
		 */
		int validMoves = aiboard.GetValidMoveCount(turn);

		/*
		 *  Start at a random position on the board. This way, if two or
		 *  more moves are equally good, we'll take one of them at random.
		 */
		Random random = new Random();
		int rowStart = random.nextInt(8);
		int colStart = random.nextInt(8);

		/*
		 *  Check all valid moves.
		 */
		int i, j;
		for (i = 0; i < 8; i++)
		{
			if (mIsAIEnd)
			{
				return (null);
			}
			for (j = 0; j < 8; j++)
			{
				if (mIsAIEnd)
				{
					return (null);
				}
				/*
				 *  Get the row and column.
				 */
				int row = (rowStart + i) % 8;
				int col = (colStart + j) % 8;

				if (aiboard.IsValidMove(turn, row, col))
				{
					/*
					 *  Make the move.
					 */
					OthelloEngine.BoardSquare testMove = aiboard.new BoardSquare(row, col);
					OthelloEngine testBoard = new OthelloEngine(aiboard);
					testBoard.placeDisc(turn, testMove.getrownum(), testMove.getcolnum());
					int score = testBoard.getWhiteCount() - testBoard.getBlackCount();

					/*
					 *  Check the board.
					 */
					int nextTurn = -turn;
					int forfeit = 0;
					boolean isEndGame = false;
					int opponentValidMoves = testBoard.GetValidMoveCount(nextTurn);
					if (opponentValidMoves == 0)
					{
						/*
						 *  The opponent cannot move, count the forfeit.
						 */
						forfeit = turn;

						/*
						 *  Switch back to the original color.
						 */
						nextTurn = -nextTurn;

						/*
						 *  If that player cannot make a move either, the game is over.
						 */
						if (!testBoard.hasAnyValidMove(nextTurn))
							isEndGame = true;
					}

					/*
					 *  If we reached the end of the look ahead (end game or
					 *  max depth), evaluate the board and set the move rank.
					 */
					if (isEndGame || depth == mLookAheadDepth || mIsAIEnd)
					{
						if (mIsAIEnd)
						{
							return (null);
						}

						/*
						 *  For an end game, max the ranking and add on the final score.
						 */
						if (isEndGame)
						{
							/*
							 *  Negative value for black win.
							 */
							if (score < 0)
								testMove.setrank(-maxRank + score);

							/*
							 *  Positive value for white win.
							 */
							else if (score > 0)
								testMove.setrank(maxRank + score);

							/*
							 *  Zero for a draw.
							 */
							else
								testMove.setrank(0);
						}

						/*
						 *  It's not an end game so calculate the move rank.
						 */
						else
						{
							testMove.setrank(
								mForfeitWeight   * forfeit +
								mBoarderWeight  * (testBoard.getBlackBoarderCount() - testBoard.getWhiteBoarderCount()) +
								mMobilityWeight  * turn * (validMoves - opponentValidMoves) +
								mStabilityWeight * (testBoard.getWhiteSafeCount() - testBoard.getBlackSafeCount()) +
								                       score);
						}
					}

					/*
					 *  Otherwise, perform a look ahead.
					 */
					else
					{
						OthelloEngine.BoardSquare nextMove = GetBestMove(testBoard, nextTurn, depth + 1, alpha, beta);

						if (nextMove == null ) {
							return null;
						}
						/*
						 *  Pull up the rank.
						 */
						testMove.setrank(nextMove.getrank());

						/*
						 *  Forfeits are cumulative, so if the move did not result in an end game,
						 *  add any current forfeit value to the rank.
						 */
						if (forfeit != 0 && Math.abs(testMove.getrank()) < maxRank)
							testMove.setrank(testMove.getrank() + mForfeitWeight * forfeit);

						/*
						 *  Adjust the alpha and beta values, if necessary.
						 */
						if (turn == OthelloEngine.WHITE_DISC && testMove.getrank() > beta)
						{
							beta = testMove.getrank();
						}
						if (turn == OthelloEngine.BLACK_DISC && testMove.getrank() < alpha)
						{
							alpha = testMove.getrank();
						}
					}

					/*
					 *  Perform a cutoff if the rank is outside tha alpha-beta range.
					 */
					if (turn == OthelloEngine.WHITE_DISC && testMove.getrank() > alpha)
					{
						testMove.setrank(alpha);
						return mIsAIEnd ? null : testMove;
					}
					if (turn == OthelloEngine.BLACK_DISC && testMove.getrank() < beta)
					{
						testMove.setrank(beta);
						return mIsAIEnd ? null : testMove;
					}

					/*
					 *  If this is the first move tested, assume it is the best for now.
					 */
					if (bestMove.getrownum() < 0)
					{
						bestMove = testMove;
					}

					/*
					 *  Otherwise, compare the test move to the current best move
					 *  and take the one that is better for this color.
					 */
					else if (turn * testMove.getrank() > turn * bestMove.getrank())
					{
						bestMove = testMove;
					}
				}
			}
		}

		/*
		 *  Return the best move found.
		 */
		return mIsAIEnd ? null : bestMove;
	}
	
	public void endAI (boolean end)
	{
		mIsAIEnd = end; 
	}
}
