package com.bingchat4urapp;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.lingala.zip4j.ZipFile;

import javax.imageio.ImageIO;

import com.jogamp.nativewindow.util.Rectangle;

public class BrowserUtils {

    public static Boolean Debug = false;

    private static final String WinChromeDriverLink = "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/116.0.5845.96/win64/chromedriver-win64.zip";
    private static final String LinuxChromeDriverLink = "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/116.0.5845.96/linux64/chromedriver-linux64.zip";

    public static final Path logsDir = Paths.get("logsDir").toAbsolutePath();

    public static class ImageData
    {
        public BufferedImage image;
        public Rectangle position;
        
        public ImageData (BufferedImage _image, Rectangle _position){
            image = _image;
            position = _position;
        }
    }

    // method that compare two images
    public static Boolean CompareImagesAlg(BufferedImage FirstImage, BufferedImage SecondImage, double tolerance, double PercentOfMath){

        if (FirstImage.getWidth()!=SecondImage.getWidth() || FirstImage.getHeight()!=SecondImage.getHeight()) return false;
        Double CountGood = 0.0;
        for (int i=0; i<FirstImage.getHeight(); i++){
            for (int j=0; j<FirstImage.getWidth(); j++){
                // j - it's x cord and i - it's y cord
                Color color1 = new Color(FirstImage.getRGB(j, i));
                Color color2 = new Color(SecondImage.getRGB(j, i));
                if (Math.abs(color1.getRed() - color2.getRed()) <= tolerance &&
                    Math.abs(color1.getGreen() - color2.getGreen()) <= tolerance &&
                    Math.abs(color1.getBlue() - color2.getBlue()) <= tolerance) {
                    CountGood += 1.0;
                }
            }
        }
        print("Good = "+CountGood+" Total = "+ FirstImage.getWidth()*FirstImage.getHeight());
        return (CountGood/(FirstImage.getWidth()*FirstImage.getHeight()))*100.0>=PercentOfMath;
    }


    public static Boolean CompareImages(BufferedImage CurrentImage, ImageData ToCompare){
        BufferedImage CroppedImage = CurrentImage.getSubimage(ToCompare.position.getX(), ToCompare.position.getY(), ToCompare.position.getWidth(), ToCompare.position.getHeight());
        Boolean result = CompareImagesAlg(CroppedImage, ToCompare.image, 10, 90.0);

        if (!result && Debug){
            try {
                ImageIO.write(CroppedImage, "png", new File("failedCrop.png"));

                // Draw a red rectangle around the cropped area on the original image
                Graphics2D g = CurrentImage.createGraphics();
                g.setColor(Color.RED);
                g.drawRect(ToCompare.position.getX(), ToCompare.position.getY(), ToCompare.position.getWidth(), ToCompare.position.getHeight());
                g.dispose();

                // Save the original image with the red rectangle
                ImageIO.write(CurrentImage, "png", new File("failedOriginal.png"));
            } catch (IOException e) {
                print("Can't save debug data");
                e.printStackTrace();
            }
        }
        return result;
    }

    // download zip file with chrome driver and extract it
    public static Boolean DownloadChromeDriver(){
        Boolean isWindows = System.getProperty("os.name").contains("Windows");
        String driverLink = isWindows ? WinChromeDriverLink : LinuxChromeDriverLink;
        String ZipPath = Paths.get(System.getProperty("user.home"), "Documents", "chromedriver.zip").toString();

        if (Files.exists(Paths.get(ZipPath))){
            return true;
        }

        try{
            InputStream in = new URL(driverLink).openStream();
            Files.copy(in, Paths.get(ZipPath));
            print("Downloaded zip file");

            ZipFile zip = new ZipFile(ZipPath);
            zip.extractAll(Paths.get(System.getProperty("user.home"), "Documents").toString());
            zip.close();
            print("Extracted zip file");
        }
        catch (Exception e){
            print("Can't download driver");
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    public static String ExtractAuthLink(String str, String key) {
        int i = str.indexOf(key);
        if (i == -1) {
            return "";
        }
        i += key.length();
        int j = str.indexOf("\"", i);
        if (j == -1) {
            return str.substring(i);
        }
        return str.substring(i, j);
    }

    // метод, который генеирирует случайное имя для файла
    public static String GenerateRandomFileName(int length){
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int)(Math.random()*alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }

    private static void print(String text){
        System.out.println("[Browser utils] " + text);
    }
}
