package com.bingchat4urapp;

import java.awt.image.BufferedImage;

import com.bingchat4urapp.BingChat.ImageData;

public class BrowserUtils {

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
        return (CountGood/(CroppedImage.getWidth()*CroppedImage.getHeight()))*100>90;
    }    
}
