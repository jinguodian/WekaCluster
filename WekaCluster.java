/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wekacluster;

import java.lang.String;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import weka.clusterers.*;
import weka.core.*;
import weka.core.converters.*;


/**
 *
 * @author JIN
 */
public class WekaCluster {

    static int clusterNum = -1;
    
    static String inAFile = null;
    static String inTFile = null;
    static String outDir = null;
    static String stddev = null;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
            if (args.length < 1) {
                System.out.println("the program argument is too less!");
                return ;
            }
        if (!GetConf(args[0]))
            return;
        
       Instances ins = null;
       File file = new File(inAFile);
       ArffLoader loader=new ArffLoader();
       try {
           
           loader.setFile(file);
           ins = loader.getDataSet();
           
           String outDirE = outDir + "\\EM";
           String outDirK = outDir + "\\KMeans";
           
           Em(ins,inTFile,outDirE);
           KMeans(ins,clusterNum,inTFile,outDirK);
      
       } catch (IOException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
    }
    
    
    static boolean GetConf(String filePath)
    {
        boolean flag = false;
        try{
            File confPath = new File(filePath);
            if (!confPath.exists())
            {
                System.out.println(filePath + " not found !");
                return false;
            }

            BufferedReader br  = new BufferedReader(new FileReader(confPath));
           
            inAFile = br.readLine();
            inTFile = br.readLine();
            outDir = br.readLine();
            String temp = null;
            if((temp = br.readLine()) != null)
                stddev = temp;
            temp = null;
            if((temp = br.readLine()) != null)
                clusterNum = Integer.valueOf(temp);
            
            if(inAFile.isEmpty() || inTFile.isEmpty() || outDir.isEmpty())
                flag = false;
            else
                flag = true;
        }
        catch (Exception e){
            System.out.println("Exception in GetConf : " + e.getMessage());
            flag = false;
        }
        finally{
            return flag;
        }
    }
    
    
    static void Em(Instances inData,String inFile,String outDir){
        BufferedReader reader = null;
        BufferedWriter[] bw = null;
        InputStreamReader isr = null;
        try{
            weka.clusterers.EM dbs = new weka.clusterers.EM();
            dbs.setMaxIterations(100);
            if(stddev != null)
                dbs.setMinStdDev(Double.valueOf(stddev));
            
            dbs.setNumClusters(clusterNum);
            dbs.buildClusterer(inData);
            
         //   System.out.println(dbs.toString());
            ClusterEvaluation eval = new ClusterEvaluation();
            eval.setClusterer(dbs);
            eval.evaluateClusterer(inData);
            //   System.out.println(inData.toString());
            if(clusterNum == -1)
                clusterNum = eval.getNumClusters();
            double[] num = eval.getClusterAssignments();
            String[] outFileName = new String[clusterNum];
            bw = new BufferedWriter[clusterNum];

            CreateNewDir(outDir);

            for (int i=0; i < clusterNum; ++i)
            {
                outFileName[i] = outDir + "\\" + i;
                CreateNewFile(outFileName[i]);
                bw[i] = new BufferedWriter(new FileWriter(outFileName[i]));
            }
            isr = new InputStreamReader(new FileInputStream(inFile),"UTF-8");
            reader = new BufferedReader(isr);
            for (int i = 0; i < num.length; i++)
            {
                  bw[(int)(num[i])].write(reader.readLine());
                  bw[(int)(num[i])].newLine();
            }
       }catch(Exception ex){}
       finally{
            try{
                if (reader != null)
                {
                   isr.close();
                    reader.close();
                }
               for (int i=0; i < bw.length; ++i)
               {
                   if (bw[i] != null){
                       bw[i].close();
                   }
               }
            }catch (IOException ex){}
            finally{
            }
       }
    }
    
    
    static void KMeans(Instances inData,int cNum,String inFile,String outDir){
        BufferedReader reader = null;
        InputStreamReader isr = null;
        BufferedWriter[] bw = null;
        try{
            weka.clusterers.SimpleKMeans dbs = new weka.clusterers.SimpleKMeans();
            dbs.setMaxIterations(100);
        //    dbs.setSeed(100);
            
            dbs.setNumClusters(cNum);
            
            dbs.buildClusterer(inData);
        //    System.out.println(dbs.toString());
            ClusterEvaluation eval = new ClusterEvaluation();
            eval.setClusterer(dbs);
            eval.evaluateClusterer(inData);
            double[] assignment = eval.getClusterAssignments();
            String[] outFileName = new String[cNum];
            bw = new BufferedWriter[cNum];

            CreateNewDir(outDir);

            for (int i = 0; i < cNum; ++i)
            {
                outFileName[i] = outDir + "\\" + i;
                CreateNewFile(outFileName[i]);
                bw[i] = new BufferedWriter(new FileWriter(outFileName[i],true));
            }
            isr = new InputStreamReader(new FileInputStream(inFile),"UTF-8");
            reader = new BufferedReader(isr);
            for (double i : assignment)
            {
                  bw[(int)i].write(reader.readLine());
                  bw[(int)i].newLine();
            }
       }catch(Exception ex){}
       finally{
            try{
                if (reader != null)
                {
                    reader.close();
                }
               for (int i=0; i < bw.length; ++i)
               {
                   if (bw[i] != null){
                       bw[i].close();
                   }
               }
            }catch (IOException ex){}
            finally{}
       }
    }

    
    static void CreateNewDir(String dirPath) {
        File fl = new File(dirPath);
        if(!fl.exists())
        {
            fl.mkdirs();
        }
    }
    
    static void CreateNewFile(String filePath){
           try{
               File temp = new File(filePath);
               if (temp.exists())
               {
                   temp.createNewFile();
               }
           }
           catch (IOException ex){}              
           finally{}
       }
}
