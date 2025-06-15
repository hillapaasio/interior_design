package interiorDesign.app

import interiorDesign.logic.*
import scalafx.geometry.Insets
import scalafx.scene.control.{Label, Separator}
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Aquamarine, Black, White, Yellow}
import scalafx.scene.shape.{Arc, ArcType, Ellipse, Rectangle}
import scalafx.scene.text.{Font, FontWeight}

class SideBar(leveys: Int, s: Blueprint, piirrä: (Double,Double) => (Unit)) extends VBox:

  //def, koska skaala muuttuu
  private def skaala = s.skaala

  private def mitatDialogi(tyyppi: String) =
    val result = AddingDialog(tyyppi).showAndWait()
    result match
      case Some(Draggable(väri, a, b, c, d)) =>
        tyyppi match
          case "rakenne" | "ikkuna" =>
            s.all += Structure(väri, 0, 0, (c * skaala).toInt, (d*skaala).toInt)
          case "ellipsi" =>
            s.all += CircFurniture(väri, (c*skaala/2).toInt , (d*skaala/2).toInt, (c*skaala).toInt, (d*skaala).toInt)
          case "ovi" =>
            s.all += Door(väri, 0, 0, (c*skaala).toInt, (d*skaala).toInt)
          case "lamppu" =>
            s.all += CircLamp(väri, (c*skaala/2).toInt , (d*skaala/2).toInt, (c*skaala).toInt, (d*skaala).toInt)
          case "suoralamppu" =>
            s.all += RectLamp(väri, 0, 0, (c*skaala).toInt, (d*skaala).toInt)
          case "suorakulmio" =>
            s.all += RectObject(väri, 0, 0, (c*skaala).toInt, (d*skaala).toInt)
          case "suoramatto" =>
            s.all += RectCarpet(väri, 0, 0, (c*skaala).toInt, (d*skaala).toInt)
          case "ympyrämatto" =>
            s.all += CircCarpet(väri, (c*skaala/2).toInt , (d*skaala/2).toInt, (c*skaala).toInt, (d*skaala).toInt)

        s.all.last.oikeaLeveys = c
        s.all.last.oikeaKorkeus = d
      case _ => ()
    piirrä(-10, -10)


  // valikon leveys
  val w = leveys / 3

  //valikon objektit

  private val rakenne = new Rectangle:
    var tyyppi = "rakenne"
    width = (w * 3) / 5
    height = 30
    onMouseClicked = (event) =>
      mitatDialogi(tyyppi)

  private val ikkuna = new Rectangle:
    var tyyppi = "ikkuna"
    width = (w * 3) / 5
    height = 30
    fill = Color.web("#b3e6e6")
    onMouseClicked = (event) =>
      mitatDialogi(tyyppi)


  private val ellipsi = new Ellipse:
    val tyyppi = "ellipsi"
    radiusX = ((w * 3) / 5) /2
    radiusY = 50
    fill = Color.LightGreen
    onMouseClicked = (event) =>
      mitatDialogi(tyyppi)

  private val kurvi = new Arc:
    val tyyppi = "ovi"
    length = 90
    radiusX = 100
    radiusY = 100
    startAngle = 0
    `type` = ArcType.Round
    fill = White
    stroke = Black
    onMouseClicked = (event) =>
      mitatDialogi(tyyppi)

  private val lamppu = new Ellipse:
    val tyyppi = "lamppu"
    radiusX = 50
    radiusY = 50
    fill = Yellow
    onMouseClicked = (event) =>
      mitatDialogi(tyyppi)

  private val suoraLamppu = new Rectangle:
    val tyyppi = "suoralamppu"
    width = 100
    height = 100
    fill = Yellow
    onMouseClicked = (event) =>
      mitatDialogi(tyyppi)

  private val suorakulmio = new Rectangle:
    val tyyppi = "suorakulmio"
    width = (w * 3) / 5
    height = 80
    fill = Aquamarine
    onMouseClicked = (event) =>
      mitatDialogi(tyyppi)

  private val suoraMatto = new Rectangle:
    val tyyppi = "suoramatto"
    width = (w * 3) / 5
    height = 80
    fill = Color.web("#994d66")
    onMouseClicked = (event) =>
      mitatDialogi(tyyppi)

  private val ympyräMatto = new Ellipse:
    val tyyppi = "ympyrämatto"
    radiusX = 50
    radiusY = 50
    fill = Color.web("#ff8080")
    onMouseClicked = (event) =>
      mitatDialogi(tyyppi)

  private val structures = new Label("Structures: "):
    font = Font("Verdana", FontWeight.Bold, 20)

  private val furniture = new Label("Furniture: "):
    font = Font("Verdana", FontWeight.Bold, 20)

  this.children = Array(structures, new Label("Wall:"), rakenne, new Label("Window:"), ikkuna, new Label("Door:"), kurvi, new Separator, furniture, new Label("Rectangular furniture:"), suorakulmio, new Label("Elliptical furniture:"), ellipsi, new Label("Lamps:"), lamppu, suoraLamppu, new Label("Carpets:"), suoraMatto, ympyräMatto)
  this.padding = Insets.apply(w / 5 - 10)
  this.spacing = 40

