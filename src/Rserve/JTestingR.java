package Rserve;

import org.math.R.Rsession;
import org.rosuda.REngine.REXPMismatchException;

public class JTestingR {
	public int nSize;
	public double [] dX;
	public double [] dY;
	
	public JTestingR(int n) {
		// TODO Auto-generated constructor stub
		nSize = n;
		dX = new double[nSize];
		dY = new double[nSize];
	}
	
	public void Printing() throws REXPMismatchException{
		Rsession s = Rsession.newInstanceTry(System.out, null);
		s.set("x", dX);
		s.set("mean", s.eval("mean(x)").asDoubles());
		s.set("sd", s.eval("sd(x)").asDoubles());
		s.set("seq", s.eval("seq(min(x),max(x),by=0.001)").asDoubles());
		s.set("y", s.eval("dnorm(seq,mean,sd)").asDoubles());
		s.eval("png('/Users/t/Desktop/asd.png', width = 10, height = 6, units = 'in', res = 300)");
		
		s.eval("hist(x, freq = FALSE, col='red', breaks = 20, xlab='R Squared', ylab='', main='')");
		s.eval("lines(seq,y,col='blue', lwd=1)");
		
		s.eval("dev.off()");
		
		s.end();
	}
	
	public void LinearRegression1() throws REXPMismatchException {
		Rsession s = Rsession.newInstanceTry(System.out, null);
		
		s.set("x", dX);
		s.set("y", dY);
		String strModel = s.asString("lm(x~y)");
		s.set("model", strModel);
		System.out.println(strModel);
		
		s.eval("png('/Users/t/Desktop/HeightEstimation.png', width = 5, height = 12, units = 'in', res = 300)");
		s.eval("plot(x, y, pch=20, col='blue',main= 'Height Regression', xlim=c(0, 4), xlab='ln(Real Height)', ylim=c(0, 8), ylab='ln(Estimated Height)')");
		s.eval("abline(lm(x~y), col='red', lwd=2)");
		s.eval("text(2, 8, model)");
		
		s.eval("abline(0, 1, col='green', lwd=1)");
		s.eval("dev.off()");
		
		s.end();
	}
	
	public void LinearRegression2() throws REXPMismatchException {
		Rsession s = Rsession.newInstanceTry(System.out, null);
		
		s.set("x", dX);
		s.set("y", dY);
		String strModel = s.asString("lm(x~y)");
		s.set("model", strModel);
		System.out.println(strModel);
		
		s.eval("png('/Users/t/Desktop/MeanHeightEstimation.png', width = 8, height = 6, units = 'in', res = 300)");
		s.eval("plot(x, y, pch=20, col='blue',main= 'Height Regression', xlab='ln(Mean Height)', ylab='ln(QuadMeanHeight)')");
		s.eval("abline(lm(x~y), col='red', lwd=2)");
		s.eval("text(2, 2, model)");
		
		s.eval("abline(0, 1, col='green', lwd=1)");
		s.eval("dev.off()");
		
		s.end();
	}
	
	public static void main(String[] args) {
		Rsession s = Rsession.newInstanceTry(System.out, null);
		s.end();
	}
}