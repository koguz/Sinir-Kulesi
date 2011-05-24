package org.kuzeykutbu.java;

/**
 * Bu sınıf hücreleri tutmak için kullanılıyor...
 * 
 * @author kaya
 */
public class Cell
{
    public int x, y;
    public boolean inUse;
    /**
     * Cell constructor
     */
    public Cell()
    {
        x = 0;
        y = 0;
        inUse = true;
    }
    /**
     * Cell constr. w/ parameters
     * @param a x val
     * @param b y val
     */
    public Cell(int a, int b)
    {
        inUse = true;
        setXY(a, b);
    }
    /**
     * X ve Y değerlerini değiştirmek için kullanılıyor
     * @param a x değişkeni için
     * @param b y değişkeni için
     */
    public void setXY(int a, int b)
    {
        x = a; y = b;
    }

    /**
     * Hücreyi taşıma/oynatma için kullanılıyor
     * @param dir true ise sağa, false ise sola doğru
     */
    public void move(boolean dir)
    {
        if(dir)
        {
            // sağa
            x = Math.min(8, x+1);
        }
        else
        {
            x = Math.max(0, x-1);
        }
    }
}
