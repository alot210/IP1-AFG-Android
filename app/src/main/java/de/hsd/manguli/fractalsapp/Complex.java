package de.hsd.manguli.fractalsapp;


/**
 * Complex Klasse für den eigenen Datentyp Complex
 */

public class Complex {
    //Realer Anteil
    private double real;
    //Imaginärer Anteil
    private double imag;

    //Constructor
    public Complex (double _real, double _imag){
        this.real = _real;
        this.imag = _imag;
    }

    /*
    getter Methoden für die Anteile der komplexen Zahl
    @return der real Anteil
    @return der imganinäre Anteil
     */
    public double getReal(){
        return this.real;
    }
    public double getImag(){
        return this.imag;
    }

    public void setReal(double real){
        this.real = real;
    }
    public void setImag(double imag){
        this.imag = imag;
    }

    /*
    Addition zweier Komplexen Zahlen
    @param die zweite komplexe Zahl
    @return die addierte neue komplexe Zahl
     */
    public Complex add(Complex b){
        double _real = this.real + b.real;
        double _imag = this.imag + b.imag;

        return new Complex(_real, _imag);
    }

    /*
    Mulitplikation zweier komplexer Zahlen
    @param die zweite zu addierende Zahl
    @return die summierte neue komplexe Zahl
     */
    public Complex mult(Complex b){
        double _real = this.real * b.real - this.imag * b.imag;
        double _imag = this.real * b.imag + this.imag * b.real;

        return new Complex(_real, _imag);
    }

    // Transformation einer Komplexen Zahl im Koordinatensystem
    public Complex scale(double sx, double sy){
        double _real = this.real * sx;
        double _imag = this.imag * sy;
        return new Complex(_real, _imag);
    }

    public Complex translate(double tx, double ty) {
        double _real = this.real + tx;
        double _imag = this.imag + ty;
        return new Complex(_real, _imag);
    }

    /*
    Berechnung des Betrags der komplexen Zahl
    @return der Betrag
     */
    public double abs(){
        return Math.sqrt(this.real * this.real + this.imag * this.imag);
    }

    public double pythagoras(){
        return (this.real * this.real + this.imag * this.imag);
    }

    /*
    Ausgabe einer Komplexen Zahl
    @return die komplexe Zahl als String
     */
    public String complexToString(){
        return "Realteil: "+ this.real +" Imaginärteil: "+ this.imag;
    }
}

