package org.kuzeykutbu.java;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * Ana Midlet Sınıfı
 * @author kaya
 */
public class SinirKulesi extends MIDlet implements CommandListener
{
    Ekran ekran;
    List anaMenu;
    Form yardimEkrani;
    Form hakkindaEkrani;
    
    protected void startApp() throws MIDletStateChangeException
    {
        if(anaMenu == null)
        {
            String[] elements = {"Yeni Oyun","Yardım","Hakkında", "Kapat"}; //Menu items as List elements
            anaMenu = new List("Ana Menü", List.IMPLICIT, elements, null);
            Command selectCommand = new Command("Seç", Command.ITEM, 1);
            anaMenu.setSelectCommand(selectCommand);
            anaMenu.setCommandListener(this);

            yardimEkrani = new Form("Yardım");
            StringItem metin = new StringItem("", "Amaç sağdan sola giden " +
                    "kutuları üst üste getirip, en yukarıya ulaşmak. " + 
                    "Kutucuklar diğer kutuların üstüne gelmeli. " +
                    "Denk gelmeyen kutuları kaybedersiniz. Eğer hiç kutunuz " +
                    "kalmazsa, oyun biter. Belli yüksekliklere ulaşınca kutularınız " +
                    "azalır. Puanlama, oyunu bitirdiğiniz süre ve kaçırdığınız " +
                    "kutu sayılarına göre hesaplanır.");
            yardimEkrani.append(metin);
            Command yardimKapa = new Command("Geri", Command.BACK, 1);
            yardimEkrani.addCommand(yardimKapa);
            yardimEkrani.setCommandListener(this);

            hakkindaEkrani = new Form("Hakkında");
            StringItem m2 = new StringItem("", "Kuzey Kutbu, 2009 - Kaya Oğuz -- " +
                    "Paralarımızı yutan o makinenin anısına :) ");
            hakkindaEkrani.append(m2);
            Command hkapa = new Command("Geri", Command.BACK, 1);
            hakkindaEkrani.addCommand(hkapa);
            hakkindaEkrani.setCommandListener(this);
        }
        Display.getDisplay(this).setCurrent(anaMenu);
    }

    protected void pauseApp()
    {
        // nothing here
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException
    {
        if(ekran != null)
            ekran.quit();
    }

    private void quitApp()
    {
        try
        {
            destroyApp(true);
            notifyDestroyed();
        }
        catch (Exception e) {}
    }

    public void commandAction(Command c, Displayable d)
    {
        if(c.getCommandType() == Command.EXIT)
        {
            quitApp();
        }
        else if ( d.getTitle().compareTo("Ana Menü") == 0 )
        {
            // casting
            List temp = (List) d;
            int s = temp.getSelectedIndex();
            switch(s)
            {
                case 0:
                    if(ekran == null)
                    {
                        ekran = new Ekran();
                        Command geri = new Command("Geri", Command.BACK, 1);
                        Command tekrar = new Command("Yeni", Command.STOP, 1);
                        ekran.addCommand(tekrar);
                        ekran.addCommand(geri);
                        ekran.setCommandListener(this);
                    }
                    else
                        ekran.reset();
                    
                    ekran.start();
                    Display.getDisplay(this).setCurrent(ekran);
                    break;
                case 1:
                    Display.getDisplay(this).setCurrent(yardimEkrani);
                    break;
                case 2:
                    Display.getDisplay(this).setCurrent(hakkindaEkrani);
                    break;
                case 3:
                    quitApp();
                    break;
            }
        }
        else if ( c.getCommandType() == Command.BACK )
        {
            Display.getDisplay(this).setCurrent(anaMenu);
        }
        else if (c.getLabel().compareTo("Yeni") == 0)
        {
            ekran.reset();
        }
    }
    
}

