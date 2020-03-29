package service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.rosuda.REngine.REXPMismatchException;

import Rserve.JTestingR;

/**最小二乘法 一元线性回归
 * y = Ax + B 
 * B = sum( y ) / n - A * sum( x ) / n
 * A = ( n * sum( xy ) - sum( x ) * sum( y ) ) / ( n * sum( x^2 ) - sum(x) ^ 2 )
 */
public class JLinearRegression {
	public int nLength;
	public double[] x;  //ln(Mean Height)
	public double[] y;	//ln(Biomass)
	public double dA;
	public double dB;
	public double dR2;
	
	public JLinearRegression() {
		// TODO Auto-generated constructor stub
		nLength = 80;
		x = new double[nLength];
		y = new double[nLength];
		dA = 0;
		dB = 0;
		dR2 = 0;
	}
	
	public double CalculateRSquared(){
        // first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        for(int i=0; i<nLength; i++) {
            sumx  += x[i];
            sumx2 += x[i] * x[i];
            sumy  += y[i];
        }
        
        double xbar = sumx/nLength;
        double ybar = sumy/nLength;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < nLength; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        dA = xybar / xxbar;
        dB = ybar - dA * xbar;

        // analyze results
        int df = nLength - 2;
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        for (int i = 0; i < nLength; i++) {
            double fit = dA*x[i] + dB;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }
        dR2 = ssr / yybar;
        double svar  = rss / df;
        double svar1 = svar / xxbar;
        double svar0 = svar/nLength + xbar*xbar*svar1;
        
        svar0 = sumx2;
        System.out.println((int)(svar0*0));
        return dR2;
	}

	public void ReadFromTable(String strFile, double[] y) throws FileNotFoundException{
		InputStream inStream = new FileInputStream(strFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

        String strLine = null;
        int nLineCount = 0;
        try {
        	strLine = reader.readLine();
            while ((strLine = reader.readLine()) != null) {
            	String datavalue[] = strLine.split("\t");
            	y[nLineCount] = (Integer.parseInt(datavalue[4]));
            	nLineCount++;
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
	}
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		JFrame demo = new JFrame();
        demo.setSize(400, 600);
        demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton button = new JButton("Random Tree Plot");
        
        JTextArea textarea = new JTextArea("");
        JScrollPane scroll = new JScrollPane(textarea);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 

        demo.getContentPane().add(BorderLayout.CENTER, scroll);
        demo.getContentPane().add(BorderLayout.SOUTH, button);
        

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	//Duplicated times
            	JLinearRegression newRegression = new JLinearRegression();
                JSimulatingPlots myPlots;
                int nRepeatedTimes = 100;
                double[] dR2List = new double[nRepeatedTimes];
                String strOut = "Times\tSlope_a\tIntercept_b\tR Squared\n";
        		for (int i = 0; i < nRepeatedTimes; i++) {
        			try {
						myPlots = new JSimulatingPlots("Plot_Species_DBH_JBio_SingleUnenve.txt");
						for (int j = 0; j < myPlots.listPlotsIDUnique.size(); j++) {
							newRegression.x[j] = Math.log(myPlots.listPlots.get(j).dMeanHeight);
							newRegression.y[j] = Math.log(myPlots.listPlots.get(j).dSumBiomass);
							//strOut += String.format("%3d\t%.2f\t%.6f\n", j, myPlots.listPlots.get(j).dSumBiomass, myPlots.listPlots.get(j).dMeanHeight);
						}
						dR2List[i] = newRegression.CalculateRSquared();
						strOut += String.format("%3d\t%.2f\t%.2f\t%.2f\n", i, newRegression.dA, newRegression.dB, newRegression.dR2);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
        		}
        		JTestingR testingR = new JTestingR(nRepeatedTimes);
        		testingR.dX = dR2List;
        		try {
					testingR.Printing();
				} catch (REXPMismatchException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                textarea.setText(strOut);
            }
        });
		demo.setVisible(true);
	}
}
