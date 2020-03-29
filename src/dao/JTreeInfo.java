package dao;


//import java.util.Random;

public class JTreeInfo {
	public double dCenterX;
	public double dCenterY;
	public double dDBH;  //2.5cm - 150cm
	public double dHeight; // ln(dHeight) = a*ln(dDBH) + b
	public double dRadius; //Canopy Radius
	public String strSpecie;
	public double dRealHeight;
	public double dJenkinsBiomass;
	
	public String strCrownClass;
	public double dCrownRatio;//Crown Height Ratio
	
	public JTreeInfo() {
		// TODO Auto-generated constructor stub
		//double a = 0.67;
		//double b = 0.01;
		//Random random = new Random();
		
		//x Fit Weibull distribution shape scale
		//double dScale = Math.random() * 30 + 0; //alpha
		//double dShape = Math.random() * 7 + 0; //betas
		//double u = -Math.log(random.nextDouble());
		//double x = dShape * Math.pow(u, 1.0/dScale);
		
		dDBH = 0;//random.nextDouble() * 150 + 2.5;
		dHeight = 0;
		dRadius = 0;//1.2256+0.03*dDBH;
		dCenterX = 0;
		dCenterY = 0;
		strSpecie = "";
		dRealHeight = 0;
		dJenkinsBiomass = 0;
		
		strCrownClass = "";
		dCrownRatio = 0;
	}
}
