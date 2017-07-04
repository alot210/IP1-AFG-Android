package de.hsd.manguli.fractalsapp.models.math;

import de.hsd.manguli.fractalsapp.models.math.Algorithm;
import de.hsd.manguli.fractalsapp.models.math.Complex;

import android.graphics.Color;

/**
 * Created by Jens on 09.06.2017.
 */

public class Julia extends Algorithm {

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
    public int isElemOfMand(Complex z0){

        Complex z = z0;

        for (int n = 0; n < getMaxIteration(); n++) {   //n Zahl der Iterationen


            if (z.pythagoras() > 4.0) {  //|Zn| > 2    => |Zn| = sqrt(x^2+y^2), dann ist Zahl C ausserhalb der Menge
                //setColor(n)
                int color =  0;
                if (n < getMaxIteration() / 3) {
                    color = interpolate(getColor2(), getColor3(), n);
                } else if (n < getMaxIteration() *2/ 4) {
                    color = interpolate(getColor3(), getColor4(), n);
                } else{
                    color = interpolate(getColor4(), getColor2(), n);
                }
                return color;
            }
            //hier Konstante statt z0 addieren
            z = z.mult(z).add(constant);
        }
        return getColor1();  //|Zn| < 2, Farbe ist schwarz
    }
}