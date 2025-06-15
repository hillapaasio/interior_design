package interiorDesign.app

import interiorDesign.logic.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.shape.{ArcType, Ellipse, Rectangle}
import scalafx.scene.paint.Color.Red
import scalafx.scene.control.{Alert, Menu, MenuBar, MenuItem, ScrollPane}
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.stage.FileChooser
import scalafx.scene.image.{Image, WritableImage}
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.FileChooser.ExtensionFilter

import java.io.File
import javax.imageio.ImageIO

object Main extends JFXApp3:

  def start() =

    val suunnitelma = Blueprint()

    stage = new JFXApp3.PrimaryStage:
      title = "Interior Design"
      width = 1000
      height = 600

    //leveys ja korkeus mm. ikkunan koon muuttamiseen
    var leveys = stage.width.toInt
    var korkeus = stage.height.toInt

    val canvas = new Canvas(2 * (leveys / 3) - 10, korkeus-60)

    //piirretään aluksi valkoinen tausta
    canvas.graphicsContext2D.fill = Color.White
    canvas.graphicsContext2D.fillRect(0, 0, canvas.width.toInt, canvas.height.toInt)

    //tallennetaan canvasin leveys ja korkeus, kun kuva ladataan, jotta kuvan
    // sijainti ei muutu ikkunan koon muuttuessa, ja huonekalut pysyvät paikoillaan suhteessa kuvaan
    var ogCanvasWidth = 0.0
    var ogCanvasHeight = 0.0

    var valittuKuva: Option[Image] = None

    def piirrä(x: Double, y: Double) =
      // piirretään aina siirron alussa taustakuva, jotta vanha objektin sijainti ei jää näkyviin
      canvas.graphicsContext2D.fill = Color.White
      canvas.graphicsContext2D.fillRect( 0, 0, canvas.width.toInt, canvas.height.toInt)
      valittuKuva match
        case Some(kuva) =>
          //piirretään kuva keskelle
          if kuva.width.toInt < kuva.height.toInt then
            canvas.graphicsContext2D.drawImage(kuva, (ogCanvasWidth-kuva.width.toDouble)/2, 0)
          else
            canvas.graphicsContext2D.drawImage(kuva, 0, (ogCanvasHeight-kuva.height.toDouble)/2)
        case None       => ()
      // siirretään objekteja pohjapiirroksessa
      suunnitelma.dragging(x, y)

      //piirretään ensin alimmat asiat
      for rakenne <- suunnitelma.all.filter(_.onBottom) do
        //punainen väri, jotta käyttäjä tietää asioiden olevan vialla
        if suunnitelma.päällekkäin then
          canvas.graphicsContext2D.fill = Red
        else
          canvas.graphicsContext2D.fill = rakenne.väri
        rakenne match
          case s: RectObject => canvas.graphicsContext2D.fillRect(s.muoto.getX, s.muoto.getY, s.muoto.getWidth, s.muoto.getHeight)
          case e: CircFurniture => canvas.graphicsContext2D.fillOval(e.muoto.getCenterX - (e.muoto.getRadiusX), e.muoto.getCenterY - (e.muoto.getRadiusY), e.muoto.getRadiusX*2, e.muoto.getRadiusY*2)

      // piirretään muut siirretyt objektit
      for rakenne <- suunnitelma.all.filterNot(_.onBottom).filterNot(_.onTop) do
        if suunnitelma.päällekkäin then
          canvas.graphicsContext2D.fill = Red
        else
          canvas.graphicsContext2D.fill = rakenne.väri
        rakenne match
          case d: Door =>
            if d.rotation % 4 == 0 then
              canvas.graphicsContext2D.fillArc(d.muoto.getX-d.muoto.getWidth, d.muoto.getY-d.muoto.getHeight, d.muoto.getWidth*2, d.muoto.getHeight*2, 0, -90, ArcType.Round)
              canvas.graphicsContext2D.strokeArc(d.muoto.getX-d.muoto.getWidth, d.muoto.getY-d.muoto.getHeight, d.muoto.getWidth*2, d.muoto.getHeight*2, 0, -90, ArcType.Round)
            else if d.rotation % 4 == 1 then
              canvas.graphicsContext2D.fillArc(d.muoto.getX, d.muoto.getY-d.muoto.getHeight, d.muoto.getWidth*2, d.muoto.getHeight*2, -90, -90, ArcType.Round)
              canvas.graphicsContext2D.strokeArc(d.muoto.getX, d.muoto.getY-d.muoto.getHeight, d.muoto.getWidth*2, d.muoto.getHeight*2, -90, -90, ArcType.Round)
            else if d.rotation % 4 == 2 then
              canvas.graphicsContext2D.fillArc(d.muoto.getX, d.muoto.getY, d.muoto.getWidth*2, d.muoto.getHeight*2, -180, -90, ArcType.Round)
              canvas.graphicsContext2D.strokeArc(d.muoto.getX, d.muoto.getY, d.muoto.getWidth*2, d.muoto.getHeight*2, -180, -90, ArcType.Round)
            else
              canvas.graphicsContext2D.fillArc(d.muoto.getX-d.muoto.getWidth, d.muoto.getY, d.muoto.getWidth*2, d.muoto.getHeight*2, -270, -90, ArcType.Round)
              canvas.graphicsContext2D.strokeArc(d.muoto.getX-d.muoto.getWidth, d.muoto.getY, d.muoto.getWidth*2, d.muoto.getHeight*2, -270, -90, ArcType.Round)

          case s: RectObject => canvas.graphicsContext2D.fillRect(s.muoto.getX, s.muoto.getY, s.muoto.getWidth, s.muoto.getHeight)
          case e: CircFurniture => canvas.graphicsContext2D.fillOval(e.muoto.getCenterX - (e.muoto.getRadiusX), e.muoto.getCenterY - (e.muoto.getRadiusY), e.muoto.getRadiusX*2, e.muoto.getRadiusY*2)

      //lopuksi piirretään vielä kaikki päällä olevat
      for rakenne <- suunnitelma.all.filter(_.onTop) do
        if suunnitelma.päällekkäin then
          canvas.graphicsContext2D.fill = Red
        else
          canvas.graphicsContext2D.fill = rakenne.väri
        rakenne match
          case s: RectObject => canvas.graphicsContext2D.fillRect(s.muoto.getX, s.muoto.getY, s.muoto.getWidth, s.muoto.getHeight)
          case e: CircFurniture => canvas.graphicsContext2D.fillOval(e.muoto.getCenterX - (e.muoto.getRadiusX), e.muoto.getCenterY - (e.muoto.getRadiusY), e.muoto.getRadiusX*2, e.muoto.getRadiusY*2)
    end piirrä



    //valitaan kuva, jos sellainen on
    def makeImage(tiedosto: File) =
      try
        //otetaan talteen canvasin leveys ja korkeus, kun kuva avataan
        ogCanvasWidth = canvas.width.toDouble
        ogCanvasHeight = canvas.height.toDouble
        var kuvana = new Image(tiedosto.toURI.toString, canvas.width.toDouble, 0, true, true)
        //testataan onko valittu kuva, eli onko tiedostolla luodulla kuvalla korkeus
        if kuvana.height.toInt > 0 then
          //sovitetaan kuva näytölle sen mukaan, onko se leveämpi vai korkeampi
          if kuvana.width.toInt < kuvana.height.toInt then
            kuvana = new Image(tiedosto.toURI.toString, 0, canvas.height.toDouble, true, true)
          valittuKuva = Some(kuvana)
        else
          //kerrotaan käyttäjälle, että tiedosto on vääränlainen
          new Alert(AlertType.Information) {
          initOwner(stage)
          title = "Alert"
          contentText = "Wrong type of file" }.showAndWait()
      catch
        //ei avata mitään tiedostoa
        case n: NullPointerException => ()

    //tallennetaan canvasin kuva
    def saveImage() =
      val saveFile = new FileChooser
      saveFile.setTitle("Save File")
      saveFile.extensionFilters.add(ExtensionFilter("jpg and png", Seq("*.jpg", "*.png")))

      val file = saveFile.showSaveDialog(stage)

      //jos tiedosto on valittu
      if file != null then
        val newImage: WritableImage = canvas.snapshot(null, null)
        val bufferedImage = SwingFXUtils.fromFXImage(newImage, null)
        ImageIO.write(bufferedImage, "png", file)

    val dialogit = DialogMatching(suunnitelma, piirrä)

    val fileChooser = new FileChooser:
      title = "open image file"

    //luodaan yläpalkin menu

    val avaaTiedosto = new MenuItem("Open image"):
      onAction = (event) =>
        makeImage(fileChooser.showOpenDialog(stage))
        piirrä(-10, -10)

    val tyhjäKuva = new MenuItem("Empty image"):
      onAction = (event) =>
        valittuKuva = None
        piirrä(-10, -10)

    val tyhjätObjektit = new MenuItem("Empty items"):
      onAction = (event) =>
        suunnitelma.all = suunnitelma.all.empty
        piirrä(-10, -10)

    val tyhjennä = new Menu("Empty"):
      items = Array(tyhjäKuva, tyhjätObjektit)

    val tallenna = new MenuItem("Save image"):
      onAction = (event) => saveImage()

    val fileMenu = new Menu("File"):
      items = Array(avaaTiedosto, tallenna)

    val skaalaa = new MenuItem("Scale"):
      onAction = (event) => dialogit.scaleDialog()

    val skaalausMenu = new Menu("Scaling"):
      items = Array[MenuItem](skaalaa)

    val help = new MenuItem("Help"):
      onAction = (event) => dialogit.helpAlert()

    val helpMenu = new Menu("Help"):
      items = Array[MenuItem](help)

    val top = new MenuBar:
      menus = Array(fileMenu, tyhjennä, skaalausMenu, helpMenu)


    val tavaraValikko = SideBar(leveys, suunnitelma, piirrä)

    val valikko = ScrollPane()
    valikko.setPrefSize(tavaraValikko.w, korkeus - 60)
    valikko.content = tavaraValikko

    //päänäkymä
    val bottom = new HBox:
      children = Array(canvas, valikko)
      onMouseDragged = (event) =>
        //piirretään kuva perustuen hiiren koordinaatteihin
        piirrä(event.getX, event.getY)
      onMousePressed = (event) =>
        //valitaan tietty objekti
        suunnitelma.valitse(event.getX, event.getY)
        //otetaan alkupiste talteen, jotta voidaan teleportata objekti takaisin, jos liike on laiton
        suunnitelma.muutaAlku()
        //hiiren oikealla nappulalla avataan huonekalumenu
        if event.getButton.toString == "SECONDARY" then dialogit.huonekaluDialogi()
      //jos uusi asia on jonkin päällä, teleportataan takaisin alkuun
      onMouseReleased = (event) => if suunnitelma.päällekkäin then piirrä(suunnitelma.alkuX, suunnitelma.alkuY)

    //ikkunan kokoa voi muuttaa
    stage.heightProperty().addListener((_, _, newVal) => korkeus = newVal.intValue)
    stage.heightProperty().addListener((_, _, _) => canvas.setHeight(korkeus-60))
    stage.heightProperty().addListener((_, _, _) => piirrä(-10, -10))

    stage.widthProperty().addListener((_, _, newVal) => leveys = newVal.intValue)
    stage.widthProperty().addListener((_, _, _) => canvas.setWidth(leveys-tavaraValikko.w-10))
    stage.widthProperty().addListener((_, _, _) => piirrä(-10, -10))

    val root = new VBox:
      children = Array(top, bottom)

    val scene = Scene(parent = root)
    stage.scene = scene

  end start

end Main

