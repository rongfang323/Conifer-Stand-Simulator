package service;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.rosuda.REngine.REXPMismatchException;

import Rserve.JTestingR;
import dao.JTreeInfo;
import dao.JTreePlot;

public class JSimulatingPlots {

	ArrayList<JTreePlot> listPlots;
	ArrayList<Integer> listPlotsIDUnique;
	
	public JSimulatingPlots() {
		// TODO Auto-generated constructor stub
		listPlots = new ArrayList<JTreePlot>();
		while (listPlots.size() < 1) {
			JTreePlot newPlot = new JTreePlot();
			listPlots.add(newPlot);
		}
	}
	
	public JSimulatingPlots(String strFile) throws FileNotFoundException {
		// TODO Auto-generated constructor stub
		listPlots = new ArrayList<JTreePlot>();
		listPlotsIDUnique = new ArrayList<Integer>();
		
		InputStream inStream = new FileInputStream(strFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

        ArrayList<Integer> listPlotsIDs = new ArrayList<Integer>();
        String strLine = null;
        try {
        	strLine = reader.readLine();
            while ((strLine = reader.readLine()) != null) {
            	String datavalue[] = strLine.split("\t");
            	listPlotsIDs.add(Integer.parseInt(datavalue[0]));
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
        HashSet<Integer> set = new HashSet<Integer>(listPlotsIDs);
    	listPlotsIDUnique = new ArrayList<Integer>(set);

		for (int i : listPlotsIDUnique) {
			JTreePlot newPlot = new JTreePlot(strFile, i);
			listPlots.add(newPlot);
		}
	}

	public String ExportPlots(JTreePlot myPlot) {
		String strOut = "";
		strOut += "ID"+"\t"+"X"+"\t"+"Y"+"\t"+"DBH"+"\t"+"Height" + "Species"+"\t"+"RealHeight" + "\n";
		System.out.println("ID"+"\t"+"X"+"\t"+"Y"+"\t"+"DBH"+"\t"+"Height");
		
		int i = 0;
		for (JTreeInfo jTree : myPlot.listTrees) {
			strOut += i+++"\t"+jTree.dCenterX+"\t"+jTree.dCenterY+"\t"+jTree.dDBH+"\t"+jTree.dHeight + jTree.strSpecie + jTree.dRealHeight + "\n";
		}
		return strOut;
	}
	
	public String ExportLiDAR(String strdir) throws IOException {
		String strOut = "";
		File fout = new File(strdir);
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		strOut += "PlotID"+"\t"+"Returns"+"\t"+"MeanHeight"+"\t"+"QuadMeanHeight"+"\t"+"CoverRate"+"\t"+"SumBiomass" + "\n";
		bw.write(strOut);
		bw.newLine();

		for (JTreePlot jTreePlot : listPlots) {
			bw.write(String.format("%d\t%d\t%.6f\t%.6f\t%.6f\t%.6f", jTreePlot.nID, jTreePlot.nGroundReturns + jTreePlot.nNonGroundReturns, jTreePlot.dMeanHeight, jTreePlot.dQuadMeanHeight, jTreePlot.dCoverRate, jTreePlot.dSumBiomass));
			strOut += String.format("%d\t%d\t%.6f\t%.6f\t%.6f\t%.6f\n", jTreePlot.nID, jTreePlot.nGroundReturns + jTreePlot.nNonGroundReturns, jTreePlot.dMeanHeight, jTreePlot.dQuadMeanHeight, jTreePlot.dCoverRate, jTreePlot.dSumBiomass);
			bw.newLine();
		}
		bw.close();
		
		JTestingR testingR = new JTestingR(listPlots.size());
		int i = 0;
		for (JTreePlot jTreePlot : listPlots) {
			testingR.dX[i] = Math.log(jTreePlot.dMeanHeight);
			testingR.dY[i] = Math.log(jTreePlot.dQuadMeanHeight);
			i++;
		}
		try {
			testingR.LinearRegression2();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return strOut;
	}
	
	public String ExportTrees(String strdir) throws IOException {
		String strOut = "";
		File fout = new File(strdir);
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		strOut += "PlotID"+"\t"+"Species"+"\t"+"DBH"+"\t"+"Radius"+"\t"+"RealHeight" +"\t"+"EstimatedHeight" + "\t"+"Xcoordinate"+"\t"+"Ycoordinate"+"\n";
		bw.write(strOut);
		bw.newLine();
		int nTreeCount = 0;
		for (JTreePlot jTreePlot : listPlots) {
			for (JTreeInfo jTree : jTreePlot.listTrees) {
				strOut += String.format("%d\t%s\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f", jTreePlot.nID, jTree.strSpecie, jTree.dDBH,  jTree.dRadius, jTree.dRealHeight, jTree.dHeight,jTree.dCenterX,jTree.dCenterY) + "\n";
				bw.write(String.format("%d\t%s\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f", jTreePlot.nID, jTree.strSpecie, jTree.dDBH,  jTree.dRadius, jTree.dRealHeight, jTree.dHeight,jTree.dCenterX,jTree.dCenterY));
				bw.newLine();
				nTreeCount++;
			}
		}
		bw.close();
		
		JTestingR testingR = new JTestingR(nTreeCount);
		nTreeCount = 0;
		for (JTreePlot jTreePlot : listPlots) {
			for (JTreeInfo jTree : jTreePlot.listTrees) {
				testingR.dX[nTreeCount] = Math.log(jTree.dRealHeight);
				testingR.dY[nTreeCount] = Math.log(jTree.dHeight);
				nTreeCount++;
			}
		}
		try {
			testingR.LinearRegression1();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return strOut;
	}
	
	public void ExportJPG(JTreePlot myPlot, String strdir) throws IOException {
		int cx = 250;
        int cy = 250;
        //填充矩形高宽
        int cz = 10;
        //生成图的宽度
        int width = cx * cz;
        //生成图的高度
        int height = cy * cz;

        OutputStream output = new FileOutputStream(new File(strdir));
        BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D gs = bufImg.createGraphics();
        gs.setBackground(Color.WHITE);
        gs.clearRect(0, 0, width, height);
        gs.setColor(new Color(255, 0, 0));
		gs.draw(new Ellipse2D.Double(0, 0, 250*cz, 250*cz));
        for (int i = 0; i < cx; i++) {
            for (int j = 0; j < cy; j++) {
            	if (myPlot.dMatrixHeight[j][i] > 0) {
            		gs.setColor(new Color(0,(int) (myPlot.dMatrixHeight[j][i]*200/myPlot.dMaxTreeHeight + 55),0));
            		gs.drawRect(i*cz, j*cz, cz, cz);
            		gs.fillRect(i*cz, j*cz, cz, cz);
            	}
            	
            	/*if (myPlot.dMatrixLiDAR[j][i] > 0) {
            		gs.setColor(new Color(255, 0, 0));
            		gs.drawRect(i*cz, j*cz, 100, 100);
            	}*/
            }
        }
        gs.dispose();
        bufImg.flush();
        //输出文件
        ImageIO.write(bufImg, "jpg", output);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame demo = new JFrame();
        demo.setSize(400, 600);
        demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JButton button = new JButton("Random Tree Plot");
        //JLabel label = new JLabel("Trees Number:");
        
        JTextArea textarea = new JTextArea("");
        JScrollPane scroll = new JScrollPane(textarea);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 

        demo.getContentPane().add(BorderLayout.CENTER, scroll);
        demo.getContentPane().add(BorderLayout.SOUTH, button);
        

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	JSimulatingPlots myPlots;
				try {
					//16 21 31 35 37 82 98 122 163 172 173 207 214 215 216 228 234 235 246 247...
					myPlots = new JSimulatingPlots("Plot_Species_DBH_JBio_SingleUnenve.txt");
					System.out.println("读取Plot数据完成！");
					//JTreePlot myPlot = myPlots.listPlots.get(0);
	            	button.setText("Random Tree Plot");
	            	
	            	//textarea.setText(myPlots.ExportTrees(myPlot));
	            	try {
						//myPlots.ExportTrees(myPlot, "TreePlot.txt");
						//JOptionPane.showMessageDialog(null, "Tree位置随机完成！");
	            		
						for (JTreePlot jTreePlot : myPlots.listPlots) {
	            			String strFileNameJPG = String.format("TreePlot_%d.png", jTreePlot.nID);
	            			myPlots.ExportJPG(jTreePlot, strFileNameJPG);
						}
						System.out.println("模拟Plot数据输出成图！");
						String strFileNameLiDAR = String.format("LiDAR_80.txt");
						String strFileNameTrees = String.format("Trees_2000.txt");
						
						myPlots.ExportLiDAR(strFileNameLiDAR);
						textarea.setText(myPlots.ExportTrees(strFileNameTrees));
						System.out.println("模拟Tree数据导出完成！");
						//JOptionPane.showMessageDialog(null, "LiDAR扫描完成！");
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
	           
				} catch (FileNotFoundException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
            }
        });
        
		demo.setVisible(true);
	}

}
