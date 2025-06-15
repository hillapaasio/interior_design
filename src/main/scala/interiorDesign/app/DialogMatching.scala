package interiorDesign.app

import interiorDesign.app.Main.stage
import interiorDesign.logic.{Blueprint, Draggable}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, TextInputDialog}
import scalafx.scene.shape.{Ellipse, Rectangle}

//dialogien käsittelyä
class DialogMatching(suunnitelma: Blueprint, piirrä: (Double,Double) => (Unit)):

  //käsitellään huonekaludialogin palauttamaa arvoa
  def huonekaluDialogi() =
    //jos on valittu joku oikea objekti, ei pseudo
    if suunnitelma.valittu.a >= 0 then
      var result = FurnitureDialog(suunnitelma.valittu).showAndWait()
      result match
        case Some(Draggable(väri, a, b, c, d)) =>
          //jos on painettu delete
          if a < 0 then
            suunnitelma.all = suunnitelma.all.filterNot(_ == suunnitelma.valittu)
          else
            suunnitelma.valittu.oikeaLeveys = c
            suunnitelma.valittu.oikeaKorkeus = d
            suunnitelma.valittu.muoto match
              case rect: Rectangle =>
                rect.width = (c * suunnitelma.skaala).toInt
                rect.height = (d * suunnitelma.skaala).toInt
              case ellip: Ellipse =>
                ellip.radiusX = ((c * suunnitelma.skaala)/2).toInt
                ellip.radiusY = ((d * suunnitelma.skaala)/2).toInt
              case _ => ()
            suunnitelma.valittu.väri = väri
            suunnitelma.valittu.rotation += b
          piirrä(-10,-10)
        case _ => ()

  //skaalaus dialogi ja tuloksen käsittely
  def scaleDialog() =
    val prosentteina = suunnitelma.skaala * 100

    val dialog = new TextInputDialog(defaultValue = prosentteina.toString):
      initOwner(stage)
      title = "Object scaling"
      headerText = "Scale the measurements in percentage (%)"
      contentText = "input scaling number:"

    val result = dialog.showAndWait()

    //alert, jossa kerrotaan väärästä inputista
    def wrongInput() =
      new Alert(AlertType.Information) {
              initOwner(stage)
              title = "Wrong input"
              contentText = "The scale must be a positive number"
            }.showAndWait()

    result match
      case Some(scale) =>
        //testataan onko input oikeanlainen, eli positiivinen numero
        try
          if scale.toDouble > 0 then
            suunnitelma.skaala = scale.toDouble / 100
            suunnitelma.all.foreach(
            obj =>
              obj.muoto match
                case r: Rectangle =>
                  r.height = obj.oikeaKorkeus.toDouble * suunnitelma.skaala
                  r.width = obj.oikeaLeveys.toDouble * suunnitelma.skaala
                case e: Ellipse =>
                  e.radiusX = (obj.oikeaLeveys.toDouble/2) * suunnitelma.skaala
                  e.radiusY = (obj.oikeaKorkeus.toDouble/2) * suunnitelma.skaala
            )
            piirrä(-10, -10)
          else 
            wrongInput()

        catch
          case n: NumberFormatException => wrongInput()
      case None       => ()



  //apukomento
  def helpAlert() =
    new Alert(AlertType.Information) {
          initOwner(stage)
          title = "Help"
          contentText =
            "Add items from the right-hand side menu by clicking them.\n" +
              "The menu can be scrolled to find more items.\n" +
              "Drag the added items around in the left-hand side view.\n" +
              "Objects may not intersect, except lamps, which can be on top\n" +
              "of furniture, and carpets, which can be under furniture. \n" +
              "To edit an added item or to see information about it,\n" +
              "right click it.\n" +
              "You can open an image file to depict a floorplan\n" +
              "from the menu above. You can also clear the view or\n" +
              "save the current image" +
              "If you want to scale your objects to fit a\n" +
              "floorplan. The easiest way to do so\n" +
              "is to add a 1 meter long object and then open the scaling menu\n" +
              "from above. Then find a fitting number for the scaling, so that\n" +
              "the object is the size you want it to be.\n" +
              "Scaling should be done before adding more items.\n"
        }.showAndWait()
