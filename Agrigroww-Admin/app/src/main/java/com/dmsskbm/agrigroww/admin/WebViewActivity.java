package com.dmsskbm.agrigroww.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    WebView custom_webView;
    String name, email, custom_reason, whereFrom, htmlEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        custom_webView = findViewById(R.id.custom_webView);
        name = getIntent().getStringExtra("name");
        whereFrom = getIntent().getStringExtra("whereFrom");
        email = getIntent().getStringExtra("email");

        if (whereFrom.equals("ShowUsersAdapter:BanUser")) {
            custom_reason = getIntent().getStringExtra("custom_reason");

            if (custom_reason == null) {
                custom_reason = "WRITE A REASON IN THE TEXTBOX!";
            }

            htmlEmail = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "\n" +
                    "<head>\n" +
                    "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "  <title>You have been banned!</title>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n" +
                    "  <table role=\"presentation\"\n" +
                    "    <style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n" +
                    "    <tbody>\n" +
                    "      <tr>\n" +
                    "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n" +
                    "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: left;\">\n" +
                    "            <tbody>\n" +
                    "              <tr>\n" +
                    "                <td style=\"padding: 40px 0px 0px;\">\n" +
                    "                  <div style=\"text-align: left;\">\n" +
                    "                    <div style=\"margin-bottom: -100px; text-align: center; background-color:white; background-color: rgb(255, 255, 255);\"><img src=\"https://i.ibb.co/0qf8xMS/splashlogo.png\" alt=\"AgriGroww\" style=\"width: 80%;\"></div>\n" +
                    "                  </div>\n" +
                    "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n" +
                    "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n" +
                    "                      <h1 style=\"margin: 1rem 0; padding-bottom: 16px\">" + name + ", you have been banned!</h1>\n" +
                    "                      <p style=\"padding-bottom: 4px\"><strong style=\"font-size: 130%\">Reason:</strong></p>\n" +
                    "                       <p style=\"padding-bottom: 16px; color:red;\">" + custom_reason + "</p>" +
                    "                      <p style=\"padding-bottom: 16px\">If you think it was a mistake, please submit a unban request!</p>\n" +
                    "                      <p style=\"padding-bottom: 16px\">Thanks,<br>The AgriGroww team</p>\n" +
                    "                    </div>\n" +
                    "                  </div>\n" +
                    "                  <div style=\"padding-top: 20px; color: rgb(153, 153, 153); text-align: center;\">\n" +
                    "                    <p style=\"padding-bottom: 16px\">Made with \uD83D\uDC96 in India</p>\n" +
                    "                  </div>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "            </tbody>\n" +
                    "          </table>\n" +
                    "        </td>\n" +
                    "      </tr>\n" +
                    "    </tbody>\n" +
                    "  </table>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";

            custom_webView.setWebViewClient(new WebViewClient());
            custom_webView.setWebChromeClient(new WebChromeClient());
            custom_webView.loadDataWithBaseURL(null, htmlEmail, "text/html", "UTF-8", null);

        } else if (whereFrom.equals("ShowUsersAdapter:UnBanUser")) {

            htmlEmail = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "\n" +
                    "<head>\n" +
                    "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "  <title>You have been Unbanned!</title>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n" +
                    "  <table role=\"presentation\"\n" +
                    "    <style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n" +
                    "    <tbody>\n" +
                    "      <tr>\n" +
                    "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n" +
                    "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: left;\">\n" +
                    "            <tbody>\n" +
                    "              <tr>\n" +
                    "                <td style=\"padding: 40px 0px 0px;\">\n" +
                    "                  <div style=\"text-align: left;\">\n" +
                    "                    <div style=\"margin-bottom: -100px; text-align: center; background-color:white;background-color: rgb(255, 255, 255);\"><img src=\"https://i.ibb.co/0qf8xMS/splashlogo.png\" alt=\"AgriGroww\" style=\"width: 80%;\"></div>\n" +
                    "                  </div>\n" +
                    "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n" +
                    "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n" +
                    "                      <h1 style=\"margin: 1rem 0; padding-bottom: 16px\">You have been unbanned!</h1>\n" +
                    "                      <p style=\"padding-bottom: 16px\"><strong style=\"font-size: 110%\">We reviewed you and did`nt find any reason to ban you, sorry for the inconvenience cause.</strong></p>\n" +
                    "                      <p style=\"padding-bottom: 16px\">Thanks,<br>The AgriGroww team</p>\n" +
                    "                    </div>\n" +
                    "                  </div>\n" +
                    "                  <div style=\"padding-top: 20px; color: rgb(153, 153, 153); text-align: center;\">\n" +
                    "                    <p style=\"padding-bottom: 16px\">Made with \uD83D\uDC96 in India</p>\n" +
                    "                  </div>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "            </tbody>\n" +
                    "          </table>\n" +
                    "        </td>\n" +
                    "      </tr>\n" +
                    "    </tbody>\n" +
                    "  </table>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";

            custom_webView.setWebViewClient(new WebViewClient());
            custom_webView.setWebChromeClient(new WebChromeClient());
            custom_webView.loadDataWithBaseURL(null, htmlEmail, "text/html", "UTF-8", null);

        } else if (whereFrom.equals("SentEmailActivity")) {

            String subject, title, body;
            subject = getIntent().getStringExtra("subject");
            title = getIntent().getStringExtra("title");
            body = getIntent().getStringExtra("body");
            if (body.contains("\\n")) {
                body = body.replace("\\n", "<br>");
            }

            htmlEmail = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "\n" +
                    "<head>\n" +
                    "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "  <title>" + subject + "</title>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body style=\"font-family: Helvetica, Arial, sans-serif; margin: 0px; padding: 0px; background-color: #ffffff;\">\n" +
                    "  <table role=\"presentation\"\n" +
                    "    <style=\"width: 100%; border-collapse: collapse; border: 0px; border-spacing: 0px; font-family: Arial, Helvetica, sans-serif; background-color: rgb(239, 239, 239);\">\n" +
                    "    <tbody>\n" +
                    "      <tr>\n" +
                    "        <td align=\"center\" style=\"padding: 1rem 2rem; vertical-align: top; width: 100%;\">\n" +
                    "          <table role=\"presentation\" style=\"max-width: 600px; border-collapse: collapse; border: 0px; border-spacing: 0px; text-align: centre;\">\n" +
                    "            <tbody>\n" +
                    "              <tr>\n" +
                    "                <td style=\"padding: 40px 0px 0px;\">\n" +
                    "                  <div style=\"text-align: left;\">\n" +
                    "                    <div style=\"margin-bottom: -100px; text-align: center; background-color:white; background-color: rgb(255, 255, 255);\"><img src=\"https://i.ibb.co/0qf8xMS/splashlogo.png\" alt=\"AgriGroww\" style=\"width: 80%;\"></div>\n" +
                    "                  </div>\n" +
                    "                  <div style=\"padding: 20px; background-color: rgb(255, 255, 255);\">\n" +
                    "                    <div style=\"color: rgb(0, 0, 0); text-align: left;\">\n" +
                    "                      <h1 style=\"margin: 1rem 0; padding-bottom: 16px\">" + title + "</h1>\n" +
                    "                       <p style=\"padding-bottom: 16px; color:red;\">" + body + "</p>" +
                    "                      <p style=\"padding-bottom: 16px\">Thanks,<br>The AgriGroww team</p>\n" +
                    "                    </div>\n" +
                    "                  </div>\n" +
                    "                  <div style=\"padding-top: 20px; color: rgb(153, 153, 153); text-align: center;\">\n" +
                    "                    <p style=\"padding-bottom: 16px\">Made with \uD83D\uDC96 in India</p>\n" +
                    "                  </div>\n" +
                    "                </td>\n" +
                    "              </tr>\n" +
                    "            </tbody>\n" +
                    "          </table>\n" +
                    "        </td>\n" +
                    "      </tr>\n" +
                    "    </tbody>\n" +
                    "  </table>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";


            custom_webView.setWebViewClient(new WebViewClient());
            custom_webView.setWebChromeClient(new WebChromeClient());
            custom_webView.loadDataWithBaseURL(null, htmlEmail, "text/html", "UTF-8", null);

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}