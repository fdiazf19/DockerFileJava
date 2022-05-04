package com.proyecto.iasi;

public class Casilla {

	public static final int TIPO_LIBRE = 0;
	public static final int TIPO_MURO = 1;
	public static final int TIPO_PIEZA = 2;
	public static final int TIPO_OBJETIVO = 3;

	private int coordX;
	private int coordY;

	private int tipo;// tipo 0: libre, tipo 1: muro, tipo 2: pieza, tipo 3: objetivo.

	private int h = 0;

	public Casilla(int coordX, int coordY, int tipo) {
		super();
		this.coordX = coordX;
		this.coordY = coordY;
		this.tipo = tipo;
	}

	public Casilla(int coordX, int coordY, int tipo, int h) {
		super();
		this.coordX = coordX;
		this.coordY = coordY;
		this.tipo = tipo;
		this.h = h;
	}

	public boolean esObjetivo() {
		return this.tipo == 3;
	}

	public boolean esPieza() {
		return this.tipo == 2;
	}

	public boolean esMuro() {
		return this.tipo == 1;
	}

	public boolean esLibre() {
		return this.tipo == 0 || this.tipo == 3;
	}

	public int getCoordX() {
		return coordX;
	}

	public int getCoordY() {
		return coordY;
	}

	public int getH() {
		return h;
	}

	public int getTipo() {
		return tipo;
	}

	public void setCoordX(int coordX) {
		this.coordX = coordX;
	}

	public void setCoordY(int coordY) {
		this.coordY = coordY;
	}

	public void setH(int h) {
		this.h = h;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + coordX;
		result = prime * result + coordY;
		result = prime * result + tipo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Casilla other = (Casilla) obj;
		if (coordX != other.coordX)
			return false;
		if (coordY != other.coordY)
			return false;
		if (tipo != other.tipo)
			return false;
		return true;
	}

}
