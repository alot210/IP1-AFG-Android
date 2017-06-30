package de.hsd.manguli.fractalsapp.models.math;


import android.graphics.Color;

/**
 * Oberklasse mit Algorithmus fuer Julia und Mandelbrot
 */
public class Algorithm {

    //Default Konstruktor
    public Algorithm() {
    }

    ;
    //Fenstergröße
    private int width;
    private int heigth;

    //Default-Werte
    private int maxIteration = 150;
    private int color1 = Color.YELLOW, color2 = Color.CYAN, color3 = Color.MAGENTA, color4 = Color.BLUE;
    //Array mit interpolierten Farben
    public int[] palette = new int[maxIteration];

    public int getWidth() {
        return this.width;
    }

    public int getHeigth() {
        return this.heigth;
    }

    public int getMaxIteration() {
        return this.maxIteration;
    }

    public void setWidth(int _width) {
        this.width = _width;
    }

    public void setHeigth(int _height) {
        this.heigth = _height;
    }

    public void setMaxIteration(int _m) {
        this.maxIteration = _m;
    }
    public int getColor1() {
        return color1;
    }

    public void setColor1(int color1) {
        this.color1 = color1;
    }

    public int getColor2() {
        return color2;
    }

    public void setColor2(int color2) {
        this.color2 = color2;
    }

    public int getColor3() {
        return color3;
    }

    public void setColor3(int color3) {
        this.color3 = color3;
    }

    public int getColor4() {
        return color4;
    }

    public void setColor4(int color4) {
        this.color4 = color4;
    }

    //Mittelpunkt des Bildausschnitts berechnen:
    //spaeter beim Verschieben werden diese Werte dynamisch geändert

    //Die Mandelbrot-Menge wird im Fenster bei w/2 und (3/4)*h zentriert
    Complex translate;
    Complex scale;

    //Methode wird nur von Julia-Menge überschrieben
    //Methode überprüft für jede übergebene Komplexe Zahl Zugehörigkeit zur Menge
    public int isElemOfMand(Complex z0, int max){

        Complex z = z0;

        for (int n = 0; n < max; n++) {   //n Zahl der Iterationen

            if (z.pythagoras() > 4.0) {  //|Zn| > 2    => |Zn| = sqrt(x^2+y^2), dann ist Zahl C ausserhalb der Menge
                //setColor(n)
                int color =  0;
                if (n < maxIteration / 3) {
                    color = interpolate(color2, color3, n);
                } else if (n < maxIteration *2/ 3) {
                    color = interpolate(color3, color4, n);
                } else{
                    color = interpolate(color4, color2, n);
                }
                return color;
            }
            //Berechnung der Folge z' = z*z + z0;
            z = z.mult(z).add(z0);
        }
        return color1;  //|Zn| < 2, Farbe ist schwarz
    }

    //Methode transformiert übergebenen Punkt (= Pixel auf Screen)
    //ruft isElemOfMand auf und gibt eine Farbe zurück
    public int setColor(int i, int j) {

        //transalte und scale und mirror: a=i, b=j a' = i*sx * x - tx & b' = j*sy* y - ty

        //Int i, j als Complexe Zahl im Verhältnis zur Skalierung des Screens
        Complex c = new Complex((double) i / this.width, (double) j / this.heigth);
        //scale
        c = c.scale(scale.getReal(), scale.getImag());

        // mirror and translate
        c = c.translate(-translate.getReal(), -translate.getImag());

        //überprüfen ob Punkt in Menge liegt und dem entsprechend Farbe zurückgeben
        return isElemOfMand(c, maxIteration);
    }




    void fillColor() {
        System.out.println("fillColor()");
        for (int i = 0; i < maxIteration; i++) {
            if (i < maxIteration / 4) {
                palette[i] = interpolate(color1, color2, i);
            } else if (i < maxIteration / 2) {
                palette[i] = interpolate(color2, color3, i);
            } else if (i < maxIteration * 3 / 4) {
                palette[i] = interpolate(color3, color4, i);
            } else {
                palette[i] = interpolate(color3, color4, i);
            }

        }
    }

    public int interpolate(int v1, int v2, int i) {
        int r = Math.abs(Color.red(v1) + i * (Color.red(v2) - Color.red(v1)) / maxIteration);
        int g = Math.abs(Color.green(v1) + i * (Color.green(v2) - Color.green(v1)) / maxIteration);
        int b = Math.abs(Color.blue(v1) + i * (Color.blue(v2) - Color.blue(v1)) / maxIteration);
        return Color.rgb(r, g, b);
    }
}


