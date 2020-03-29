package dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class JTreePlot {
	public int nID;
	public double dWidth;
	public double dHeight;
	public double dMaxTreeHeight;
	public int nTreeNumber; //2-80
	public ArrayList<JTreeInfo> listTrees;
	public double dMatrixHeight[][]; //Tree DEM
	public double dMatrixLiDAR[][]; //LiDAR DEM
	public int nGroundReturns;
	public int nNonGroundReturns;
	public double dMeanHeight;
	public double dQuadMeanHeight;
	public double dSumBiomass;
	public double dCoverRate;
	public double dCrownRatio;
	
	public JTreePlot() {
		// TODO Auto-generated constructor stub
		nID = 0;
		dWidth = 25;
		dHeight = 25;
		dMaxTreeHeight = 0;
		//nTreeNumber = (int) (Math.random() * 78 + 2);

		nGroundReturns = 0;
		nNonGroundReturns = 0;
		dMeanHeight = 0;
		dSumBiomass = 0;
		
		dCoverRate = 0;
		dCrownRatio = 0;
		
		Random random = new Random();
		nTreeNumber = (int) (Math.sqrt(10)*random.nextGaussian() + 20);
		listTrees = new ArrayList<JTreeInfo>();
		while (listTrees.size() < nTreeNumber) {
			JTreeInfo newTree = new JTreeInfo();//Random
			newTree.dCenterX = Math.random() * dWidth;
			newTree.dCenterY = Math.random() * dHeight;
			
			while (!ValidationTree(newTree)) {
				newTree.dCenterX = Math.random() * dWidth;
				newTree.dCenterY = Math.random() * dHeight;
			}
			
			if (dMaxTreeHeight <= newTree.dHeight) {
				dMaxTreeHeight = newTree.dHeight;
			}
			listTrees.add(newTree);
		}
		
		FillingMatrix();
	}
	
	public JTreePlot(String strFile, int nPlotID) throws FileNotFoundException {
		// TODO Auto-generated constructor stub
		nID = nPlotID;
		dWidth = 25;
		dHeight = 25;
		dMaxTreeHeight = 0;
		nTreeNumber = 0;
		listTrees = new ArrayList<JTreeInfo>();
		nGroundReturns = 0;
		nNonGroundReturns = 0;
		dMeanHeight = 0;
		dSumBiomass = 0;
		dCoverRate = 0;
		dCrownRatio = 0;
		
		InputStream inStream = new FileInputStream(strFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

        String strLine = null;
        Random random = new Random();
        double dVariance;
        try {
        	strLine = reader.readLine();
            while ((strLine = reader.readLine()) != null) {
            	String datavalue[] = strLine.split("\t");
            	if (Integer.parseInt(datavalue[0]) == nID) {
            		JTreeInfo newTree = new JTreeInfo();
            		newTree.strSpecie = datavalue[1];
            		newTree.dDBH = Double.parseDouble(datavalue[2]);
            		newTree.strCrownClass = datavalue[9];
            		// since	random.nextGaussian() ~ N(0, 1*1)
            		//			random.nextGaussian()+1 ~ N(1, 1*1)
            		//			b*random.nextGaussian()+a ~ N(a, b*b)
            		// let x0 = 0.00*random.nextGaussian()+1 then x0 ~ N(1, 0) == 1
            		// let x1 = 0.01*random.nextGaussian()+1 then x1 ~ N(1, 0.1*0.1)
            		// let x2 = 0.04*random.nextGaussian()+1 then x2 ~ N(1, 0.2*0.2)
            		// let x3 = 0.09*random.nextGaussian()+1 then x3 ~ N(1, 0.3*0.3)
            		// let x4 = 0.16*random.nextGaussian()+1 then x4 ~ N(1, 0.4*0.4)
            		// let x5 = 0.25*random.nextGaussian()+1 then x5 ~ N(1, 0.5*0.5)
            		
            		/*dVariance = random.nextGaussian() + 1;
            		while (dVariance <= 0 || dVariance >= 2) {
            			dVariance = random.nextGaussian() + 1;
					}
            		newTree.dRadius = dVariance*(2.5067 + 0.0244*newTree.dDBH);*/
            		if(newTree.strCrownClass.equals("D")||newTree.strCrownClass.equals("CD"))
            		{
            		newTree.dRadius = 2.5067 + 0.0244*newTree.dDBH;
            		}
            		else
            		{
            			

            		
            		if(newTree.strSpecie.equals("ABCO"))
            		{
            			
            			newTree.dRadius = (5.82+0.591*(newTree.dDBH/2.54))*0.3048;
            		}
            		if(newTree.strSpecie.equals("ABMA"))
            		{
            			newTree.dRadius = (1.025+0.025*newTree.dDBH);
            			
            		}
            		if(newTree.strSpecie.equals("PICO "))
            		{
           			newTree.dRadius = (0.60*Math.pow((newTree.dDBH/2.54),0.7086));
           			
            		}
           
            		if(newTree.strSpecie.equals("PIMO"))
            		{
            			newTree.dRadius = (0.06*newTree.dDBH-0.15);
            			
            		}
            		if(newTree.strSpecie.equals("PIJE"))
            		{
            			newTree.dRadius = (0.23*Math.pow((newTree.dDBH/2.54), 0.891));
            			
            		}
            		if(newTree.strSpecie.equals("TSME"))
            		{
            			newTree.dRadius = (0.72+0.04*newTree.dDBH);
            			
            		}
            		if(newTree.strSpecie.equals("POTR"))
            		{
            			newTree.dRadius = (0.0762+0.0972*newTree.dDBH);
            		}
            		}

           //dVariance = 0.5*random.nextGaussian() + 0;
            		//while (dVariance <= 0 || dVariance >= 2) {
            			//dVariance = 0.1*random.nextGaussian() + 1;
					//}
            		//dVariance = 0;
            		//newTree.dHeight = Math.exp((0.7140)*Math.log(newTree.dDBH) + 0.3042+dVariance);
            		
            		newTree.dRealHeight = Double.parseDouble(datavalue[3]);
            		newTree.dHeight = newTree.dRealHeight ;
            		newTree.dCrownRatio = Double.parseDouble(datavalue[4]);
            		newTree.dJenkinsBiomass = Double.parseDouble(datavalue[5]);
            		dSumBiomass += newTree.dJenkinsBiomass;
            		
        			/*newTree.dCenterX = Math.random() * dWidth;
        			newTree.dCenterY = Math.random() * dHeight;
        			while (!ValidationTree(newTree)) {
        				newTree.dCenterX = Math.random() * dWidth;
        				newTree.dCenterY = Math.random() * dHeight;
        			}*/
            		newTree.dCenterX = Double.parseDouble(datavalue[10]);
            		newTree.dCenterY = Double.parseDouble(datavalue[11]);
            		
        			if (dMaxTreeHeight <= newTree.dHeight) {
        				dMaxTreeHeight = newTree.dHeight;
        			}
        			
        			listTrees.add(newTree);
        			nTreeNumber++;
				}
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
		FillingMatrix();
	}
	
	
	public void FillingMatrix() {
		dMatrixHeight = new double[250][250];
		dMatrixLiDAR = new double[250][250];
		
		int nX;
		int nY;
		int nr;
		double dH;
		double dOverlapHeight;
		
		for (JTreeInfo t : listTrees) {
			nX = (int) (t.dCenterX*10);
			nY = (int) (t.dCenterY*10);
			nr = (int) (t.dRadius*10);
			dH = t.dHeight;
			if (dMatrixHeight[nX][nY] < t.dHeight) {
				dMatrixHeight[nX][nY] = t.dHeight;
			}
			
			//t.dCrownRatio = 2.0/3.0;
			for (int i = -nr; i < nr; i++) {
				for (int j = -nr; j < nr; j++) {
					if (nX + i>=0 && nY + j>=0 && nX + i<250 && nY + j<250 && Math.sqrt(i*i+j*j) <= nr) {
						dOverlapHeight = (1-t.dCrownRatio)*dH + t.dCrownRatio*dH*(nr - Math.sqrt(i*i+j*j))/nr;
						if (dMatrixHeight[nX + i][nY + j] < dOverlapHeight) {
							dMatrixHeight[nX + i][nY + j] = dOverlapHeight;
						}
					}
				}
			}
		}
		
		//Return 2-4 points in moving 10*10 windows
		int nNum;
		double dHeightSum = 0;
		double dHeightQuadraticSum = 0;
		int nCover = 0;
		nGroundReturns = 0;
		nNonGroundReturns = 0;
		
		for (int i = 0; i < 250; i++) {
			for (int j = 0; j < 250; j++) {
				if (dMatrixHeight[i][j] > 0) {
					nCover++;
				}
			}
		}
		for (int i = 0; i < 240;) {
			for (int j = 0; j < 240;) {
				nNum = (int) (Math.random() * 2 + 2);
				for (int j2 = 0; j2 < nNum; j2++) {
					nX = (int) (Math.random() * 10) + i;
					nY = (int) (Math.random() * 10) + j;
					if (Math.sqrt((nX-125)*(nX-125)+(nY-125)*(nY-125)) <= 125) {
						if (dMatrixHeight[nX][nY] > 0) {
							nNonGroundReturns++;
							dMatrixLiDAR[nX][nY] = dMatrixHeight[nX][nY];
							dHeightSum += Math.pow(dMatrixLiDAR[nX][nY], 1);
							dHeightQuadraticSum += dMatrixLiDAR[nX][nY]*dMatrixLiDAR[nX][nY];
						} else {
							nGroundReturns++;
						}
					}
					
				}
				j += 10;
			}
			i += 10;
		}
		
		//Mean Height
		dMeanHeight = Math.pow(dHeightSum/(nNonGroundReturns + nGroundReturns),1);
		dQuadMeanHeight = Math.sqrt(dHeightQuadraticSum/(nNonGroundReturns + nGroundReturns));
		
		//Cover Rate
		dCoverRate = (double) nCover/(250*250);
	}
	
	public Boolean ValidationTree(JTreeInfo i) {
		//Falling in the 12.5m circle
		if (Math.sqrt((i.dCenterX-12.5)*(i.dCenterX-12.5)+(i.dCenterY-12.5)*(i.dCenterY-12.5)) > 12.5) {
			return false;
		}
		
		//DBH No Overlapping
		if (nTreeNumber == 0) {
			return true;
		} else {
			for (JTreeInfo j : listTrees) {
				
				
				if (Math.sqrt((i.dCenterX-j.dCenterX)*(i.dCenterX-j.dCenterX)+(i.dCenterY-j.dCenterY)*(i.dCenterY-j.dCenterY)) <= (i.dDBH + j.dDBH)*(i.dDBH + j.dDBH)/4900) {
					return false;
				}
				
				
			}
			return true;
		}
	}
}
