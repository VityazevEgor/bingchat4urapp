package com.bingchat4urapp;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jogamp.nativewindow.util.Rectangle;

public class BrowserUtils {

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

        if (!result){
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }
    
    private static void print(String text){
        System.out.println("[Browser utils] " + text);
    }
}
