package com.ledor.othello;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class OthelloGUI extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	
	private Othello mOthelloMainActivity;
	private OthelloEngine mBoard;
	private OthelloAI mAI;
	private Thread mOthelloThread;
	private OthelloEngine.BoardCoordinate mCoordinates[][];
	private int mBoardWidth = 0;
	private int mBoardHeight = 0;
	private int mScoreBoardHeight = 120;
	private Bitmap mWhitePiece, mBlackPiece;
	private int mCurrentTurn;
	private boolean mIsEndGame = false;
	private String mWinner = "";
	private boolean mMoveMade = false;
	private String mUserName;
	private String mGameLevel;
	private boolean mIsNewGame = true;
	
	public OthelloGUI(Context context) {
		
		super(context);
		getHolder().addCallback(this);
		mOthelloMainActivity = (Othello)context;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Canvas canvas = null;
		
		try {

			canvas = getHolder().lockCanvas(null);
			synchronized (getHolder()) {
				mBoardWidth = canvas.getWidth();
				mBoardHeight = canvas.getHeight() - mScoreBoardHeight;
				initializeComponents();
				doDraw(canvas);
				updateScores(canvas);
				drawContent(canvas);				
			}
		} finally {

			if (canvas != null)
			{
				getHolder().unlockCanvasAndPost(canvas);

			}
		}
		}
		

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	/*
	 * Initialize all game components
	 */
	private void initializeComponents() {
		
		mBoard = new OthelloEngine();
		initializeBoard();	
		mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.blackpiece);
		mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.whitepiece);
		mBoard.newGame();
		GetGameSetting();
		mAI = new OthelloAI(mGameLevel);
		mCurrentTurn = OthelloEngine.WHITE_DISC;
		mOthelloThread = new Thread(this);
		mOthelloThread.setName("Othello");
		mOthelloThread.start();	
	}

	/*
	 * Initialize main board
	 */
	private void initializeBoard() {
		mCoordinates = new OthelloEngine.BoardCoordinate[8][8];
		int horizontal_incr = mBoardWidth / 8;
		int vertical_incr = mBoardHeight / 8;
		int horizontal_start = 0;
		int vertical_start = 0;
		int x, y = 0;

		for (x = 0; x < 8; x++)
		{
			for (y = 0; y < 8; y++)
			{
				Point a = new Point(horizontal_start, vertical_start);
				Point b = new Point(horizontal_start + horizontal_incr,
						vertical_start);
				Point c = new Point(horizontal_start + horizontal_incr,
						vertical_start + vertical_incr);
				Point d = new Point(horizontal_start, vertical_start
						+ vertical_incr);
				try {
					mCoordinates[x][y] = mBoard.new BoardCoordinate(a, b, c, d);
				} catch (Exception e) {
					Log.i("error", e.getMessage());
				}
				horizontal_start += horizontal_incr;
			}
			horizontal_start = 0;
			vertical_start += vertical_incr;

		}

	}

	/*
	 * Draw the main board
	 */
	private void doDraw(Canvas canvas) {		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GREEN);
		canvas.drawPaint(paint);

		int width = mBoardWidth;
		int height = mBoardHeight;		

		int xpos = width / 8;
		int ypos = height / 8;
		
		paint.setColor(Color.BLACK);
		for (int i = 0; i < 8; i++)
		{			
			canvas.drawLine(xpos + (xpos * i), 0, xpos + (xpos * i), height, paint);		
		}
		
		for (int i = 0; i < 8; i++)
		{		
			canvas.drawLine(0, (ypos * i) + ypos, width, (ypos * i) + ypos, paint);
		}
	}

	/*
	 * Update the score board
	 */
	private void updateScores(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);		    
		paint.setColor(Color.WHITE);	
		paint.setTextSize(20);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		
		Rect rect = new Rect(1, mBoardHeight, mBoardWidth, mBoardHeight + mScoreBoardHeight);		
		canvas.drawRect(rect, paint);
		paint.setColor(Color.BLACK);
		
		canvas.drawText("Welcome back, " + mUserName, 15, mBoardHeight + 30, paint);
		
		canvas.drawText("Game Level: " + mGameLevel, 15, mBoardHeight + 50, paint);

		canvas.drawText("White : " + mBoard.getWhiteCount(), 15, mBoardHeight + 70, paint);
		canvas.drawText("Black : " + mBoard.getBlackCount(), 115, mBoardHeight + 70, paint);
		
		if(mIsEndGame == true)
		{
			canvas.drawText(mWinner, 15, mBoardHeight + 90, paint);
		}
		else
		{
			if(mCurrentTurn == OthelloEngine.BLACK_DISC)
			{
				canvas.drawText("I am thinking !!!!", 15, mBoardHeight +90, paint);
			}
			else
			{
				canvas.drawText("Your turn !!!!", 15, mBoardHeight + 90, paint);
			}
		}
	}
	
	/*
	 * Layout discs on the board
	 */
	private void drawContent(Canvas canvas) {		
		for (int row = 0; row < 8; row++)
		{
			for (int col = 0; col < 8; col++)
			{
				if (mBoard.getSquareContents(row,col) == OthelloEngine.BLACK_DISC)
				{					
					drawBlackDisc(canvas, mBoard.new BoardSquare(row, col));
				}
				else if (mBoard.getSquareContents(row,col) == OthelloEngine.WHITE_DISC)
				{					
					drawWhiteDisc(canvas, mBoard.new BoardSquare(row, col));
				}
			}
		}

	}

	/*
	 * Draw black discs on the board
	 */
	private void drawBlackDisc(Canvas canvas, OthelloEngine.BoardSquare boardSquare) {
		Paint paint = new Paint();
		
		int xOffset = (int) (((mBoardWidth / 8) - OthelloEngine.DISC_DIAMETER) / 2);
		int yOffset = (int) (((mBoardHeight / 8) - OthelloEngine.DISC_DIAMETER) / 2);
		
		Point topLeft = mCoordinates[boardSquare.getrownum()][boardSquare.getcolnum()].getBoardCoordinate(0);
		canvas.drawBitmap(mBlackPiece, topLeft.x + xOffset, topLeft.y + yOffset, paint);
		
	}
	
	/*
	 * Draw white discs on the board
	 */
	private void drawWhiteDisc(Canvas canvas, OthelloEngine.BoardSquare boardSquare) {		
		Paint paint = new Paint();
		
		int xOffset = (int) (((mBoardWidth / 8) - OthelloEngine.DISC_DIAMETER) / 2);
		int yOffset = (int) (((mBoardHeight / 8) - OthelloEngine.DISC_DIAMETER) / 2);
		
		Point topLeft = mCoordinates[boardSquare.getrownum()][boardSquare.getcolnum()].getBoardCoordinate(0);
		canvas.drawBitmap(mWhitePiece, topLeft.x + xOffset, topLeft.y + yOffset, paint);
		
	}

	@Override
	public void run() {
		
		initializeBoard();
		
		while (true) {
			if (mBoard.IsEndGame() == true)
			{
				if (mBoard.getWinner() == OthelloEngine.BLACK_DISC)
				{
					mIsEndGame = true;
					mWinner = mOthelloMainActivity.getString(R.string.blackWinner);					
				}
				else if (mBoard.getWinner() == OthelloEngine.WHITE_DISC)
				{
					mIsEndGame = true;
					mWinner = mOthelloMainActivity.getString(R.string.whiteWinner);					
				}
				else
				{
					mIsEndGame = true;
					mWinner = mOthelloMainActivity.getString(R.string.draw);					
				}
				DrawBoard();
				SaveHighScore();
			}
			else
			{
				if (mCurrentTurn == OthelloEngine.WHITE_DISC)
				{
					while (mMoveMade == false)
					{
						/* wait till the user makes a move */
					}
					mIsNewGame = false;
					mAI.endAI(false);
					mCurrentTurn = OthelloEngine.BLACK_DISC;					
					mMoveMade = false;
					DrawBoard();

				}
				else if (mCurrentTurn == OthelloEngine.BLACK_DISC)
				{
					if ( mBoard.hasAnyValidMove(OthelloEngine.BLACK_DISC) && !mIsEndGame)
					{
						int alpha = OthelloAI.maxRank + 64;
						int beta = -alpha;
						OthelloEngine.BoardSquare move = mAI.GetBestMove(mBoard, mCurrentTurn, 1, alpha, beta);
						if (move == null) {
							if (mIsNewGame) 
							{
								mCurrentTurn = OthelloEngine.WHITE_DISC;							
							}
							else
							{
								mCurrentTurn = OthelloEngine.BLACK_DISC;							
							}
						} else {
							mBoard.placeDisc(mCurrentTurn, move.getrownum(), move.getcolnum());
							mCurrentTurn = OthelloEngine.WHITE_DISC;						
						}
						if (!mIsNewGame) 
						{
							DrawBoard();
						}
						
						if ( !mBoard.hasAnyValidMove(OthelloEngine.WHITE_DISC) && !mIsEndGame)
						{
							mCurrentTurn = OthelloEngine.BLACK_DISC;
						}
					} else {
						mCurrentTurn = OthelloEngine.WHITE_DISC;
						DrawBoard();
					}
				}

			}

		}		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		touchResponse((int) event.getX(), (int) event.getY());
		return super.onTouchEvent(event);
	}

	/*
	 * Process the user activity
	 */
	public void touchResponse(int x, int y) {
		
		if (mCurrentTurn == OthelloEngine.WHITE_DISC)
		{
			OthelloEngine.BoardSquare position = FindPosition(x, y);
			if (position != null)
			{				
				if (mBoard.IsValidMove(mCurrentTurn, position.getrownum(), position.getcolnum()) == true)
				{
					mBoard.placeDisc(mCurrentTurn, position.getrownum(), position.getcolnum());
					DrawBoard();
					mMoveMade = true;					
				} else {
					Log.i("debug", "Invalid move");
				}
			}
		}

	}	

	/*
	 * Get the square where the user tap
	 */
	private OthelloEngine.BoardSquare FindPosition(int x, int y) {
		
		if (x >= mCoordinates[0][0].getBoardCoordinate(0).x && y > mCoordinates[0][0].getBoardCoordinate(0).y 
				&& x <= mCoordinates[7][7].getBoardCoordinate(2).x && y <= mCoordinates[7][7].getBoardCoordinate(2).y )
		{
			int squarewidth = mCoordinates[0][0].getBoardCoordinate(1).x - mCoordinates[0][0].getBoardCoordinate(0).x;
			int squareheight = mCoordinates[0][0].getBoardCoordinate(3).y - mCoordinates[0][0].getBoardCoordinate(0).y;
			
			int i = y/squareheight;
			int j = x/squarewidth;
			
			return mBoard.new BoardSquare(i, j);
		}
			
		return null;
	}

	/*
	 * Update the board after each turn
	 */
	private void DrawBoard() {
		Canvas canvas = null;
		try {

			canvas = this.getHolder().lockCanvas(null);
			synchronized (this.getHolder()) {
				doDraw(canvas);				
				drawContent(canvas);
				updateScores(canvas);				
			}
		} finally {

			if (canvas != null) {
				this.getHolder().unlockCanvasAndPost(canvas);

			}
		}
	}
	
	/*
	 * New Game
	 */
	public void newGame() {
		mAI.endAI(true);
		mIsNewGame = true;
		initializeBoard();
		mBoard.newGame();
		GetGameSetting();
		mCurrentTurn = OthelloEngine.WHITE_DISC;
		mIsEndGame = false;
		mWinner = "";
		DrawBoard();
		if (!mOthelloThread.isAlive())
		{
			mOthelloThread.start();
		}		
	}
	
	/*
	 * Get Game Setting From SharePreference
	 */
	private void GetGameSetting()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mOthelloMainActivity);
		mUserName = prefs.getString("username", "Gamer");
		mGameLevel = prefs.getString("gamelevel", "Beginner");
	}

	/*
	 * Save Highest score to SharedPreference
	 */
	private void SaveHighScore()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mOthelloMainActivity);
		int highScore = prefs.getInt("high_score", 0);
		int currentScore = mBoard.getWhiteCount();
		if ( currentScore > highScore ) {
			SharedPreferences.Editor prefEditor = prefs.edit();
			prefEditor.putString("high_user", mUserName);
			prefEditor.putString("high_level", mGameLevel);
			prefEditor.putInt("high_score", mBoard.getWhiteCount());
			prefEditor.apply();
			mOthelloMainActivity.setHighScoreInd(true);
		}
	}
}
