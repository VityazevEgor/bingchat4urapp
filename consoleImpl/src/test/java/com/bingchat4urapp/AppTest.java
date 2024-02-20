package com.bingchat4urapp;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void TestDownload(){
        assertTrue(BrowserUtils.DownloadChromeDriver());
    }

    @Test
    public void TestExtratLink(){
        String str = "https://www.bing.com/fd/auth/signin?action=interactive&amp;provider=windows_live_id&amp;return_url=https%3a%2f%2fwww.bing.com%2f%3ftoWww%3d1%26redig%3dDF2ADC5E5B934C358E318C22032CE435%26wlsso%3d1%26wlexpsignin%3d1&amp;src=EXPLICIT&amp;sig=1E7D6247ECF66CEC3C85766CED9A6DA9\"";
        String MustGet = "interactive&amp;provider=windows_live_id&amp;return_url=https%3a%2f%2fwww.bing.com%2f%3ftoWww%3d1%26redig%3dDF2ADC5E5B934C358E318C22032CE435%26wlsso%3d1%26wlexpsignin%3d1&amp;src=EXPLICIT&amp;sig=1E7D6247ECF66CEC3C85766CED9A6DA9";
        assertTrue(BrowserUtils.ExtractAuthLink(str, "https://www.bing.com/fd/auth/signin?action=").equals(MustGet));
    }
}
