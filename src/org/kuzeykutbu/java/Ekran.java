package org.kuzeykutbu.java;

import java.util.Random;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

/**
 * Ana oyun ekranı, oyun burada yer alıyor
 * @author kaya
 */
public class Ekran extends GameCanvas implements Runnable
{
    private int ssize;
    private long frameDuration;
    private boolean gameRunning;
    private int bw, bh, bx, by; // border values
    private boolean control;
    private boolean direction;
    private boolean gameOver;
    private boolean isWinner;
    private boolean flashing;
    private boolean flashControl;
    private long flashStart;
    private int currentLevel;
    private int sqcount;
    private int points;
    private long gameStartTime;
    private Thread t;
    private static final int WHITE = 0xffffff;
    private static final int BLUE = 0x336699;
    private static final int BLACK = 0x000000;
    private static final int GRAY = 0xeeeeee;
    private int alan[][];
    private Cell squares[];
    
    public Ekran()
    {
        super(true);
        setTitle("Sinir Kulesi");
        int w = getWidth();
        int h = getHeight();
        // try height first, make sure it fits
        ssize = h / 14;
        if (ssize*9 > w)
        {
            do
            {
                ssize--;
            }
            while(ssize*9 > w);
        }
        frameDuration = 50;  // 20 frames per second

        bw = (ssize *  9) + 2;
        bh = (ssize * 14) + 2;
        bx = (getWidth() - bw) / 2;
        by = (getHeight() - bh) / 2;

        control = true;
        currentLevel = 0;

        alan = new int[9][14];
        for(int i=0;i<9;i++)
            for(int j=0;j<14;j++)
                alan[i][j] = 0;

        runLevel(currentLevel, 3);
        gameOver = false;
        isWinner = false;
        flashing = false;
        flashStart = 0;
        flashControl = false;
        sqcount = 0;
        points = 0;
        gameStartTime = System.currentTimeMillis();
        t = null;
        
    }

    public void reset()
    {
        
        for(int i=0;i<9;i++)
            for(int j=0;j<14;j++)
                alan[i][j] = 0;
        currentLevel = 0;
        control = true;
        gameOver = false;
        isWinner = false;
        flashing = false;
        flashStart = 0;
        flashControl = false;
        sqcount = 0;
        points = 0;
        gameStartTime = System.currentTimeMillis();
        runLevel(currentLevel, 3);
    }

    private void runLevel(int l, int num)
    {
        squares = null;
        System.gc();
        squares = new Cell[num];
        Random gen = new Random();
        int rx = gen.nextInt(9-num);
        int rd = gen.nextInt(2);
        for(int i=0;i<num;i++)
        {
            squares[i] = new Cell(8-rx-i, 13-l);
        }
        if(rd == 0)
            direction = false;
        else direction = true;
    }

    public void start()
    {
        gameRunning = true;
        if(t == null)
        {
            t = new Thread(this);
            t.start();
        }
    }

    public void stop()
    {
        gameRunning = false;
    }

    public void quit()
    {
        stop();/*
        try { Thread.currentThread().join(); }
        catch(InterruptedException ex) {} */
    }

    private void localWait(long d)
    {
        try { Thread.sleep(d); }
        catch (InterruptedException ex) { stop(); }
    }

    public void run()
    {
        Graphics g = getGraphics();
        while(gameRunning)
        {
            long basla = System.currentTimeMillis();

            render(g);
            gameLogic();
            checkKeys();
            

            long son = System.currentTimeMillis();
            int duration = (int)(son - basla);

            if (duration < frameDuration)
            {
                localWait(frameDuration - duration);
            }
        }
    }

    private void gameLogic()
    {
        if(flashing||gameOver) return;
        if(currentLevel < 4) { localWait(100); }
        else if (currentLevel < 7) { localWait(95); }
        else if (currentLevel < 10) { localWait(90); }
        else if (currentLevel < 13) { localWait(84); }
        
        for(int i=0;i<squares.length;i++)
        {
            if ((squares[i].x == 0 && !direction) || (squares[i].x == 8 && direction))
                direction = !direction;
        }
        for(int i=0;i<squares.length;i++) squares[i].move(direction);
    }

    private void placeSquares()
    {
        for(int i=0;i<squares.length;i++)
        {
            if(squares[i].inUse)
                alan[squares[i].x][squares[i].y] = 1;
        }
    }

    private void moveUp()
    {
        if(gameOver || flashing)
            return;
        // check if everything is OK
        // sqcount = 0;
        if (currentLevel < 14 && currentLevel > 0 && !flashControl)
        {
            // karelerin bir altlarını kontrol et
            sqcount = 0;
            for(int i=0;i<squares.length;i++)
            {
                int a = squares[i].x;
                int b = squares[i].y + 1;
                if(alan[a][b] == 1)
                {
                    sqcount++;
                }
                else
                {
                    flashing = true;
                    squares[i].inUse = false;
                    points -= 14/(currentLevel+1);
                }
            }

            if(sqcount == 0)
            {
                // game over
                gameOver = true;
                return;
            }
            if(currentLevel == 13 && sqcount > 0)
            {
                // winner
                long totalTime = (System.currentTimeMillis() - gameStartTime)/1000;
                points += 400 / (int)totalTime;
                gameOver = true;
                isWinner = true;
                return;
            }
        }
        if(flashing)
        {
            flashStart = System.currentTimeMillis();
            flashControl = true;
            return;
        }
        if(!flashing && flashControl)
            flashControl = false;

        if (currentLevel == 0)
            sqcount = 3;
        else if (currentLevel == 4 && sqcount == 3)
            sqcount = 2;
        else if (currentLevel == 7 && sqcount == 2)
            sqcount = 1;
        placeSquares();
        currentLevel++;
        runLevel(currentLevel, sqcount);
    }

    private void checkKeys()
    {
        int keyState = getKeyStates();
        if((keyState & FIRE_PRESSED) != 0 && control)
        {
            moveUp();
            control = false;
        }
        else if ((keyState & FIRE_PRESSED) == 0) control = true;
        if(flashing && ( System.currentTimeMillis() - flashStart > 1000 ) )
        {
            flashing = false;
            moveUp();
        }
    }

    private void render(Graphics g)
    {
        // CLS
        g.setColor(WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // draw borders
        g.setColor(BLACK);
        g.drawRect(bx, by, bw, bh);
        g.setColor(GRAY);
        g.fillRect(bx+1, by+1, bw-1, ssize);
        g.fillRect(bx+1, (4*ssize)+5, bw-1, ssize);

        for(int i=0;i<9;i++)
        {
            for(int j=0;j<14;j++)
            {
                if(alan[i][j] == 1)
                    renderSquare(g, i, j);
            }
        }

        
        for(int i=0;i<squares.length;i++)
        {
            if(squares[i].inUse)
            {
                renderSquare(g, squares[i].x, squares[i].y);
            }
            if(flashing && !squares[i].inUse) 
            {
                renderSquare(g, squares[i].x, squares[i].y, BLACK);
            }
        }

        if (gameOver)
        {
            for(int i=0;i<squares.length;i++)
                renderSquare(g, squares[i].x, squares[i].y, BLACK);
            int w = getWidth();
            int h = getHeight();
            Font font = g.getFont();
            int fontHeight = font.getHeight();
            int fontWidth;
            String son;
            if(isWinner)
                son = "KAZANDINIZ! PUAN: " + String.valueOf(points);
            else son = "KAYBETTİNİZ!";
            fontWidth = font.stringWidth(son);
            g.setColor(BLACK);
            g.fillRect((w-fontWidth)/2, (h-fontHeight)/2, fontWidth+2, fontHeight);
            g.setColor(WHITE);
            g.setFont(font);
            g.drawString(son, (w-fontWidth)/2, (h-fontHeight)/2, g.TOP|g.LEFT);
            
        }

        flushGraphics();
    }

    private void renderSquare(Graphics g, int i, int j, int C)
    {
        g.setColor(C);
        g.fillRect((i*ssize) + bx + 1, (j*ssize) + by + 1, ssize, ssize);
        g.setColor(WHITE);
        g.drawRect((i*ssize) + bx + 1, (j*ssize) + by + 1, ssize, ssize);
    }

    private void renderSquare(Graphics g, int i, int j)
    {
        renderSquare(g, i, j, BLUE);
    }
    
}


