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
    public static Boolean CompareImages(BufferedImage CurrentImage, ImageData ToCompare){
        BufferedImage CroppedImage = CurrentImage.getSubimage(ToCompare.position.getX(), ToCompare.position.getY(), ToCompare.position.getWidth(), ToCompare.position.getHeight());
        Double CountGood = 0.0;
        for (int i=0; i<CroppedImage.getHeight(); i++){
            for (int j=0; j<CroppedImage.getWidth(); j++){
                // j - it's x cord and i - it's y cord
                if (CroppedImage.getRGB(j, i) == ToCompare.image.getRGB(j, i)){
                    CountGood+=1.0;
                }
            }
        }
        print("Good = "+CountGood+" Total = "+ CroppedImage.getWidth()*CroppedImage.getHeight());
        Boolean result = (CountGood/(CroppedImage.getWidth()*CroppedImage.getHeight()))*100>90;
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
        System.out.println("[Browser utils]" + text);
    }
}
