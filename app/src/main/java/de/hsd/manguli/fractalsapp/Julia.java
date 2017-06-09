package de.hsd.manguli.fractalsapp;

/**
 * Created by Jens on 09.06.2017.
 */

public class Julia extends Algorithm{

    //Konstante für Julia-Menge
    private static Complex constant;

    //Konstruktor
    public Julia(int width, int heigth, int maxIteration, Complex translate, Complex scale, Complex constant){
        super.setWidth(width);
        super.setHeigth(heigth);
        super.setMaxIteration(maxIteration);
        super.translate = translate;
        super.scale = scale;
        this.constant = constant;
    }
    //Methode von Algorithm überschreiben
    public Boolean isElemOfMand(Complex z0, int max){

        Complex z = z0;

        for (int n = 0; n < max; n++) {   //n Zahl der Iterationen


            if (z.pythagoras() > 4.0) {  //|Zn| > 2    => |Zn| = sqrt(x^2+y^2), dann ist Zahl C ausserhalb der Menge
                //setColor(n)
                return false;
            }
            //hier Konstante statt z0 addieren
            z = z.mult(z).add(constant);
        }
        return true;  //|Zn| < 2, Farbe ist schwarz
    }
}
