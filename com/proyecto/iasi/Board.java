package com.proyecto.iasi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

	private final int pieceSize = 4;

	private int sizeX;
	private int sizeY;

	private Casilla boxes[][];

	private Casilla piece[];

	private Casilla objetivo[];

	// Hace referencia a que casilla dentro del array pieza es el vertice de la
	// pieza.
	private int pieceVertex;

	// Hace referencia a que casilla dentro del array pieza es el vertice de la
	// pieza.
	private int objetiveVertex;

	// Guarda el ultimo movimiento que se haya realizado sobre el tablero
	private String mov;

	// Referencia al tablero anterior para el algoritmo Primero Mejor
	private Board prevBoard;

	// Lista de posibles movimientos sobre un Tablero
	private List<String> avaibleMov;

	// Orientacion actual de la pieza
	private int orient;
	
	//Funcion de coste de creacion de nodo (Solo utilizado en A*)
	private int g;

	public Board() {
		this.sizeX = 10;
		this.sizeY = 10;
		this.boxes = new Casilla[this.sizeX][this.sizeY];
		this.piece = new Casilla[pieceSize];
		this.objetivo = new Casilla[pieceSize];
		this.avaibleMov = null;
		this.mov = "";
		this.prevBoard = null;
		this.g = 0;
	}

	public Board(int tamX, int tamY) {
		this.sizeX = tamX;
		this.sizeY = tamY;
		this.boxes = new Casilla[this.sizeX][this.sizeY];
		this.piece = new Casilla[pieceSize];
		this.objetivo = new Casilla[pieceSize];
		this.avaibleMov = null;
		this.mov = "";
		this.prevBoard = null;
		this.g = 0;
	}

	/*
	 * Constructor para hacer copias de tableros.
	 */
	public Board(Board tab) {
		// Copiamos las casillas
		for (int i = 0; i < tab.sizeX; i++) {
			for (int j = 0; j < tab.sizeY; j++) {
				this.setBox(i, j, tab.getBox(i, j).getTipo(), tab.getBox(i, j).getH());

			}
		}

		// Copiamos la Pieza
		for (int i = 0; i < tab.pieceSize; i++) {
			this.getPiece()[i] = new Casilla(tab.piece[i].getCoordX(), tab.piece[i].getCoordY(), 2,
					tab.piece[i].getH());
		}

		this.setPieceVertex(tab.pieceVertex);

		// Copiamos el Objetivo
		for (int i = 0; i < tab.pieceSize; i++) {
			this.getObjetive()[i] = new Casilla(tab.objetivo[i].getCoordX(), tab.objetivo[i].getCoordY(), 3,
					tab.objetivo[i].getH());
		}

		this.setObjetiveVertex(tab.objetiveVertex);
		
		this.setG(tab.g);

		// Guardamos la referencia en tab del tablero this
		this.setTabAnt(tab);
		
	}

	/*
	 * Busca la pieza objetivo y guarda la referencia de sus casillas en el Array
	 * objetivo.
	 */
	public void findObjetive() {
		int cont = 0;
		for (int i = 0; (i < sizeX) && (cont < 4); i++) {
			for (int j = 0; (j < sizeY) && (cont < 4); j++) {
				if (this.boxes[i][j].esObjetivo()) {
					this.objetivo[cont] = new Casilla(i, j, 3);
					cont++;
				}
			}
		}
		loadObjetiveVertex();
	}

	/*
	 * Busca la pieza y guarda la referencia de sus casillas en el Array pieza.
	 */
	public void findPiece() {
		int cont = 0;
		for (int i = 0; (i < sizeX) && (cont < 4); i++) {
			for (int j = 0; (j < sizeY) && (cont < 4); j++) {
				if (this.boxes[i][j].esPieza()) {
					this.piece[cont] = new Casilla(i, j, 2);
					cont++;
				}
			}
		}
		loadPieceVertex();
		setOrient();
	}

	/*
	 * Busca el indice del Array Objetivo donde se encuentra el vertice.
	 */
	public void loadObjetiveVertex() {
		this.objetiveVertex = -1;
		for (int i = 0; (i < pieceSize) && (objetiveVertex == -1); i++) {
			if (boxesObjetiveAdjacent(objetivo[i]) == 2)
				this.objetiveVertex = i;
		}
	}

	/*
	 * Busca el indice del Array Pieza donde se encuentra el vertice.
	 */
	public void loadPieceVertex() {
		this.pieceVertex = -1;
		for (int i = 0; (i < pieceSize) && (pieceVertex == -1); i++) {
			if (casillasPiezaColindantes(piece[i]) == 2)
				this.pieceVertex = i;
		}
	}

	/*
	 * Retorna el numero de Casillas Objetivo colindantes a una dada, siempre y
	 * cuando no se encuentren dentro del mismo eje.
	 */
	private int boxesObjetiveAdjacent(Casilla cas) {
		int x = cas.getCoordX();
		int y = cas.getCoordY();
		int contH = 0;
		int contY = 0;
		Casilla aux;

		if ((x - 1) > 1) {
			aux = this.boxes[x - 1][y];
			if (aux.esObjetivo())
				contH++;
		}
		if ((x + 1) < (sizeX - 1)) {
			aux = this.boxes[x + 1][y];
			if (aux.esObjetivo())
				contH++;
		}
		if ((y - 1) > 1) {
			aux = this.boxes[x][y - 1];
			if (aux.esObjetivo())
				contY++;
		}
		if ((y + 1) < (sizeY - 1)) {
			aux = this.boxes[x][y + 1];
			if (aux.esObjetivo())
				contY++;
		}
		if (contH > 0 && contY > 0)
			return 2;
		else
			return 1;
	}

	/*
	 * Retorna el numero de Casillas Pieza colindantes a una dada, siempre y cuando
	 * no se encuentren dentro del mismo eje.
	 */
	private int casillasPiezaColindantes(Casilla cas) {
		int x = cas.getCoordX();
		int y = cas.getCoordY();
		int contH = 0;
		int contY = 0;
		Casilla aux;

		aux = this.boxes[x - 1][y];
		if (aux.esPieza())
			contH++;

		aux = this.boxes[x + 1][y];
		if (aux.esPieza())
			contH++;

		aux = this.boxes[x][y - 1];
		if (aux.esPieza())
			contY++;

		aux = this.boxes[x][y + 1];
		if (aux.esPieza())
			contY++;

		if (contH > 0 && contY > 0)
			return 2;
		else
			return 1;
	}

	public Board copyBoard() {

		Board tab = new Board(this.sizeX, this.sizeY);

		// Copiamos las casillas
		for (int i = 0; i < this.sizeX; i++) {
			for (int j = 0; j < this.sizeY; j++) {
				tab.setBox(i, j, this.getBox(i, j).getTipo(), this.getBox(i, j).getH());
			}
		}

		// Copiamos la Pieza
		for (int i = 0; i < this.pieceSize; i++) {
			tab.getPiece()[i] = new Casilla(this.piece[i].getCoordX(), this.piece[i].getCoordY(), 2,
					this.piece[i].getH());
		}

		tab.loadPieceVertex();

		// Copiamos el Objetivo
		for (int i = 0; i < this.pieceSize; i++) {
			tab.getObjetive()[i] = new Casilla(this.objetivo[i].getCoordX(), this.objetivo[i].getCoordY(), 3,
					this.objetivo[i].getH());
		}

		tab.setObjetiveVertex(this.objetiveVertex);

		// Guardamos la referencia en tab del tablero this
		tab.setTabAnt(this);

		return tab;
	}

	/*
	 * Copia el tablero actual en el que recibe por parametro
	 */
	public void copyBoard(Board tab) {

		// Copiamos las casillas
		for (int i = 0; i < this.sizeX; i++) {
			for (int j = 0; j < this.sizeY; j++) {
				tab.setBox(i, j, this.getBox(i, j).getTipo(), this.getBox(i, j).getH());

			}
		}

		// Copiamos la Pieza
		for (int i = 0; i < this.pieceSize; i++) {
			tab.getPiece()[i] = new Casilla(this.piece[i].getCoordX(), this.piece[i].getCoordY(), 2,
					this.piece[i].getH());
		}

		tab.setPieceVertex(this.pieceVertex);

		// Copiamos el Objetivo
		for (int i = 0; i < this.pieceSize; i++) {
			tab.getObjetive()[i] = new Casilla(this.objetivo[i].getCoordX(), this.objetivo[i].getCoordY(), 3,
					this.objetivo[i].getH());
		}

		tab.setObjetiveVertex(this.objetiveVertex);
		
		tab.setG(this.g);

		// Guardamos la referencia en tab del tablero this
		tab.setTabAnt(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Board other = (Board) obj;

		if (!Arrays.deepEquals(boxes, other.boxes))
			return false;
		if (!Arrays.equals(objetivo, other.objetivo))
			return false;
		if (!Arrays.equals(piece, other.piece))
			return false;

		return true;
	}

	/*
	 * Control para saber si se ha llegado al fin.
	 */
	public boolean isEnd() {
		int cont = 0;
		int x = 0;
		int y = 0;

		for (int i = 0; i < this.pieceSize; i++) {
			x = this.piece[i].getCoordX();
			y = this.piece[i].getCoordY();
			for (int j = 0; j < this.pieceSize; j++) {
				if (x == this.objetivo[j].getCoordX() && y == this.objetivo[j].getCoordY())
					cont++;
			}
		}

		if (cont == 4)
			return true;
		else
			return false;
	}

	public Casilla getBox(int x, int y) {
		return this.boxes[x][y];
	}

	public Casilla[][] getBoxes() {
		return boxes;
	}
	
	public int getActualHeuristic() {
		return getBox(this.piece[this.pieceVertex].getCoordX(), this.piece[this.pieceVertex].getCoordY()).getH();
	}

	public int getOnMovHeuristic(String dir) {
		if (dir.equals("A") || dir.equals("B") || dir.equals("D") || dir.equals("I")) {
			if (possibleMovement(dir)) {
				int x = this.piece[pieceVertex].getCoordX();
				int y = this.piece[pieceVertex].getCoordY();

				if (dir.equals("A")) {
					return getBox(x - 1, y).getH();
				}

				if (dir.equals("B")) {
					return getBox(x + 1, y).getH();
				}

				if (dir.equals("D")) {
					return getBox(x, y + 1).getH();
				}

				if (dir.equals("I")) {
					return getBox(x, y - 1).getH();
				}
			}
		} else {
			if (dir.equals("R")) {
				return this.getBox(this.piece[pieceVertex].getCoordX(), this.piece[pieceVertex].getCoordY())
						.getH();
			}
		}

		return 0;
	}

	public String getMov() {
		return mov;
	}

	public Casilla[] getObjetive() {
		return objetivo;
	}

	public int getOrient() {
		return orient;
	}

	/*
	 * Comprueba la orientacion de la pieza Objetivo
	 */
	@SuppressWarnings("unused")
	private int getObjetiveOrient() {
		int x = objetivo[this.objetiveVertex].getCoordX();
		int y = objetivo[this.objetiveVertex].getCoordY();

		// Comprobamos si Posicion Inicial
		if ((x > 1) && getBox(x - 1, y).esObjetivo() && getBox(x - 2, y).esObjetivo())
			return 0;

		// Comprobamos si 1 Rotacion
		if ((y < this.sizeY - 2) && getBox(x, y + 1).esObjetivo() && getBox(x, y + 2).esObjetivo())
			return 1;

		// Comprobamos si 2 Rotacion
		if ((x < this.sizeX - 2) && getBox(x + 1, y).esObjetivo() && getBox(x + 2, y).esObjetivo())
			return 2;

		// Comprobamos si 3 Rotacion
		if ((y > 1) && getBox(x, y - 1).esObjetivo() && getBox(x, y - 2).esObjetivo())
			return 3;

		return -1;
	}

	/*
	 * Comprueba la orientacion de la Pieza
	 */
	private int getPieceOrient() {
		int x = piece[this.pieceVertex].getCoordX();
		int y = piece[this.pieceVertex].getCoordY();

		// Comprobamos si Posicion Inicial
		if (getBox(x + 1, y).esPieza() && getBox(x + 2, y).esPieza())
			return 0;

		// Comprobamos si 1 Rotacion
		if (getBox(x, y - 1).esPieza() && getBox(x, y - 2).esPieza())
			return 1;

		// Comprobamos si 2 Rotacion
		if (getBox(x - 1, y).esPieza() && getBox(x - 2, y).esPieza())
			return 2;

		// Comprobamos si 3 Rotacion
		if (getBox(x, y + 1).esPieza() && getBox(x, y + 2).esPieza())
			return 3;

		return -1;
	}

	public Casilla[] getPiece() {
		return piece;
	}

	public List<String> getAvaibleMov() {
		this.avaibleMov = new ArrayList<String>();

		if (rotatePieceSim())
			avaibleMov.add("R");

		if (invRotatePieceSim())
			avaibleMov.add("L");

		if (moverPieceSim("A") && !this.mov.equals("B"))
			avaibleMov.add("A");

		if (moverPieceSim("B") && !this.mov.equals("A"))
			avaibleMov.add("B");

		if (moverPieceSim("I") && !this.mov.equals("D"))
			avaibleMov.add("I");

		if (moverPieceSim("D") && !this.mov.equals("I"))
			avaibleMov.add("D");

		return this.avaibleMov;
	}

	public List<String> getAvaibleMovFB() {
		this.avaibleMov = new ArrayList<String>();

		if (moverPieceSim("A"))
			avaibleMov.add("A");

		if (moverPieceSim("B"))
			avaibleMov.add("B");

		if (moverPieceSim("I"))
			avaibleMov.add("I");

		if (moverPieceSim("D"))
			avaibleMov.add("D");

		if (rotatePieceSim())
			avaibleMov.add("R");

		if (invRotatePieceSim())
			avaibleMov.add("L");

		return this.avaibleMov;
	}

	public Board getPrevBoard() {
		return prevBoard;
	}

	public int getXSize() {
		return sizeX;
	}

	public int getYSize() {
		return sizeY;
	}

	public int getObjetiveVertex() {
		return objetiveVertex;
	}

	public int getPieceVertex() {
		return pieceVertex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(boxes);
		result = prime * result + Arrays.hashCode(objetivo);
		result = prime * result + Arrays.hashCode(piece);
		result = prime * result + ((prevBoard == null) ? 0 : prevBoard.hashCode());
		result = prime * result + objetiveVertex;
		result = prime * result + pieceVertex;
		return result;
	}

	/*
	 * Mueve una casilla a otra con libertad dentro del tablero.
	 */
	private void moveAvaibleBox(int xOrig, int yOrig, int xDest, int yDest) {
		getBox(xOrig, yOrig).setTipo(Casilla.TIPO_LIBRE);
		getBox(xDest, yDest).setTipo(Casilla.TIPO_PIEZA);
	}

	/*
	 * Para mover la pieza se toma como base el vertice de la pieza A = Arriba, B =
	 * Abajo, D = Derecha, I = Izquierda
	 */
	public boolean movePiece(String dir) {
		if (dir.equals("A") || dir.equals("B") || dir.equals("D") || dir.equals("I")) {
			if (possibleMovement(dir)) {

				// Apagamos en el Tablero las casillas de la Pieza
				for (int i = 0; i < piece.length; i++) {
					getBox(piece[i].getCoordX(), piece[i].getCoordY()).setTipo(0);
				}

				// Para mover la Pieza simplemente actualizamos sus coordenadas en el Array
				// Pieza
				if (dir.equals("A")) {
					for (int i = 0; i < piece.length; i++) {
						piece[i].setCoordX(piece[i].getCoordX() - 1);
					}
				}

				if (dir.equals("B")) {
					for (int i = 0; i < piece.length; i++) {
						piece[i].setCoordX(piece[i].getCoordX() + 1);
					}
				}

				if (dir.equals("D")) {
					for (int i = 0; i < piece.length; i++) {
						piece[i].setCoordY(piece[i].getCoordY() + 1);
					}
				}

				if (dir.equals("I")) {
					for (int i = 0; i < piece.length; i++) {
						piece[i].setCoordY(piece[i].getCoordY() - 1);
					}
				}
				

				// Encendemos en el Tablero de nuevo las casillas de la Pieza
				// Controlamos tambien las casillas de la pieza objetivo
				for (int i = 0; i < piece.length; i++) {
					getBox(piece[i].getCoordX(), piece[i].getCoordY()).setTipo(2);

					if (getBox(objetivo[i].getCoordX(), objetivo[i].getCoordY()).getTipo() == 0) {
						getBox(objetivo[i].getCoordX(), objetivo[i].getCoordY()).setTipo(3);
					}
				}

				return true;
			} else
				return false;
		} else
			return false;
	}

	/*
	 * Mismo metodo que el anterior, pero efectua el movimiento sobre el tablero que
	 * le pasamos
	 */
	public boolean moverPieceSim(String dir) {

		if (dir.equals("A") || dir.equals("B") || dir.equals("D") || dir.equals("I")) {
			if (possibleMovement(dir)) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	/*
	 * Metodo que comprobara si la pieza se puede mover en la direccion indicada.
	 */
	private boolean possibleMovement(String dir) {
		int x = 0;
		int y = 0;

		if (dir.equals("A")) {// Arriba
			// Si tenemos algun muro en la parte superior no nos podremos mover
			for (int i = 0; i < piece.length; i++) {
				x = piece[i].getCoordX();
				y = piece[i].getCoordY();
				if (x == 0 || getBox(x - 1, y).esMuro())
					return false;
			}
		}

		if (dir.equals("B")) {// Abajo
			// Si tenemos algun muro en la parte inferior no nos podremos mover
			for (int i = 0; i < piece.length; i++) {
				x = piece[i].getCoordX();
				y = piece[i].getCoordY();
				if (x == this.sizeX - 1 || getBox(x + 1, y).esMuro())
					return false;
			}
		}

		if (dir.equals("D")) {// Derecha
			// Si tenemos algun muro en la parte derecha no nos podremos mover
			for (int i = 0; i < piece.length; i++) {
				x = piece[i].getCoordX();
				y = piece[i].getCoordY();
				if (y == this.sizeY - 1 || getBox(x, y + 1).esMuro())
					return false;
			}
		}

		if (dir.equals("I")) {// Izquierda
			for (int i = 0; i < piece.length; i++) {
				x = piece[i].getCoordX();
				y = piece[i].getCoordY();
				if (y == 0 || getBox(x, y - 1).esMuro())
					return false;
			}
		}

		return true;
	}

	public boolean doMovement(String mov) {
		setMov(mov);
		if (mov.equals("R")) {
			return rotatePiece();
		} else {
			if (mov.equals("L"))
				return invRotatePiece();
			else
				return movePiece(mov);
		}
	}

	/*
	 * Comprueba si hay alguna rotacion posible
	 */
	private boolean possibleRotation(int orientation) {
		int x = piece[this.pieceVertex].getCoordX();
		int y = piece[this.pieceVertex].getCoordY();

		if ((orientation == 0)) {
			// Comprobamos que la casilla de abajo y dos a la derecha sean 0.
			if (getBox(x - 1, y).esLibre() && getBox(x, y - 2).esLibre()) {
				return true;
			}
		}

		if ((orientation == 1)) {
			// Comprobamos que la casilla de la izquierda y la segunda de abajo sean 0.
			if (getBox(x - 2, y).esLibre() && getBox(x, y + 1).esLibre()) {
				return true;
			}
		}

		if ((orientation == 2)) {
			// Comprobamos que la casilla de arriba y la segunda de la izquierda sean 0.
			if (getBox(x + 1, y).esLibre() && getBox(x, y + 2).esLibre()) {
				return true;
			}
		}

		if ((orientation == 3)) {
			// Comprobamos que la casilla de la derecha y la segunda de la arriba sean 0.
			if (getBox(x + 2, y).esLibre() && getBox(x, y - 1).esLibre()) {
				return true;
			}
		}

		return false;
	}

	/*
	 * Comprueba si hay alguna rotacion posible
	 */
	private boolean possibleInvRotation(int orientation) {
		int x = piece[this.pieceVertex].getCoordX();
		int y = piece[this.pieceVertex].getCoordY();

		if ((orientation == 0)) {
			if (getBox(x, y + 1).esLibre() && getBox(x, y + 2).esLibre()) {
				return true;
			}
		}

		if ((orientation == 1)) {
			if (getBox(x + 1, y).esLibre() && getBox(x + 2, y).esLibre()) {
				return true;
			}
		}

		if ((orientation == 2)) {
			if (getBox(x, y - 1).esLibre() && getBox(x, y - 2).esLibre()) {
				return true;
			}
		}

		if ((orientation == 3)) {
			if (getBox(x - 1, y).esLibre() && getBox(x - 2, y).esLibre()) {
				return true;
			}
		}

		return false;
	}

	/*
	 * Rota una pieza teniendo en cuenta la rotacion inicial en la que se encuentra
	 * esa.
	 */
	private void rotate(int orientation) {
		int x = piece[this.pieceVertex].getCoordX();
		int y = piece[this.pieceVertex].getCoordY();

		/*
		 * Con mover dos casillas generalmente nos vale
		 */
		if (orientation == 0) {
			moveAvaibleBox(x + 2, y, x, y - 2);
			moveAvaibleBox(x + 1, y, x - 1, y);

		}

		if (orientation == 1) {
			moveAvaibleBox(x, y - 2, x - 2, y);
			moveAvaibleBox(x, y - 1, x, y + 1);
		}

		if (orientation == 2) {
			moveAvaibleBox(x - 2, y, x, y + 2);
			moveAvaibleBox(x - 1, y, x + 1, y);
		}

		if (orientation == 3) {
			moveAvaibleBox(x, y + 2, x + 2, y);
			moveAvaibleBox(x, y + 1, x, y - 1);
		}

	}

	private void invRotate(int orientation) {
		int x = piece[this.pieceVertex].getCoordX();
		int y = piece[this.pieceVertex].getCoordY();

		/*
		 * Con mover dos casillas generalmente nos vale
		 */
		if (orientation == 0) {
			moveAvaibleBox(x, y - 1, x, y + 1);
			moveAvaibleBox(x + 2, y, x, y + 2);

		}

		if (orientation == 1) {
			moveAvaibleBox(x - 1, y, x + 1, y);
			moveAvaibleBox(x, y - 2, x + 2, y);
		}

		if (orientation == 2) {
			moveAvaibleBox(x, y + 1, x, y - 1);
			moveAvaibleBox(x - 2, y, x, y - 2);
		}

		if (orientation == 3) {
			moveAvaibleBox(x + 1, y, x - 1, y);
			moveAvaibleBox(x, y + 2, x - 2, y);
		}

	}

	/*
	 * Para mover la pieza se toma como base el vertice de la pieza Toda la pieza
	 * rotara en sentido horario en torno a este.
	 */
	public boolean rotatePiece() {

		// 0: Posicion Inicial, 1: Con 1 rotacion, 2: Con 2 rotaciones, 3: Con 3
		// Rotaciones
		int orientation = getPieceOrient();

		if (orientation != -1 && possibleRotation(orientation)) {
			rotate(orientation);
			findPiece();
			return true;
		} else
			return false;
	}

	public boolean invRotatePiece() {

		// 0: Posicion Inicial, 1: Con 1 rotacion, 2: Con 2 rotaciones, 3: Con 3
		// Rotaciones
		int orientation = getPieceOrient();

		if (orientation != -1 && possibleInvRotation(orientation)) {
			invRotate(orientation);
			findPiece();
			return true;
		} else
			return false;
	}

	/*
	 * Realiza una rotacion de la pieza sobre el tablero que se le pasa por
	 * parametro.
	 */
	public boolean rotatePieceSim() {
		int orientation = getPieceOrient();

		if (orientation != -1 && possibleRotation(orientation)) {
			return true;
		} else
			return false;
	}

	public boolean invRotatePieceSim() {
		int orientation = getPieceOrient();

		if (orientation != -1 && possibleInvRotation(orientation)) {
			return true;
		} else
			return false;
	}

	public void setBox(int x, int y, int type) {
		this.boxes[x][y] = new Casilla(x, y, type);
	}

	public void setBox(int x, int y, int type, int h) {
		this.boxes[x][y] = new Casilla(x, y, type, h);
	}

	public void setBoxes(Casilla[][] boxes) {
		this.boxes = boxes;
	}

	public void setMov(String mov) {
		this.mov = mov;
	}

	public void setObjetive(Casilla[] objetive) {
		this.objetivo = objetive;
	}

	public void setOrient() {
		this.orient = this.getPieceOrient();
	}

	public void setPiece(Casilla[] piece) {
		this.piece = piece;
	}

	public void setTabAnt(Board prevBoard) {
		this.prevBoard = prevBoard;
	}

	public void setSizeX(int SizeX) {
		this.sizeX = SizeX;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}

	public void setObjetiveVertex(int objetiveVertex) {
		this.objetiveVertex = objetiveVertex;
	}

	public void setPieceVertex(int pieceVertex) {
		this.pieceVertex = pieceVertex;
	}
	
	public int getG() {
		return this.g;
	}
	
	public void setG(int g) {
		this.g = g;
	}

	@Override
	public String toString() {
		String ret = "";
		for (int i = 0; i < this.sizeX; i++) {
			for (int j = 0; j < this.sizeY; j++) {
				ret += getBox(i, j).getTipo() + " ";
			}
			ret += "\n";
		}

		return ret;
	}

}
