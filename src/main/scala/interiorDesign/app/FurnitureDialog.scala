package interiorDesign.app

import scalafx.scene.control.{Button, ButtonType, ChoiceBox, ColorPicker, Dialog, Label, TextField}
import interiorDesign.logic.*
import scalafx.geometry.Insets
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color.{Black, Red}
import scalafx.Includes.jfxColor2sfx
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.ButtonBar.ButtonData
import interiorDesign.app.Main.stage

//info dialogi esineille
class FurnitureDialog(kaluste: Draggable) extends Dialog[Draggable]():
  initOwner(stage)
  title =
    kaluste match
      case s: Structure => "Structure info"
      case d: Door => "Structure info"
      case _ => "Furniture info"

  //napit
  this.getDialogPane.getButtonTypes.add(ButtonType.Apply)
  val deleteButtonType = new ButtonType("Delete", ButtonData.Other)
  this.getDialogPane.getButtonTypes.add(deleteButtonType)
  val closeButtonType = new ButtonType("Close", ButtonData.CancelClose)
  this.getDialogPane.getButtonTypes.add(closeButtonType)

  // jotta voidaan laittaa nappi pois päältä
  private val applyButton = this.dialogPane().lookupButton(ButtonType.Apply)

  private val leveys = new TextField():
    promptText = kaluste.oikeaLeveys.toString
    text = kaluste.oikeaLeveys.toString

  private val korkeus = new TextField():
    promptText = kaluste.oikeaKorkeus.toString
    text = kaluste.oikeaKorkeus.toString

  private var validWidth = true
  private var validHeight = true

  private def check(text: String): Boolean =
    if text.isEmpty then false
    else
      try
        if text.toInt > 0 then true
        else false
      catch
        case n: NumberFormatException => false

  private def enable() =
    //jos oikeat inputit, apply nappi päälle ja tekstin vaihto
    if validWidth && validHeight then
      applyButton.setDisable(false)
      warning2.setTextFill(Black)
      warning2.setText(" are valid")
    else
      applyButton.setDisable(true)
      warning2.setTextFill(Red)
      warning2.setText(" must be positive numbers")

  //kuuntelijat tekstin muuttumiselle
  leveys.textProperty().addListener((_, _, newValue) => ( validWidth = check(newValue)))
  leveys.textProperty().addListener((_, _, newValue) => ( enable() ))
  korkeus.textProperty().addListener((_, _, newValue) => ( validHeight = check(newValue)))
  korkeus.textProperty().addListener((_, _, newValue) => ( enable() ))

  //varoitus jaetaan kahteen osaan, jotta mahtuu gridiin
  private val warning1 = new Label("Width and height")
  private val warning2 = new Label(" are valid")

  private val väriValitsin = new ColorPicker(kaluste.väri)

  private var painallukset = 0

  private val käännin = new Button("rotate"):
    onAction = (event) =>
      painallukset += 1
      //vaihdetaan leveyen ja korkeuden paikkaa
      val muisti = leveys.text()
      leveys.text = korkeus.text()
      korkeus.text = muisti

  private val materiaalit = new ChoiceBox[String]:
    items = ObservableBuffer("Wood", "Stone", "Glass", "Metal", "Textile", "Leather")
    value = kaluste.materiaali
    onAction = (event) => kaluste.materiaali = this.value.value



  private val grid = new GridPane():
    hgap = 10
    vgap = 10
    padding = Insets(20, 100, 10, 10)

    add(new Label("Width (cm):"), 0, 0)
    add(leveys, 1, 0)
    add(new Label("Height (cm):"), 0, 1)
    add(korkeus, 1, 1)
    add(warning1, 0, 2)
    add(warning2, 1, 2)
    add(new Label("Color:"), 0, 3)
    add(väriValitsin, 1, 3)
    add(new Label("Rotate:"),0,4)
    add(käännin,1,4)
    //materiaali vain huonekaluille ja oville
    kaluste match
      case s: Structure => ()
      case _ =>
        add(new Label("Material:"), 0, 5)
        add(materiaalit, 1, 5)


  this.dialogPane().setContent(grid)

  //jos hyväksytään, niin sitten muutokset
  //draggable oliota käytetään tiedon varastona
  this.resultConverter = dialogButton =>
    if (dialogButton == ButtonType.Apply) then
      Draggable(väriValitsin.getValue, 0, painallukset, leveys.text().toInt, korkeus.text().toInt)
    else if dialogButton == deleteButtonType then
      Draggable(Black, -100, -100, 0, 0)
    else
      null




