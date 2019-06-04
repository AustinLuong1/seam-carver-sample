import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stopwatch;

public class SeamCarver {
	
	private Picture picture;
	
	public SeamCarver(Picture picture){
		// create a seam carver object based on the given picture
		if (picture == null) {
			throw new IllegalArgumentException("picture cannot be null");
		}
		this.picture = new Picture(picture);
	}
   
	public Picture picture(){
		// current picture
		Picture curr = picture;
		return curr;
	}
   
	public int width(){
		// width of current picture
		return picture.width();
	}
   
	public int height(){
		// height of current picture
		return picture.height();
	}
   
	public double energy(int x, int y){
		// energy of pixel at column x and row y
		if (x < 0 || x >= picture.width()) {
			throw new IllegalArgumentException("x not within bounds of image");
		}
		
		if (y < 0 || y >= picture.height()) {
			throw new IllegalArgumentException("y not within bounds of image");
		}
		Color left;
		Color right;
		Color up;
		Color down;
		if (x == 0) { 
			left = picture.get(picture.width() - 1, y);
		} else {
			left = picture.get(x - 1, y);
		}
		
		if (x == picture.width() - 1) {
			right = picture.get(0, y);
		} else {
			right = picture.get(x + 1, y);
		}
		
		if (y == 0) {
			up = picture.get(x, picture.height() - 1);
		} else {
			up = picture.get(x, y - 1);
		}
		
		if (y == picture.height() - 1) {
			down = picture.get(x, 0);
		} else {
			down = picture.get(x, y + 1);
		}
		double rx = right.getRed() - left.getRed();
		double gx = right.getGreen() - left.getGreen();
		double bx = right.getBlue() - left.getBlue();
		double ry = down.getRed() - up.getRed();
		double gy = down.getGreen() - up.getGreen();
		double by = down.getBlue() - up.getBlue();
		double xgrad = (rx * rx) + (gx * gx) + (bx * bx);
		double ygrad = (ry * ry) + (gy * gy) + (by * by);
		double energy = Math.sqrt(xgrad + ygrad);
		
		return energy;
	}
   
	public int[] findHorizontalSeam(){
		// sequence of indices for horizontal seam
		if (picture.height() == 1) {
			int[] minSeam = new int[picture.width()];
			return minSeam;
		}
		double[][] energySum = new double[picture.width()][picture.height()];
		// initializes double arr representation of picture
		// fills in top row with energies from top row of pixels
		for (int y = 0; y < picture.height(); y++) {
			energySum[0][y] = energy(0, y);
		}
		
		// finds lowest summed energy from pixels above and sums the lowest energy
		// with the current energy
		for (int x = 1; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				double currMin = energySum[x - 1][y];
				if (y == 0) {
					if (energySum[x - 1][y + 1] < currMin) {
						currMin = energySum[x - 1][y + 1];
					}
				} else if (y == picture.height() - 1) {
					if (energySum[x - 1][y - 1] < currMin) {
						currMin = energySum[x - 1][y - 1];
					}
				} else {
					if (energySum[x - 1][y - 1] < currMin) {
						currMin = energySum[x - 1][y - 1];
					}
					if (energySum[x - 1][y + 1] < currMin) {
						currMin = energySum[x - 1][y + 1];
					}
				}
				energySum[x][y] = energy(x, y) + currMin;
			}
		}
		
		// finds the lowest energy sum at the bottom of the image
		double minE = Double.MAX_VALUE;
		int minIndex = 0;
		for (int y = 0; y < picture.height(); y++) {
			if (energySum[picture.width() - 1][y] < minE) {
				minE = energySum[picture.width() - 1][y];
				minIndex = y;
			}
		}
		
		int[] minSeam = new int[picture.width()];
		minSeam[picture.width() - 1] = minIndex;
		
		// traces seam up from lowest sum at bottom, connecting to lowest sum
		// above
		for (int x = picture.width() - 1; x > 0; x--) {
			if (minIndex == 0) {
				if (energySum[x - 1][minIndex] <= energySum[x - 1][minIndex + 1]) {
					minSeam[x - 1] = minIndex;
				} else {
					minSeam[x - 1] = minIndex + 1;
					minIndex++;
				}
			} else if (minIndex == picture.height() - 1) {
				if (energySum[x - 1][minIndex] <= energySum[x - 1][minIndex - 1]) {
					minSeam[x - 1] = minIndex;
				} else {
					minSeam[x - 1] = minIndex - 1;
					minIndex--;
				}
			} else {
				if (energySum[x - 1][minIndex] <= energySum[x - 1][minIndex - 1] &&
					energySum[x - 1][minIndex] <= energySum[x - 1][minIndex + 1]) {
					minSeam[x - 1] = minIndex;
				} else if (energySum[x - 1][minIndex - 1] <= energySum[x - 1][minIndex] &&
					energySum[x - 1][minIndex - 1] <= energySum[x - 1][minIndex + 1]) {
					minSeam[x - 1] = minIndex - 1;
					minIndex --;
				} else {
					minSeam[x - 1] = minIndex + 1;
					minIndex++;
				}
			}
		}
		return minSeam;
	}
   
	public int[] findVerticalSeam(){
		// sequence of indices for vertical seam
		if (picture.width() == 1) {
			int[] minSeam = new int[picture.height()];
			return minSeam;
		}
		
		double[][] energySum = new double[picture.width()][picture.height()];
		// initializes double arr representation of picture
		// fills in top row with energies from top row of pixels
		for (int x = 0; x < picture.width(); x++) {
			energySum[x][0] = energy(x,0);
		}
		
		// finds lowest summed energy from pixels above and sums the lowest energy
		// with the current energy
		for (int y = 1; y < picture.height(); y++) {
			for (int x = 0; x < picture.width(); x++) {
				double currMin = energySum[x][y - 1];
				if (x == 0) {
					if (energySum[x + 1][y - 1] < currMin) {
						currMin = energySum[x + 1][y - 1];
					}
				} else if (x == picture.width() - 1) {
					if (energySum[x - 1][y - 1] < currMin) {
						currMin = energySum[x - 1][y - 1];
					}
				} else {
					if (energySum[x - 1][y - 1] < currMin) {
						currMin = energySum[x - 1][y - 1];
					}
					if (energySum[x + 1][y - 1] < currMin) {
						currMin = energySum[x + 1][y - 1];
					}
				}
				energySum[x][y] = energy(x, y) + currMin;
			}
		}
		
		// finds the lowest energy sum at the bottom of the image
		double minE = Double.MAX_VALUE;
		int minIndex = 0;
		for (int x = 0; x < picture.width(); x++) {
			if (energySum[x][picture.height() - 1] < minE) {
				minE = energySum[x][picture.height() - 1];
				minIndex = x;
			}
		}
		
		int[] minSeam = new int[picture.height()];
		minSeam[picture.height() - 1] = minIndex;
		
		// traces seam up from lowest sum at bottom, connecting to lowest sum
		// above
		for (int y = picture.height() - 1; y > 0; y--) {
			if (minIndex == 0) {
				if (energySum[minIndex][y - 1] <= energySum[minIndex + 1][y - 1]) {
					minSeam[y - 1] = minIndex;
				} else {
					minSeam[y - 1] = minIndex + 1;
					minIndex++;
				}
			} else if (minIndex == picture.width() - 1) {
				if (energySum[minIndex][y - 1] <= energySum[minIndex - 1][y - 1]) {
					minSeam[y - 1] = minIndex;
				} else {
					minSeam[y - 1] = minIndex - 1;
					minIndex--;
				}
			} else {
				if (energySum[minIndex][y - 1] <= energySum[minIndex - 1][y - 1] &&
					energySum[minIndex][y - 1] <= energySum[minIndex + 1][y - 1]) {
					minSeam[y - 1] = minIndex;
				} else if (energySum[minIndex - 1][y - 1] <= energySum[minIndex][y - 1] &&
					energySum[minIndex - 1][y - 1] <= energySum[minIndex + 1][y - 1]) {
					minSeam[y - 1] = minIndex - 1;
					minIndex --;
				} else {
					minSeam[y - 1] = minIndex + 1;
					minIndex++;
				}
			}
		}
		return minSeam;
	}
   
	public void removeHorizontalSeam(int[] seam){
		// remove horizontal seam from current picture
		if (seam == null) {
			throw new IllegalArgumentException("seam cannot be null");
		}
		
		if (picture.height() == 1) {
			throw new IllegalArgumentException("cannot remove seam from 1 height image");
		}
		
		if (seam.length != picture.width()) {
			throw new IllegalArgumentException("length of seam not equal to width of image");
		}
		
		Picture newPic = new Picture(picture.width(), picture.height() - 1);
		for (int x = 0; x < picture.width(); x++) {
			if (seam[x] < 0 || seam[x] >= picture.height()) {
				throw new IllegalArgumentException("seam is not within bounds of image");
			}
			
			boolean foundSeam = false;
			
			if (x > 0) {
				if (Math.abs(seam[x] - seam[x - 1]) > 1) {
					throw new IllegalArgumentException("seam is not valid");
				}
			}
			
			for (int y = 0; y < picture.height(); y++) {
				if (y == seam[x]) {
					foundSeam = true;
					continue;
				}
				
				if (foundSeam) { 
					newPic.set(x, y - 1, picture.get(x, y));
				} else {
					newPic.set(x, y, picture.get(x, y));
				}
			}
		}
		
		picture = newPic;
		return;
	}
   
	public void removeVerticalSeam(int[] seam){
		// remove vertical seam from current picture
		if (seam == null) {
			throw new IllegalArgumentException("seam cannot be null");
		}
		
		if (picture.width() == 1) {
			throw new IllegalArgumentException("cannot remove seam of 1x1 image");
		}
		
		if (seam.length != picture.height()) {
			throw new IllegalArgumentException("length of seam not equal to height of image");
		}
		
		Picture newPic = new Picture(picture.width() - 1, picture.height());
		for (int y = 0; y < picture.height(); y++) {
			if (seam[y] < 0 || seam[y] >= picture.width()) {
				throw new IllegalArgumentException("seam is not within bounds of image");
			}
			
			boolean foundSeam = false;
			
			if (y > 0) {
				if (Math.abs(seam[y] - seam[y - 1]) > 1) {
					throw new IllegalArgumentException("seam is not valid");
				}
			}
			
			for (int x = 0; x < picture.width(); x++) {
				if (x == seam[y]) {
					foundSeam = true;
					continue;
				}
				
				if (foundSeam) { 
					newPic.set(x - 1, y, picture.get(x, y));
				} else {
					newPic.set(x, y, picture.get(x, y));
				}
				
			}
		}
		picture = newPic;
		return;
	}
}