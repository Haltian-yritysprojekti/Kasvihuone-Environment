# Kasvihuone-Environment

------------------------------
# Rest API osio

Rest API kansiossa MQTT Client hakee sensorien datat, cloud funktiot jotka hakevat ja muokkaavat tietokannan tiedot ja SQL model tauluista johon tiedot tallennetaan.

## Asennus

1. Kloonaa repositorio

    git clone https://github.com/Haltian-yritysprojekti/Kasvihuone-Environment.git

2. Asenna tarvittavat riippuvuudet

    - siirry REST API kansioon
    - npm install
    - vaihda mqtt tiedot omiin tietoihin
    - luo db.json file, jossa tietokannan tiedot

3. Luo MySQL tietokanta lokaalisti tai pilveen ja käytä Kasvihuone kaaviota luodaksesi tietokannan taulut

4. Luo Node.js zip file jossa asennettuna mysql, mime ja ibm-cos-sdk paketit
   
5. Luo AWS Lambda funktiot zip filen avulla ja lisää funktioihin index.js file johon lisäät halutun funktion toiminnallisuuden

6. Lisää Lambda funktioille Environment variableihin:
   - **tietokannan tiedot**
     - databaseHost
     - user
     - password
     - database
   - **IBM Storage tiedot**
     - endpoint
     - apikey
     - serviceInstance
     - bucketName

7. Avaa Lambda funktioiden url julkiseksi
    
## Käyttö

1. Käynnistäminen

    - aja tietokanta cloudissa tai locaalisti
    - aja MQTT client cloudissa tai locaalisti komennolla node index.js

2. Testaa AWS Lambda funktiot selaimessa tai POSTMAN sovelluksessa

## Toiminnot

- Kontrolloi tietokantaa POSTMAN pyyntöjen avulla
- Hae Environment sensorin lämpötila, kosteus, valaistus, ilmanpaine, oven tiedot
- Hae kosteuden tiedot päivittäisessä ja ajan kulun mukaan
- Hae lämpötilan tiedot päivittäisessä ja ajan kulun mukaan
- Hae vierailijoiden tiedot tiettyinä päivinä
- Hae kasvien tiedot
- Lisää kasvi tietokantaan
- Muuta kasvin tietoja
- Poista kasvi tietokannasta

## Teknologiat

- JavaScript
- Node.js
- SQL
- IBM Cloud Bucket
- AWS RDS MySQL tietokanta
- AWS Lambda funktiot

## Käytetyt Node.js kirjastot

- MQTT
- Mysql
- Mime
- IBM-COS-SDK

## Kehitysympäristö

- Node.js versio 14.15.4
- Npm versio 6.14.10

## Tekijä

GitHub: https://github.com/RamM21
