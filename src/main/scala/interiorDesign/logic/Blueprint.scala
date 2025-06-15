package interiorDesign.logic

import scala.collection.mutable.Buffer
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.{Rectangle, Shape}

//luokka, jotta voidaan käyttää muiden luokkien konstruktoriparametrina
class Blueprint:
  //kaikki objektit säilötään samaan puskuriin
  var all = Buffer[Draggable]()

  //suunnitelman skaala
  var skaala = 1.0

  //valitaan aina objekti, joka on hiiren alla tai placeholder olio
  var valittu: Draggable = RectObject(Black, -10, -10, 0, 0)

  //valitaan vain yksi objekti siirrettäväksi
  def valitse(x: Double, y:Double) =
    if x >= 0 && y >= 0 then
      //käydään ensin läpi matot, sitten muut ja sitten lamput, jotta ylin tulee valituksi
      for rakenne <- all.filter(_.isInstanceOf[RectCarpet]) ++ all.filter(_.isInstanceOf[CircCarpet]) do
        if rakenne.muoto.contains(x,y) then
          valittu = rakenne
      for rakenne <- all.filterNot(_.isInstanceOf[RectCarpet]).filterNot(_.isInstanceOf[CircCarpet]) do
        if rakenne.muoto.contains(x,y) then
          valittu = rakenne
      for rakenne <- all.filter(_.isInstanceOf[RectLamp]) ++ all.filter(_.isInstanceOf[CircLamp]) do
        if rakenne.muoto.contains(x,y) then
          valittu = rakenne
    // jos hiiri ei ole minkään objektin päällä, käytetään "tyhjää" objektia
    if !all.map(_.muoto).exists(_.contains(x,y)) then
      valittu = RectObject(Black, -10, -10, 0, 0)

  //muistetaan dragin aloituksen koordinaatit
  var alkuX = 0.0
  var alkuY = 0.0

  //valitaan tietty sijainti, kun hiiri painetaan pohjaan
  def muutaAlku() =
    alkuX = valittu.muoto.getBoundsInLocal.getCenterX
    alkuY = valittu.muoto.getBoundsInLocal.getCenterY


  // tehdään vähän pienemmät näkymättömät suorakulmiot kuvioiden alle, jotta kuviot voivat koskea toisiinsa olematta päällekkäin
  private def pienemmät(tavarat: Buffer[Draggable]): Buffer[Shape] = tavarat.map(_.muoto).map( sh => Rectangle(sh.getBoundsInLocal.getMinX + 0.5, sh.getBoundsInLocal.getMinY + 0.5, sh.getBoundsInLocal.getWidth - 1, sh.getBoundsInLocal.getHeight - 1))

  // tehdään näkymättömät pienemmät suorakulmiot rakenteiden ja valitun alle
  //private def rakenteetJaItse = pienemmät(all.filter(_.isInstanceOf[Structure]) ++ Buffer(valittu))

  var päällekkäin = false

  def dragging(x: Double, y: Double) =
    //liikutetaan objektia
    valittu.drag(x,y)
    var suorakulmiot = pienemmät(all)
    valittu match
      case l: CircLamp =>
        //otetaan vain rakenteet, sekä lamput, koska lamput eivät mene päällekkäin
        suorakulmiot = pienemmät(all.filter(_.isInstanceOf[Structure]) ++ all.filter(_.isInstanceOf[RectLamp]) ++ all.filter(_.isInstanceOf[CircLamp]))
      case r: RectLamp =>
        suorakulmiot = pienemmät(all.filter(_.isInstanceOf[Structure]) ++ all.filter(_.isInstanceOf[RectLamp]) ++ all.filter(_.isInstanceOf[CircLamp]))
      case c: RectCarpet =>
        //otetaan vain rakenteet ja itse objekti, sillä matot voivat mennä päällekkäin
        suorakulmiot = pienemmät(all.filter(_.isInstanceOf[Structure]) ++ Buffer(valittu))
      case s: CircCarpet =>
        suorakulmiot = pienemmät(all.filter(_.isInstanceOf[Structure]) ++ Buffer(valittu))
      case s: Structure => ()
      case _ =>
        //otetaan vaan ei päällä tai alla olevat
        suorakulmiot = pienemmät(all.filterNot(_.onTop).filterNot(_.onBottom))
    //katsotaan meneekö mikään suorakulmioista valitun päälle (koska listassa on itse objekti mukana, listassa aina yksi objekti menee päällekkäin)
    if suorakulmiot.map(_.intersects(valittu.muoto.getBoundsInLocal)).count(_ == true) <= 1 then
      päällekkäin = false
    else
      päällekkäin = true

