package interiorDesign.app

import interiorDesign.logic.Draggable
import scalafx.geometry.Insets
import scalafx.scene.control.{ButtonType, ColorPicker, Dialog, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color.{Aquamarine, Black, LightGreen, Red, White, Yellow}
import scalafx.Includes.jfxColor2sfx
import scalafx.scene.paint.Color
import interiorDesign.app.Main.stage

//samanlainen kuin furniture dialog, mutta objektin lisäämiselle (kuvaavammat kommentit furnitrueDialogissa)
class AddingDialog(tyyppi: String) extends Dialog[Draggable]():
  initOwner(stage)
  title = "Adding an item"
  
  this.getDialogPane.getButtonTypes.add(ButtonType.Apply)
  this.getDialogPane.getButtonTypes.add(ButtonType.Cancel)

  private val applyButton = this.dialogPane().lookupButton(ButtonType.Apply)

  private val leveys = new TextField():
    promptText = "Width"
    text = 100.toString

  private val korkeus = new TextField():
    promptText = "Height"
    //jos on ikkuna tai seinä, tehdään automaattisesti ohuempi
    text =
      tyyppi match
        case "rakenne" => 10.toString
        case "ikkuna" => 10.toString
        case _ => 100.toString

  //varoitus jaetaan kahteen osaan, jotta mahtuu gridiin
  private val warning1 = new Label("Width and height")
  private val warning2 = new Label(" are valid")

  private var validWidth = true
  private var validHeight = true

  private def check(text: String): Boolean =
    if text.isEmpty then false
    else
      try
        if text.toInt > 0 then true; else false
      catch
        case n: NumberFormatException => false

  private def enable() =
    if validWidth && validHeight then
      applyButton.setDisable(false)
      warning2.setTextFill(Black)
      warning2.setText(" are valid")
    else
      applyButton.setDisable(true)
      warning2.setTextFill(Red)
      warning2.setText(" must be positive numbers")

  leveys.textProperty().addListener((_, _, newValue) => ( validWidth = check(newValue)))
  leveys.textProperty().addListener((_, _, newValue) => ( enable() ))
  korkeus.textProperty().addListener((_, _, newValue) => ( validHeight = check(newValue)))
  korkeus.textProperty().addListener((_, _, newValue) => ( enable() ))




  //oletusväri
  private val väri =
    tyyppi match
      case "rakenne" => Black
      case "ellipsi" => LightGreen
      case "ovi" => White
      case "lamppu" => Yellow
      case "suorakulmio" => Aquamarine
      case "ikkuna" => Color.web("#b3e6e6")
      case "suoralamppu" => Yellow
      case "ympyrämatto" => Color.web("#ff8080")
      case "suoramatto" => Color.web("#994d66")
      case _ => Black

  private val väriValitsin = new ColorPicker(väri)

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

  this.dialogPane().setContent(grid)
  
  this.resultConverter = dialogButton =>
  if (dialogButton == ButtonType.Apply) then
    Draggable(väriValitsin.getValue, 0, 0, leveys.text().toInt, korkeus.text().toInt)
  else
    null
