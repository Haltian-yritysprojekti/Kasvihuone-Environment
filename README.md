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

------------------------------
# Android-osio
All icons used were made by Icons8

## Tarvittavat asiat

- Android Studio
- Jotta puhelimeen voi asentaa sovellus, puhelimen on oltava debug-moodissa ja usb-yhteydessä tietokoneeseen. Sovellus asentuu kun se ajetaan Android Studion sisällä.
- Sovelluksen asennuksen jälkeen sovellusta voi käyttää kuten normaalia sovellusta.

## Teknologiat
- Kotlin-kieli

## Käytetyt android-kirjastot

- volley
- widget
- androidx
- Handler
- java
- Intent

## Toiminta 
- Sovellus käynnistyy päänäkymään, MainActivity, josta voidaan siirtyä kuuteen eri aktiviteettiin. Aktiviteetteja ovat FloraActivity, AddFlora, TempOT, HumdOT ja VisitorActivity. Aktiviteettiin PlantChange pääsee AddFloran sisältä. Taustalla toimii myös DoorStatusService, joka selvittää minuutin välein oven asentoa. Päänäkymän ovikuva muuttuu oven asennon mukaan. Minuutin välein päänäkymä päivittää itsensä.

  <img src="https://github.com/Haltian-yritysprojekti/Kasvihuone-Environment/assets/79058877/6f1da9a6-5181-46f4-ad00-3a312306c529" width="270" height="585">
  
- Käyttäjän valitessa FloraActivityn, hän näkee seuraavan kuvan. Hän voi valita kasvien väliltä napeilla, tai siirtyä aktiviteettiin AddFlora tai poistaa nykyisen kukan punaisella ruksilla.

  <img src="https://github.com/Haltian-yritysprojekti/Kasvihuone-Environment/assets/79058877/e3cfe2cd-191f-4a1c-a12a-ab6bb0e43b07" width="270" height="585">

- Poistaessa kukan näet tämän ikkunan, voit joko palata aikaisempaan näkymään tai poistaa kukan.

  <img src="https://github.com/Haltian-yritysprojekti/Kasvihuone-Environment/assets/79058877/aae51c6f-2a44-4688-837f-00e261be2d50" width="270" height="585">

- Käyttäjän siirtyessä AddPlant aktiviteettiin, hän voi täyttää kentät jotta uusi kasvi voidaan lisätä tietokantaan.

  <img src="https://github.com/Haltian-yritysprojekti/Kasvihuone-Environment/assets/79058877/194b7851-79b8-418c-829d-56566724e26a" width="270" height="585">
  
- AddPlant aktiviteetin sisältä voit siirtyä PlantChange aktiviteettiin, ja vaihtaa kasvitietoja.

  <img src="https://github.com/Haltian-yritysprojekti/Kasvihuone-Environment/assets/79058877/249ffafb-e806-4557-848a-d5e8665a59f9" width="270" height="585">

- TempOT, Lämpötilatiedot ilmoitettuna ajan funktiona. Ensimmäisessä kuvassa listataan päivät, jolloin on tullut lämpötiladataa. Jokainen päivä on klikattavissa.

  <img src="https://github.com/Haltian-yritysprojekti/Kasvihuone-Environment/assets/79058877/7e7edfe4-5241-420d-99b4-baa9d2034dcf" width="270" height="585">

- TempOT, Lämpötilatiedot klikkauksen jälkeen ilmoitettuna kuvaajalla. Palata voi palaa-napilla päivälistaukseen. Napilla poistutaan päänäkymään.

<img src="https://github.com/Haltian-yritysprojekti/Kasvihuone-Environment/assets/79058877/6fbe4729-68df-4947-b48a-daa3c7a6f23d" width="270" height="585">

- HumdOT, Kosteustiedot ilmoitettuna ajan funktiona. Ensimmäisessä kuvassa listataan päivät, jolloin on tullut kosteusdataa. Jokainen päivä on klikattavissa.

<img src="https://github.com/Haltian-yritysprojekti/Kasvihuone-Environment/assets/79058877/b706756a-a167-4ffb-a5d1-8efe24f2c8e2" width="270" height="585">

- HumdOT, Kosteustiedot klikkauksen jälkeen ilmoitettuna kuvaajalla. Palata voi palaa-napilla päivälistaukseen. Napilla poistutaan päänäkymään.

<img src="https://github.com/Haltian-yritysprojekti/Kasvihuone-Environment/assets/79058877/db03f593-7e50-4b88-b501-781793781af5" width="270" height="585">

- VisitorActivity, Listattuna käyntipäivät ja kävijämäärät. Napilla palataan MainActivity-näkymään.

<img src="https://github.com/Haltian-yritysprojekti/Kasvihuone-Environment/assets/79058877/74f995e8-a59b-4e3a-904f-421fa5b912b8" width="270" height="585">

## Tekijä

GitHub: https://github.com/LassiTihinen
