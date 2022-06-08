#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>

const char* ssid = "TcpToSerialServer";
//const char* password = "";

ESP8266WebServer server(80);

void handleNotFound() {
  String message = "File Not Found\n\n";
  message += "URI: ";
  message += server.uri();
  message += "\nMethod: ";
  message += (server.method() == HTTP_GET) ? "GET" : "POST";
  message += "\nArguments: ";
  message += server.args();
  message += "\n";
  for (uint8_t i = 0; i < server.args(); i++) {
    message += " " + server.argName(i) + ": " + server.arg(i) + "\n";
  }
  server.send(404, "text/plain", message);
  Serial.println(message);
}

void handleCommDownloadAll() {
  String odpowiedz = "MSG:Pozdrawiam z esp, moj czas od wlaczenia: " + String(millis());
  char odpowiedz_downgrade[odpowiedz.length()];
  odpowiedz.toCharArray(odpowiedz_downgrade,odpowiedz.length());

  server.send(200, "text/plain" ,odpowiedz_downgrade);
  Serial.println(odpowiedz_downgrade);
}

void handleRoot() {
  server.send(200, "text/html", "oops root responsem, method: "+server.method() );
}

void setup() {
  Serial.begin(38400);
  Serial.println("Zaczynam");
  
  delay(3000);

  // w przypadku resetu programowego,zeby wifi tez zresetowalo
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  delay(100);

  // if we want other than default 192.168.4.1
  IPAddress localIp(192,168,0,1);
  IPAddress gateway(192,168,0,1);
  IPAddress subnet(255,255,255,0);


  WiFi.softAPConfig(localIp,gateway,subnet);
  
  // WiFi.softAp(ssid,password,channel,hidden)
  WiFi.softAP(ssid);
  IPAddress myIP = WiFi.softAPIP();
  Serial.println("AP stworzone");
  Serial.print("Moje IP: ");
  Serial.print(myIP);
  Serial.println();

  server.on("/", handleRoot);
  server.on("/CommDownloadAll",handleCommDownloadAll);
  server.onNotFound(handleNotFound);
  
  server.begin();
}

void loop() {
  server.handleClient();
}
