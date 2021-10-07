import com.bzz.M306.Controller.FileHandler;
import com.bzz.M306.Data.Data;
import com.bzz.M306.Data.EnergyData;
import com.bzz.M306.View.GUIVerbrauchsdiagramm;
import org.junit.*;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * @author Malo Jaboulet
 * @version 1.0
 * @since 07.10.2021
 * <p>
 * JUnit-Klasse
 * Hier werden die JUnit-Test ausgeführt.
 */
public class Testing {

    // Anfang Attribute

    // Ende Attribute
    private static FileHandler fileHandler;
    private static GUIVerbrauchsdiagramm gui;
    // Anfang Methoden


    @Before
    public void setUp() {
        fileHandler = FileHandler.getFileHandler();
        gui = new GUIVerbrauchsdiagramm("Test", new EnergyData());
    }

    /**
     * Testet, ob die Daten richtig aus den Files gelesen werden und in die TreeMap gespeichert werden.
     */
    @Test
    public void readSDATFile() {
        fileHandler.readSDAT();
        int index = 0;
        double result = 1;
        for (Map.Entry<Long, Data> entry : fileHandler.getSdatData().entrySet()) {
            if (index == 3) {
                result = entry.getValue().getRelativBezug();
                break;
            }
            index++;
        }
        assertEquals(0.6, result, 0.0);
    }

    /**
     * Testet, ob bei einem gültigen Datum das Textfeld leer bleibt.
     */
    @Test
    public void gueltigesDatum() {
        String datum = "09.09.2020";
        String resultat = "";
        JButton button = new JButton();

        gui.gettDate().setText(datum);
        gui.checkDate(new KeyEvent(button, 1, 20, 1, KeyEvent.VK_ENTER));


        resultat = gui.getlEmpty2().getText();

        assertEquals("", resultat);
    }

    /**
     * Testet, ob bei einem falschen Datum eine Fehlermeldung angezeigt wird.
     */
    @Test
    public void falschesDatum() {
        String datum = "09.09.200";
        String resultat = "";
        JButton button = new JButton();
        gui.gettDate().setText(datum);

        gui.checkDate(new KeyEvent(button, 1, 20, 1, KeyEvent.VK_ENTER));

        resultat = gui.getlEmpty2().getText();

        assertEquals("Es wurde eine flasches Datum eingegeben.", resultat);
    }

    // Ende Methoden
}
