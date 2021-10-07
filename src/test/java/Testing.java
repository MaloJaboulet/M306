import com.bzz.M306.Controller.FileHandler;
import com.bzz.M306.Data.Data;
import com.bzz.M306.View.GUIVerbrauchsdiagramm;
import org.junit.*;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Map;

import static org.junit.Assert.* ;


public class Testing {

    // Anfang Attribute

    // Ende Attribute
    private static FileHandler fileHandler;
    private static GUIVerbrauchsdiagramm gui;
    // Anfang Methoden


    @Before
    public void setUp() {
        fileHandler = FileHandler.getFileHandler();
        gui = new GUIVerbrauchsdiagramm("Test", null);
    }

    @Test
    public void readSDATFile(){
        fileHandler.readSDAT();
        int index = 0;
        double result = 1;
        for (Map.Entry<Long, Data> entry:fileHandler.getSdatData().entrySet()) {
            if (index == 3){
                result = entry.getValue().getRelativBezug();
                break;
            }
            index++;
        }
         assertEquals(0.6,result,0.0);
    }

    @Test
    public void gueltigesDatum(){
        String datum = "09.09.2020";
        String resultat = "";
        JButton button = new JButton();

        //gui.checkDate(new KeyEvent(button,1,20,1,10,"0x0A"));

    }


    public static void main(String[] args) {

    }

    // Ende Methoden
}
